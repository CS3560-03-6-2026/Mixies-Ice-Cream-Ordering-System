import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ToppingDAO {

    public List<Topping> getAllToppings() {
        List<Topping> toppings = new ArrayList<>();
        String sql = "SELECT * FROM Topping ORDER BY toppingName";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

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

    public boolean removeTopping(int toppingID) {
        String sql = "DELETE FROM Topping WHERE toppingID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, toppingID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}