/*
 * CartPanel is where users can see their items.
 * It displays the cart total and provides buttons to go back or checkout.
 */

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CartPanel extends JPanel {

    private final MixiesService service;
    private final KioskSession session;
    private final KioskNavigator navigator;
    private final OrderTimeoutManager timeoutManager;

    // Table model for cart items
    private final DefaultTableModel cartTableModel = new DefaultTableModel(
            new Object[] { "OrderItem ID", "Flavor", "Quantity", "Cost", "Refund Status" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    // Table used to display current order items
    private final JTable cartTable = new JTable(cartTableModel);

    // Label showing subtotal for current cart
    private final JLabel subtotalLabel = new JLabel("Subtotal: $0.00");

    public CartPanel(MixiesService service, KioskSession session, KioskNavigator navigator, OrderTimeoutManager timeoutManager) {
        this.service = service;
        this.session = session;
        this.navigator = navigator;
        this.timeoutManager = timeoutManager;

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Cart", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        // Button returns user to flavor menu
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> navigator.showMenu());

        // Button removes the selected item from the cart
        JButton removeButton = new JButton("Remove Item");
        removeButton.addActionListener(e -> removeSelectedItem());

        JButton cancelButton = new JButton("Cancel Order");
        cancelButton.addActionListener(e -> cancelOrder());

        // Button moves user to checkout screen
        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(e -> {
            if (cartTableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Add at least one item first.");
                return;
            }

            navigator.showCheckout();
        });

        // Bottom panel contains subtotal and navigation buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel buttons = new JPanel();
        buttons.add(backButton);
        buttons.add(removeButton);
        buttons.add(cancelButton);
        buttons.add(checkoutButton);

        bottomPanel.add(subtotalLabel, BorderLayout.WEST);
        bottomPanel.add(buttons, BorderLayout.EAST);

        add(title, BorderLayout.NORTH);
        add(new JScrollPane(cartTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Refreshes cart table and subtotal from the database
    public void refreshCart() {
        cartTableModel.setRowCount(0);

        int orderID = session.getCurrentOrderID();

        if (orderID == -1) {
            subtotalLabel.setText("Subtotal: $0.00");
            return;
        }

        // Retrieve all items for current order
        List<OrderItem> items = service.getOrderItemsForOrder(orderID);
        double subtotal = 0.0;

        // Add each item to table and calculate subtotal
        for (OrderItem item : items) {
            double itemCost = service.getDisplayedOrderItemCost(item);
            subtotal += itemCost;

            cartTableModel.addRow(new Object[] {
                    item.getOrderItemID(),
                    item.getFlavor().getFlavorName(),
                    item.getQuantity(),
                    String.format("$%.2f", itemCost),
                    item.getRefundStatus()
            });
        }

        subtotalLabel.setText(String.format("Subtotal: $%.2f", subtotal));
    }

    // Removes the selected cart item from the order
    private void removeSelectedItem() {
        int selectedRow = cartTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an item first.");
            return;
        }

        int orderID = session.getCurrentOrderID();
        int orderItemID = (int) cartTableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Remove selected item from cart?",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean removed = service.removeOrderItem(orderItemID, orderID);

        JOptionPane.showMessageDialog(
                this,
                removed ? "Item removed." : "Could not remove item.");

        refreshCart();
    }

    public void cancelOrder() {
        int orderID = session.getCurrentOrderID();

        if (orderID == -1) {
            JOptionPane.showMessageDialog(this, "No active order.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Cancel the entire order? This cannot be undone.",
                "Confirm Cancel",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean cancelled = service.cancelOrder(orderID);

        JOptionPane.showMessageDialog(
                this,
                cancelled ? "Order cancelled." : "Could not cancel order.");
        
        refreshCart();
        session.reset();
        navigator.showWelcome();
        timeoutManager.onOrderEnded();
    }

    // Automatically refresh cart whenever this panel becomes visible
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        timeoutManager.onOrderActivity();

        if (visible) {
            refreshCart();
        }
    }
}