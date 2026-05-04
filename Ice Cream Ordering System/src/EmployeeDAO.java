import java.sql.*;

/** Data Access Object Class for Employee
    Handles all Employee database operations
*/
public class EmployeeDAO {

    /** Retrieves an Employee from the database by their ID
        @param employeeID is the ID of the employee
        @return Employee object if found
    */
    public Employee getEmployeeById(int employeeID) {
        String sql = "SELECT * FROM Employee WHERE employeeID = ?";

        // try with resources to close connection and prepared statement 
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set value of the placeholder in the query
            stmt.setInt(1, employeeID);

            // Execute the query and store results
            try (ResultSet rs = stmt.executeQuery()) {

                // If result found, create employee object
                if (rs.next()) {
                    return new Employee(
                            rs.getInt("employeeID"),
                            rs.getString("employeeName"),
                            EmployeeRoles.valueOf(rs.getString("employeeRole").toUpperCase())
                    );
                }
            }
        } catch (SQLException e) { // Print error details 
            e.printStackTrace();
        }

        return null; // Return null if employee is not found
    }
}
