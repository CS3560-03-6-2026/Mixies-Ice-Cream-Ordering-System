import javax.swing.SwingUtilities;

/**
 * Main class that serves as the entry point for the Mixies application.
 * 
 * It initializes the GUI on the Event Dispatch Thread (EDT) and
 * launches the main application window with a logged-in employee.
 */
public class Main {

    public static void main(String[] args) {

        // Ensures that all Swing components are created and updated
        // on the Event Dispatch Thread (best practice for Swing apps)
        SwingUtilities.invokeLater(() -> {

            // Create a sample logged-in employee (hardcoded for now)
            Employee loggedIn = new Employee(1, "Ava", EmployeeRoles.MANAGER);

            // Create and display the main application frame
            new MixiesAppFrame(loggedIn).setVisible(true);
        });
    }
}