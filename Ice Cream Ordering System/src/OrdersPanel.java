import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * OrdersPanel displays a list of all orders in the system.
 * 
 * It allows the user to:
 * - View all orders with their ID, total, and status
 * - Refresh the order list
 * - Open a detailed view of a selected order
 * 
 * This panel interacts with the MixiesService to retrieve
 * and display order data.
 */
public class OrdersPanel extends JPanel {

    // Service layer used to fetch order data
    private final MixiesService service;

    // Table model for displaying orders (non-editable)
    private final DefaultTableModel ordersTableModel = new DefaultTableModel(
        new Object[] { "Order ID", "Total", "Status", "Actions" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 3; // Only the button column is editable
        }
    };

    // Table displaying all orders
    private final JTable ordersTable = new JTable(ordersTableModel);

    private final ButtonColumn completeButtonColumn;

    /**
     * Constructor initializes UI components and event handlers.
     */
    public OrdersPanel(MixiesService service) {
        this.service = service;

        // Set layout for the panel
        setLayout(new BorderLayout());

        // Buttons for refreshing and viewing order details
        JButton refreshButton = new JButton("Refresh Orders");
        JButton viewDetailsButton = new JButton("View Order Details");

        // Top panel for buttons
        JPanel top = new JPanel();
        top.add(refreshButton);
        top.add(viewDetailsButton);

        // Configure orders table
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1 && row != -1) {
                    int modelRow = table.convertRowIndexToModel(row);

                    int orderID = (int) ordersTableModel.getValueAt(modelRow, 0);
                    viewSelectedOrder(orderID);
                }
            }
        });

        // Add components to panel
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(ordersTable), BorderLayout.CENTER);

        completeButtonColumn = new ButtonColumn(ordersTable, row -> {
            int orderID = (int) ordersTableModel.getValueAt(row, 0);
            completeOrder(orderID);
        }, row -> {
            String status = (String) ordersTableModel.getValueAt(row, 2);
            return "Open".equalsIgnoreCase(status);
        }, 3);

        // Button actions
        refreshButton.addActionListener(e -> refreshOrders());
        viewDetailsButton.addActionListener(e -> viewSelectedOrder());

        // Load initial data
        refreshOrders();
    }

    /**
     * Reloads all orders from the service and updates the table.
     */
    public final void refreshOrders() {

        // Clear existing rows
        ordersTableModel.setRowCount(0);

        // Fetch all orders
        List<Order> orders = service.getAllOrders();

        // Populate table with order data, and buttons to complete order
        for (Order order : orders) {
            ordersTableModel.addRow(new Object[] {
                    order.getOrderID(),
                    order.getTotal(),
                    order.getOrderStatus(),
                    "Complete",
            });
        }
    }

    /**
     * Opens a dialog showing details for the selected order.
     */
    private void viewSelectedOrder() {

        // Get selected row
        int selectedRow = ordersTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an order first.");
            return;
        }

        // Get order ID from selected row
        int orderID = (int) ordersTableModel.getValueAt(selectedRow, 0);

        // Open OrderDetailsDialog for the selected order
        new OrderDetailsDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                service,
                orderID).setVisible(true);

        // Refresh orders after closing dialog (in case changes were made)
        refreshOrders();
    }

    private void viewSelectedOrder(int orderID) {

        if (orderID == -1) {
            JOptionPane.showMessageDialog(this, "Select an order first.");
            return;
        }

        new OrderDetailsDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                service,
                orderID).setVisible(true);

        refreshOrders();
    }

    private void completeOrder(int orderID) {
        // Mark the order as completed in the service
        service.concludeOrder(orderID);

        // Refresh the orders list to reflect changes
        refreshOrders();
    }
}