import javax.swing.*;

public class MixiesAppFrame extends JFrame {

    public MixiesAppFrame(Employee loggedInEmployee) {
        MixiesService service = new MixiesService();

        setTitle("Mixies Ice Cream System");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Employee Orders", new EmployeeOrderPanel(service, loggedInEmployee));

        if ("Manager".equalsIgnoreCase(loggedInEmployee.getEmployeeRole())) {
            tabs.addTab("Manager", new ManagerPanel(service, loggedInEmployee));
        }

        add(tabs);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Employee loggedIn = new Employee(1, "Ava", "Manager");
            new MixiesAppFrame(loggedIn).setVisible(true);
        });
    }
}