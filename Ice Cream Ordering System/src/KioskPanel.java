import java.awt.*;

/**
 * KioskPanel represents the customer-facing kiosk UI.
 * 
 * It allows users to:
 * - Start an order
 * - Browse flavors
 * - Customize items (scoops + toppings)
 * - View cart and checkout
 * - Complete the order with an optional tip
 */
public class KioskPanel extends JPanel {

    // Service layer for business logic
    private final MixiesService service;

    // Employee creating the order (kiosk user)
    private final Employee employee;

    // Card layout to switch between different screens
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    // Daily order number
    // Increase at order completion
    private int orderNumber = 1;

    private final JLabel subtotalLabel = new JLabel("Subtotal: $0.00");
    private final JLabel tipLabel = new JLabel("Tip: $0.00");
    private final JLabel totalLabel = new JLabel("Total: $0.00");

    /**
     * Constructor initializes all screens and sets default view.
     */
    public KioskPanel(MixiesService service, Employee employee, OrdersPanel ordersPanel) {
        this.service = service;
        this.employee = employee;

        setLayout(new BorderLayout());

        // Add all screens to card layout
        cardPanel.add(buildWelcomeScreen(), "welcome");
        cardPanel.add(new IceCreamMenuPanel(service), "menu");
        //cardPanel.add(buildCustomizeScreen(), "customize");
        //cardPanel.add(buildCheckoutScreen(), "checkout");

        add(cardPanel, BorderLayout.CENTER);

        // Show welcome screen first
        cardLayout.show(cardPanel, "welcome");
    }

    /**
     * Builds the initial welcome screen.
     */
    private JPanel buildWelcomeScreen() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Welcome to Mixies", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));

        JButton startButton = new JButton("Start Order");
        startButton.setPreferredSize(new Dimension(180, 60));

        // Start button
        startButton.addActionListener(e -> {
            // Switch to Menu panel
            cardLayout.show(cardPanel, "menu");
        });

        JPanel center = new JPanel();
        center.add(startButton);

        panel.add(title, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }
}

    