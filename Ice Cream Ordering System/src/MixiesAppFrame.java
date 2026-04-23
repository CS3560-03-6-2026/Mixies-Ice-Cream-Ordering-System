import javax.swing.*;
import java.awt.*;

public class MixiesAppFrame extends JFrame {

    private final MixiesService service;
    private final Employee loggedInEmployee;
    private final JTabbedPane tabs;

    public MixiesAppFrame(Employee loggedInEmployee) {
        this.loggedInEmployee = loggedInEmployee;
        this.service = new MixiesService();
        this.tabs = new JTabbedPane();

        setTitle("Mixies Ice Cream System");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Default tabs
        tabs.addTab("Employee Orders", new EmployeeOrderPanel(service, loggedInEmployee));
        tabs.addTab("Orders", new OrdersPanel(service));

        // Top bar
        JButton managerAccessButton = new JButton("Manager Access");
        managerAccessButton.addActionListener(e -> openManagerAccess());

        JLabel loggedInLabel = new JLabel(
                "Logged in: " + loggedInEmployee.getEmployeeName() +
                        " (" + loggedInEmployee.getEmployeeRole() + ")");

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(loggedInLabel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(managerAccessButton);
        topPanel.add(rightPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private void openManagerAccess() {
        String input = JOptionPane.showInputDialog(this, "Enter employee ID:");
        if (input == null || input.isBlank()) {
            return;
        }

        try {
            int employeeID = Integer.parseInt(input.trim());
            Employee manager = service.getEmployeeById(employeeID);

            if (manager == null) {
                JOptionPane.showMessageDialog(this, "Employee ID not found.");
                return;
            }

            if (!"Manager".equalsIgnoreCase(manager.getEmployeeRole())) {
                JOptionPane.showMessageDialog(this, "Access denied. Employee is not a manager.");
                return;
            }

            int existingTabIndex = tabs.indexOfTab("Manager");

            if (existingTabIndex == -1) {
                tabs.addTab("Manager", new ManagerPanel(service, manager));
                tabs.setSelectedIndex(tabs.getTabCount() - 1);
            } else {
                tabs.setSelectedIndex(existingTabIndex);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid employee ID.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Change this to match a real employee in your database
            Employee loggedIn = new Employee(1, "Ava", "Employee");
            new MixiesAppFrame(loggedIn).setVisible(true);
        });
    }
}