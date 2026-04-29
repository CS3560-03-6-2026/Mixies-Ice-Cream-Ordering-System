import java.awt.*;
import javax.swing.*;

/**
 * MixiesAppFrame is the main application window for the Mixies Ice Cream System.
 * 
 * It manages:
 * - Navigation between different panels using tabs
 * - Displaying employee information
 * - Providing access to employee, manager, and kiosk features
 * 
 * Tabs include:
 * - Employee Orders (order creation and management)
 * - Orders (viewing existing orders)
 * - Manager (restricted access for managers only)
 * - Kiosk Menu (customer-facing flavor selection)
 */
public class MixiesAppFrame extends JFrame {

    // Service layer used for all business logic and data access
    private final MixiesService service;

    // Tabbed pane for switching between different views
    private final JTabbedPane tabs;

    private final OrdersPanel ordersPanel;

    //Employee access
    JButton employeeAccessButton;

    JLabel loggedInLabel;

    /**
     * Constructor initializes the main application frame and UI components.
     */
    public MixiesAppFrame(Employee loggedInEmployee) {
        this.service = new MixiesService();
        this.tabs = new JTabbedPane();
        this.ordersPanel = new OrdersPanel(service);
        
        tabs.addTab("Kiosk Menu", new KioskPanel(service, loggedInEmployee, ordersPanel));

        // Frame settings
        setTitle("Mixies Ice Cream System");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    
        // Button to access manager panel
        this.employeeAccessButton = new JButton("Employee Access");
        employeeAccessButton.addActionListener(e -> employeeIDPrompt());

        // Label displaying logged-in employee info
        this.loggedInLabel = new JLabel("");

        // Top panel containing user info and action buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(loggedInLabel, BorderLayout.WEST);

        // Right side of the top panel for buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(employeeAccessButton);
        topPanel.add(rightPanel, BorderLayout.EAST);

        // Add components to frame
        add(topPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private void employeeIDPrompt() {
        String input = JOptionPane.showInputDialog(this, "Enter employee ID:");

        // Cancel or empty input
        if (input == null || input.isBlank()) {
            return;
        }

        try {
            int employeeID = Integer.parseInt(input.trim());
            Employee employee = service.getEmployeeById(employeeID);

            if (employee == null) {
                JOptionPane.showMessageDialog(this, "Employee ID not found.");
                return;
            }

            employeeAccessButton.setText("Sign Out (" + employee.getEmployeeName() + ")");
            employeeAccessButton.removeActionListener(employeeAccessButton.getActionListeners()[0]);
            employeeAccessButton.addActionListener(e -> employeeSignOut());
            
            //Changed logged in label to show employee name and role
            loggedInLabel.setText(
                "Logged in: " + employee.getEmployeeName() +
                " (" + employee.getEmployeeRole() + ")"
            );

            // Open employee access panel
            openEmployeeAccess(employee);
            
            // If employee is a manager, also open manager access
            if (employee.getEmployeeRole() == employeeRoles.MANAGER) {
                openManagerAccess(employee);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid employee ID.");
        }
    }

    private void openEmployeeAccess(Employee employee) {
        //Show employee orders and orders panels
        if (tabs.indexOfTab("Employee Orders") == -1) {
            tabs.addTab("Employee Orders", new EmployeeOrderPanel(service, employee));
        }

        if (tabs.indexOfTab("Orders") == -1) {
            tabs.addTab("Orders", new OrdersPanel(service));
        }
    }


    /**
     * Handles manager access by prompting for an employee ID
     * and verifying if the employee has manager privileges.
     * If valid, opens or switches to the Manager tab.
     */
    private void openManagerAccess(Employee manager) {

        // Check if Manager tab already exists
        int existingTabIndex = tabs.indexOfTab("Manager");

        if (existingTabIndex == -1) {
            // Create new Manager tab
            tabs.addTab("Manager", new ManagerPanel(service, manager));
            tabs.setSelectedIndex(tabs.getTabCount() - 1);
        } else {
            // Switch to existing Manager tab
            tabs.setSelectedIndex(existingTabIndex);
        }
    }

    private void employeeSignOut() {
        // Remove employee-specific tabs
        int employeeOrdersTabIndex = tabs.indexOfTab("Employee Orders");
        if (employeeOrdersTabIndex != -1) {
            tabs.removeTabAt(employeeOrdersTabIndex);
        }

        int ordersTabIndex = tabs.indexOfTab("Orders");
        if (ordersTabIndex != -1) {
            tabs.removeTabAt(ordersTabIndex);
        }

        int managerTabIndex = tabs.indexOfTab("Manager");
        if (managerTabIndex != -1) {
            tabs.removeTabAt(managerTabIndex);
        }

        loggedInLabel.setText("");

        // Reset employee access button
        employeeAccessButton.setText("Employee Access");
        employeeAccessButton.removeActionListener(employeeAccessButton.getActionListeners()[0]);
        employeeAccessButton.addActionListener(e -> employeeIDPrompt());
    }
}