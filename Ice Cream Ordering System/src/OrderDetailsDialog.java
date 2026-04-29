import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * OrderDetailsDialog is a modal dialog that displays all items
 * for a specific order. It allows the user to:
 * - View order item details (flavor, quantity, cost, refund status)
 * - Refund selected items (if eligible)
 * - Refresh the displayed data
 *
 * This dialog interacts with the MixiesService to retrieve and
 * update order item information.
 */
public class OrderDetailsDialog extends JDialog {

    // Service layer for accessing order data
    private final MixiesService service;

    // ID of the order being viewed
    private final int orderID;

    // Table model for displaying order items (non-editable)
    private final DefaultTableModel itemsTableModel = new DefaultTableModel(
            new Object[] { "OrderItem ID", "Flavor", "Scoops", "Cost", "Refund Status" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Prevent editing in table
        }
    };

    // Table displaying order items
    private final JTable itemsTable = new JTable(itemsTableModel);

    /**
     * Constructor initializes the dialog window and UI components.
     */
    public OrderDetailsDialog(Frame owner, MixiesService service, int orderID) {

        // Create modal dialog tied to parent frame
        super(owner, "Order Details - " + orderID, true);

        this.service = service;
        this.orderID = orderID;

        // Dialog settings
        setSize(700, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Buttons for refreshing data and refunding items
        JButton refundButton = new JButton("Refund Selected Item");
        JButton refreshButton = new JButton("Refresh");

        // Top panel for action buttons
        JPanel top = new JPanel();
        top.add(refreshButton);
        top.add(refundButton);

        // Add components to dialog
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(itemsTable), BorderLayout.CENTER);

        // Button actions
        refreshButton.addActionListener(e -> refreshItems());
        refundButton.addActionListener(e -> refundSelectedItem());

        // Load initial data
        refreshItems();
    }

    /**
     * Reloads order items from the service and updates the table.
     */
    private void refreshItems() {

        // Clear existing rows
        itemsTableModel.setRowCount(0);

        // Fetch updated order items
        List<OrderItem> items = service.getOrderItemsForOrder(orderID);

        // Populate table with item data
        for (OrderItem item : items) {
            itemsTableModel.addRow(new Object[] {
                    item.getOrderItemID(),
                    item.getFlavor().getFlavorName(),
                    item.getQuantity(),
                    service.getDisplayedOrderItemCost(item),
                    item.getRefundStatus()
            });
        }
    }

    /**
     * Refunds the selected order item (if one is selected).
     */
    private void refundSelectedItem() {

        // Get selected row
        int selectedRow = itemsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an item first.");
            return;
        }

        // Get order item ID from table
        int orderItemID = (int) itemsTableModel.getValueAt(selectedRow, 0);

        // Attempt refund through service layer
        boolean ok = service.refundOrderItem(orderItemID, orderID);

        // Show result message
        JOptionPane.showMessageDialog(this, ok ? "Item refunded." : "Refund failed.");

        // Refresh table after refund attempt
        refreshItems();
    }
}