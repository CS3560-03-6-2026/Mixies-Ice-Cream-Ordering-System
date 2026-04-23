import java.sql.*;

public class EmployeeDAO {

    public Employee getEmployeeById(int employeeID) {
        String sql = "SELECT * FROM Employee WHERE employeeID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Employee(
                            rs.getInt("employeeID"),
                            rs.getString("employeeName"),
                            rs.getString("employeeRole"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}