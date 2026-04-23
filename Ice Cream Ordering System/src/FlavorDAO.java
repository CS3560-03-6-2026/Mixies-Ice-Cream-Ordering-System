import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlavorDAO {

    public List<IceCreamFlavor> getAllFlavors() {
        List<IceCreamFlavor> flavors = new ArrayList<>();
        String sql = "SELECT * FROM IceCreamFlavor ORDER BY flavorName";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

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
            e.printStackTrace();
        }

        return flavors;
    }

    public IceCreamFlavor getFlavorById(int flavorID) {
        String sql = "SELECT * FROM IceCreamFlavor WHERE flavorID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, flavorID);

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
            e.printStackTrace();
        }

        return null;
    }

    public boolean createFlavor(String flavorName, String seasonality, int stockLevel,
            int remakeThreshold, String allergens, String availabilityStatus) {
        String sql = """
                INSERT INTO IceCreamFlavor
                (flavorName, seasonality, stockLevel, remakeThreshold, allergens, availabilityStatus)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, flavorName);
            stmt.setString(2, seasonality);
            stmt.setInt(3, stockLevel);
            stmt.setInt(4, remakeThreshold);
            stmt.setString(5, allergens);
            stmt.setString(6, availabilityStatus);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFlavorAvailability(int flavorID, String newAvailability) {
        String sql = "UPDATE IceCreamFlavor SET availabilityStatus = ? WHERE flavorID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newAvailability);
            stmt.setInt(2, flavorID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFlavorSeasonality(int flavorID, String newSeasonality) {
        String sql = "UPDATE IceCreamFlavor SET seasonality = ? WHERE flavorID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newSeasonality);
            stmt.setInt(2, flavorID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeFlavor(int flavorID) {
        String sql = "DELETE FROM IceCreamFlavor WHERE flavorID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, flavorID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean decreaseStock(int flavorID, int amount) {
        String sql = """
                UPDATE IceCreamFlavor
                SET stockLevel = stockLevel - ?
                WHERE flavorID = ? AND stockLevel >= ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, amount);
            stmt.setInt(2, flavorID);
            stmt.setInt(3, amount);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFlavorStock(int flavorID, int newStock) {
        String sql = "UPDATE IceCreamFlavor SET stockLevel = ? WHERE flavorID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newStock);
            stmt.setInt(2, flavorID);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}