import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ToppingDAO handles all database operations related to toppings.
 * 
 * Responsibilities include:
 * - Retrieving all toppings
 * - Creating new toppings
 * - Removing existing toppings
 * 
 * This class interacts directly with the database using JDBC.
 */
public class ToppingDAO {

    /**
     * Retrieves all toppings from the database,
     * sorted alphabetically by name.
     */
    public List<Topping> getAllToppings() {
        List<Topping> toppings = new ArrayList<>();
        String sql = "SELECT * FROM Topping ORDER BY toppingName";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Loop through result set and create Topping objects
            while (rs.next()) {
                toppings.add(new Topping(
                        rs.getInt("toppingID"),
                        rs.getString("toppingName")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return toppings;
    }

    public Topping getToppingById(int toppingID) {
        String sql = "SELECT * FROM Topping WHERE toppingID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, toppingID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Topping(
                            rs.getInt("toppingID"),
                            rs.getString("toppingName")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Inserts a new topping into the database.
     * 
     * @param toppingName Name of the topping
     * @return true if insertion was successful, false otherwise
     */
    public boolean createTopping(String toppingName) {
        String sql = "INSERT INTO Topping (toppingName) VALUES (?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, toppingName);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a topping from the database using its ID.
     * 
     * @param toppingID ID of the topping to remove
     * @return true if deletion was successful, false otherwise
     */
    public boolean removeTopping(int toppingID) {
        String sql = "DELETE FROM Topping WHERE toppingID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, toppingID);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            // Likely fails if topping is referenced by other tables (foreign key constraint)
            return false;
        }
    }
}