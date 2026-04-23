import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Utility Class to establish a connection to the database
public class DBConnection {
    // The database must be running on the same machine
    private static final String URL = "jdbc:mysql://localhost:3306/MixiesDB";
    private static final String USER = "root";
    private static final String PASSWORD = "cs3560mixies";

    // Ensure MySQL driver is available
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found on classpath.", e);
        }
    }

    // Method to establish and return a database connection
    // Throws SQLException for connection error
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD); // Uses DriveManager
    }
}
