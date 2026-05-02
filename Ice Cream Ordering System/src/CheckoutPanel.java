/* 
 * CheckoutPanel is where the user finalizes the order
 * with tip and payment method.
 * It includes a back-to-cart button and a confirm order button.
 */

import java.awt.*;
import java.util.List;
import javax.swing.*;

public class CheckoutPanel extends JPanel {

    private final MixiesService service;
    private final KioskSession session;
    private final KioskNavigator navigator;
    private final OrdersPanel ordersPanel;
    private final OrderTimeoutManager timeoutManager;

    // Labels used to display the order totals
    private final JLabel subtotalLabel = new JLabel("Subtotal: $0.00");
    private final JLabel taxLabel = new JLabel("Tax: $0.00");
    private final JLabel tipLabel = new JLabel("Tip: $0.00");
    private final JLabel totalLabel = new JLabel("Total: $0.00");

    private double subtotal = 0.0;

    public CheckoutPanel(MixiesService service, KioskSession session,
            KioskNavigator navigator, OrdersPanel ordersPanel, OrderTimeoutManager timeoutManager) {
        this.service = service;
        this.session = session;
        this.navigator = navigator;
        this.ordersPanel = ordersPanel;
        this.timeoutManager = timeoutManager;

        setLayout(new BorderLayout());

        // Title at top of checkout screen
        JLabel title = new JLabel("Checkout", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        // Center panel displays subtotal, tip, total, and payment method
        JPanel centerPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Tip selection panel
        JPanel tipPanel = new JPanel();
        tipPanel.setBorder(BorderFactory.createTitledBorder("Tip"));

        JButton noTip = new JButton("No Tip");
        JButton fifteenTip = new JButton("15%");
        JButton eighteenTip = new JButton("18%");
        JButton twentyTip = new JButton("20%");

        noTip.addActionListener(e -> setTipPercent(0.0));
        fifteenTip.addActionListener(e -> setTipPercent(0.15));
        eighteenTip.addActionListener(e -> setTipPercent(0.18));
        twentyTip.addActionListener(e -> setTipPercent(0.20));

        tipPanel.add(noTip);
        tipPanel.add(fifteenTip);
        tipPanel.add(eighteenTip);
        tipPanel.add(twentyTip);

        // Payment method section
        JLabel paymentLabel = new JLabel("Select Payment Method:");

        JComboBox<String> paymentBox = new JComboBox<>(new String[] { "Cash", "Credit Card", "Debit Card" });

        // Add total/payment components to center panel
        centerPanel.add(subtotalLabel);
        centerPanel.add(taxLabel);
        centerPanel.add(tipLabel);
        centerPanel.add(totalLabel);
        centerPanel.add(paymentLabel);
        centerPanel.add(paymentBox);

        // Back button returns user to cart
        JButton backButton = new JButton("Back to Cart");
        backButton.addActionListener(e -> navigator.showCart());

        // Confirm button completes order
        JButton confirmButton = new JButton("Confirm Order");
        confirmButton.addActionListener(e -> confirmOrder());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backButton);
        bottomPanel.add(confirmButton);

        add(title, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(tipPanel, BorderLayout.WEST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Refreshes checkout totals whenever checkout screen is shown
    public void refreshCheckout() {
        int orderID = session.getCurrentOrderID();

        if (orderID == -1) {
            subtotal = 0.0;
            updateLabels();
            return;
        }

        List<OrderItem> items = service.getOrderItemsForOrder(orderID);
        subtotal = 0.0;

        for (OrderItem item : items) {
            subtotal += service.getDisplayedOrderItemCost(item);
        }

        updateLabels();
    }

    // Stores selected tip percentage and updates displayed totals
    private void setTipPercent(double percent) {
        session.setSelectedTip(percent);
        updateLabels();
    }

    // Updates subtotal, tip, and total labels
    private void updateLabels() {
        double tipAmount = subtotal * session.getSelectedTip();
        double tax = subtotal * Prices.TAX_RATE;
        double total = subtotal + tax + tipAmount;

        subtotalLabel.setText(String.format("Subtotal: $%.2f", subtotal));
        taxLabel.setText(String.format("Tax: $%.2f", tax));
        tipLabel.setText(String.format("Tip: $%.2f", tipAmount));
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    // Completes the order and saves final total/tip to database
    private void confirmOrder() {
        int orderID = session.getCurrentOrderID();

        if (orderID == -1) {
            JOptionPane.showMessageDialog(this, "No active order.");
            return;
        }

        List<OrderItem> items = service.getOrderItemsForOrder(orderID);

        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Add at least one item first.");
            return;
        }

        double total = service.getOrderTotal(orderID);

        if (total == 0.0) {
            JOptionPane.showMessageDialog(this, "Order total is zero.");
            return;
        }

        double tipAmount = subtotal * session.getSelectedTip();

        service.updateOrderTip(orderID, tipAmount);
        service.refreshOrderTotal(orderID);

        boolean checked_out = service.checkoutOrder(orderID);

        if (checked_out) {

            JOptionPane.showMessageDialog(this, "Order confirmed! Total: $" + String.format("%.2f", total + tipAmount));
            session.reset();
            navigator.showWelcome();
            timeoutManager.onOrderEnded();
            ordersPanel.refreshOrders();

        } else {
            JOptionPane.showMessageDialog(this, "Could not confirm order.");
        }

    }

    // Automatically refresh checkout data when this panel becomes visible
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (visible) {
            refreshCheckout();
        }
    }
}