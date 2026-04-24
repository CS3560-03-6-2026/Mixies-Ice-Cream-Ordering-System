import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Data Access Object class for Flavor
    Handles all database operations related to ice cream flavors
*/
public class FlavorDAO {

    /** Retrieves ice cream flavors from the database
        @return list of IceCreamFlavor objects in a list
    */
    public List<IceCreamFlavor> getAllFlavors() {
        List<IceCreamFlavor> flavors = new ArrayList<>();
        String sql = "SELECT * FROM IceCreamFlavor ORDER BY flavorName";

        // Try with resources closes connection, prepared statement, and result set
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            // Create an IceCreamFlavor for object for each row
            while (rs.next()) {
                flavors.add(new IceCreamFlavor(
                        rs.getInt("flavorID"),
                        rs.getString("flavorName"),
                        rs.getString("seasonality"),
                        rs.getInt("stockLevel"),
                        rs.getInt("remakeThreshold"),
                        rs.getString("allergens"),
                        rs.getString("availabilityStatus")));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print error
        }

        return flavors;
    }

    /** Retrieves ice cream flavor from the database
        @param flavorID is the unique ID of the flavor
        @return IceCreamFlavor object if found
    */
    public IceCreamFlavor getFlavorById(int flavorID) {
        String sql = "SELECT * FROM IceCreamFlavor WHERE flavorID = ?";

        // Try with resources closes connection and prepared statement
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the flavor ID in the query
            stmt.setInt(1, flavorID);

            // Execute the query and store the result
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new IceCreamFlavor(
                            rs.getInt("flavorID"),
                            rs.getString("flavorName"),
                            rs.getString("seasonality"),
                            rs.getInt("stockLevel"),
                            rs.getInt("remakeThreshold"),
                            rs.getString("allergens"),
                            rs.getString("availabilityStatus"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print error
        }

        return null; // Return null if no matching flavor found
    }

    /** Creates a new ice cream flavor in the database
        @param flavorName the name of the flavor
        @param seasonality the seasonal category of the flavor
        @param stockLevel the current stock amount
        @param remakeThreshold the stock level that triggers restocking
        @param allergens allergy information for the flavor
        @param availabilityStatus whether the flavor is available or unavailable
        @return true if the insert was successful, false otherwise
    */
    public boolean createFlavor(String flavorName, String seasonality, int stockLevel,
            int remakeThreshold, String allergens, String availabilityStatus) {
        String sql = """
                INSERT INTO IceCreamFlavor
                (flavorName, seasonality, stockLevel, remakeThreshold, allergens, availabilityStatus)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set the placeholder value before inserting
            stmt.setString(1, flavorName);
            stmt.setString(2, seasonality);
            stmt.setInt(3, stockLevel);
            stmt.setInt(4, remakeThreshold);
            stmt.setString(5, allergens);
            stmt.setString(6, availabilityStatus);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Print error
            return false;
        }
    }

    /** Updates the availability of a flavor
        @param flavorID the ID of the flavor to update
        @param newAvailability the new availability status
        @return true if the update was successful, false otherwise
    */
    public boolean updateFlavorAvailability(int flavorID, String newAvailability) {
        String sql = "UPDATE IceCreamFlavor SET availabilityStatus = ? WHERE flavorID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set new availability value and target flavor ID
            stmt.setString(1, newAvailability);
            stmt.setInt(2, flavorID);
            return stmt.executeUpdate() > 0; // Return true if updated
        } catch (SQLException e) {
            e.printStackTrace(); // Print error
            return false;
        }
    }

    /** Updates the seasonality category of a specific flavor.
        @param flavorID the ID of the flavor to update
        @param newSeasonality the new seasonality value
        @return true if the update was successful, false otherwise
    */
    public boolean updateFlavorSeasonality(int flavorID, String newSeasonality) {
        String sql = "UPDATE IceCreamFlavor SET seasonality = ? WHERE flavorID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the new seasonality and target flavor ID
            stmt.setString(1, newSeasonality);
            stmt.setInt(2, flavorID);
            return stmt.executeUpdate() > 0; // Retutrn true if updates
        } catch (SQLException e) {
            e.printStackTrace(); // Print error
            return false;
        }
    }

    /** Removes a flavor from the database by its ID.
        @param flavorID the ID of the flavor to delete
        @return true if deletion was successful, false otherwise
    */
    public boolean removeFlavor(int flavorID) {
        String sql = "DELETE FROM IceCreamFlavor WHERE flavorID = ?";

        
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the target flavor ID to be deleted
            stmt.setInt(1, flavorID);
            return stmt.executeUpdate() > 0; // Return true if updated
        } catch (SQLException e) { // Return false otherwise
            return false;
        }
    }

    /** Decreases the stock level of a flavor every time it is purchases if enough stock
        @param flavorID the ID of the flavor to update
        @param amount the amount of stock to subtract
        @return true if the stock was successfully decreased, false otherwise
    */
    public boolean decreaseStock(int flavorID, int amount) {
        String sql = """
                UPDATE IceCreamFlavor
                SET stockLevel = stockLevel - ?
                WHERE flavorID = ? AND stockLevel >= ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the amount ot decrease, flavorID, and the minimum stock check
            stmt.setInt(1, amount);
            stmt.setInt(2, flavorID);
            stmt.setInt(3, amount);
            return stmt.executeUpdate() > 0; // Return true if updated
        } catch (SQLException e) { // Print error
            e.printStackTrace();
            return false;
        }
    }

    /** Sets the stock level of a flavor to a new value.
        @param flavorID the ID of the flavor to update
        @param newStock the new stock level
        @return true if the stock update was successful, false otherwise
    */
    public boolean updateFlavorStock(int flavorID, int newStock) {
        String sql = "UPDATE IceCreamFlavor SET stockLevel = ? WHERE flavorID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the new stock and the target flavor ID
            stmt.setInt(1, newStock);
            stmt.setInt(2, flavorID);
            return stmt.executeUpdate() > 0; // Return true if updated

        } catch (SQLException e) { // Print error
            e.printStackTrace();
            return false;
        }
    }
}
