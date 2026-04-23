import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Employee loggedIn = new Employee(1, "Alpha", "Manager");
            new MixiesAppFrame(loggedIn).setVisible(true);
        });
    }
}