// this is the topping panel, with scoops/cones/toppings
// customize after ice cream flavor
// a go back to menu button
// and a add to cart button

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class IceCreamCustomizationPanel extends JPanel {

    // Service layer for database operations
    private final MixiesService service;

    // Shared kiosk session (stores selected flavor and order ID)
    private final KioskSession session;

    // Navigator for switching screens
    private final KioskNavigator navigator;

    // Label to display selected flavor
    private final JLabel flavorLabel = new JLabel("Selected Flavor: None", SwingConstants.CENTER);

    // Spinner to select number of scoops (1-3)
    private final JSpinner scoopSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 3, 1));

    // Label to display cost based on number of scoops
    private final JLabel costLabel = new JLabel("Cost: $3.50", SwingConstants.CENTER);

    // Panel that holds topping cards (populated from DB)
    private final JPanel toppingCardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

    // List of checkboxes matching each topping from the database
    private final List<JCheckBox> toppingCheckboxes = new ArrayList<>();

    // List of toppings loaded from the database
    private List<Topping> toppings = new ArrayList<>();

    public IceCreamCustomizationPanel(MixiesService service, KioskSession session, KioskNavigator navigator) {
        this.service = service;
        this.session = session;
        this.navigator = navigator;

        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Customize Ice Cream", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));

        // Back button
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> navigator.showMenu());

        // Add to cart button
        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.addActionListener(e -> addToCart());

        // Bottom panel for buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backButton);
        bottomPanel.add(addToCartButton);

        // Scoop selector row
        // Updates cost label whenever number of scoops changes
        {
            scoopSpinner.addChangeListener(e -> {
                int scoops = (int) scoopSpinner.getValue();
                double cost = Prices.SCOOP_PRICES[scoops];
                costLabel.setText(String.format("Cost: $%.2f", cost));
            });
        }
        JPanel scoopPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel scoopLabel = new JLabel("Number of Scoops:");
        scoopLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        scoopSpinner.setPreferredSize(new Dimension(60, 30));
        scoopPanel.add(scoopLabel);
        scoopPanel.add(scoopSpinner);
        scoopPanel.add(costLabel);

        // Toppings label
        JLabel toppingTitle = new JLabel("Select Toppings:", SwingConstants.CENTER);
        toppingTitle.setFont(new Font("SansSerif", Font.BOLD, 16));

        // Scroll pane for topping cards
        JScrollPane toppingScroll = new JScrollPane(toppingCardPanel);
        toppingScroll.setPreferredSize(new Dimension(500, 200));
        toppingScroll.setBorder(BorderFactory.createTitledBorder("Toppings"));
        toppingScroll.getHorizontalScrollBar().setUnitIncrement(16);

        // Center panel layout
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        flavorLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        flavorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        toppingTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoopPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        toppingScroll.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(flavorLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(scoopPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(toppingTitle);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(toppingScroll);

        // Add components
        add(title, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Creates a card for each topping with image and checkbox
    // Matches the same pattern as createFlavorCard in IceCreamMenuPanel
    private JPanel createToppingCard(Topping topping, JCheckBox checkBox) {

        // Panel representing one topping card
        JPanel card = new JPanel(new BorderLayout());

        // Set card size and styling
        card.setPreferredSize(new Dimension(140, 170));
        card.setMinimumSize(new Dimension(140, 170));
        card.setMaximumSize(new Dimension(140, 170));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Load topping image using topping name (lowercase, no spaces) + "-topping.png"
        JLabel imageLabel = new JLabel();
        try {
            String path = "src/images/" + topping.getToppingName()
                    .toLowerCase()
                    .replaceAll(" ", "") + "-topping.png";

            Image img = new ImageIcon(path).getImage()
                    .getScaledInstance(100, 100, Image.SCALE_SMOOTH);

            if (img != null) {
                imageLabel = new JLabel(new ImageIcon(img));
            } else {
                imageLabel = new JLabel("No Image", SwingConstants.CENTER);
                System.out.println("Image not found for topping: " + topping.getToppingName());
            }
        } catch (Exception e) {
            imageLabel = new JLabel("No Image", SwingConstants.CENTER);
        }

        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Label to display topping name
        JLabel nameLabel = new JLabel(topping.getToppingName(), SwingConstants.CENTER);

        // Checkbox at the bottom of the card
        checkBox.setHorizontalAlignment(SwingConstants.CENTER);
        checkBox.setBackground(Color.WHITE);

        // Add components to the card
        card.add(nameLabel, BorderLayout.NORTH);
        card.add(imageLabel, BorderLayout.CENTER);
        card.add(checkBox, BorderLayout.SOUTH);

        return card;
    }

    // Loads toppings from the database and builds topping cards
    private void loadToppings() {
        toppingCardPanel.removeAll();
        toppingCheckboxes.clear();

        // Fetch all toppings from DB via service layer
        toppings = service.getAllToppings();

        for (Topping topping : toppings) {
            JCheckBox checkBox = new JCheckBox("Add");
            checkBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
            toppingCheckboxes.add(checkBox);
            toppingCardPanel.add(createToppingCard(topping, checkBox));
        }

        toppingCardPanel.revalidate();
        toppingCardPanel.repaint();
    }

    // Resets all topping checkboxes to unchecked
    private void resetToppingSelections() {
        for (JCheckBox checkBox : toppingCheckboxes) {
            checkBox.setSelected(false);
        }
    }

    // Updates the displayed flavor when panel becomes visible
    private void refreshSelectedFlavor() {
        IceCreamFlavor flavor = session.getSelectedFlavor();

        if (flavor == null) {
            flavorLabel.setText("Selected Flavor: None");
        } else {
            flavorLabel.setText("Selected Flavor: " + flavor.getFlavorName());
        }
    }

    // Runs whenever panel is shown
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (visible) {
            refreshSelectedFlavor();
            loadToppings();
            resetToppingSelections();

            // Reset scoop count to 1 each time panel opens
            scoopSpinner.setValue(1);
        }
    }

    // Adds selected flavor, scoops, and toppings to the cart
    private void addToCart() {

        IceCreamFlavor flavor = session.getSelectedFlavor();

        if (flavor == null) {
            JOptionPane.showMessageDialog(this, "No flavor selected.");
            return;
        }

        int orderID = session.getCurrentOrderID();

        if (orderID == -1) {
            JOptionPane.showMessageDialog(this, "No active order.");
            return;
        }

        // Get selected scoop count from spinner
        int scoops = (int) scoopSpinner.getValue();

        int orderItemID = service.addOrderItem(
                orderID,
                flavor.getFlavorID(),
                scoops);

        if (orderItemID == -1) {
            JOptionPane.showMessageDialog(this, "Could not add item.");
            return;
        }

        // Add each selected topping to the order item
        for (int i = 0; i < toppingCheckboxes.size(); i++) {
            if (toppingCheckboxes.get(i).isSelected()) {
                Topping topping = toppings.get(i);
                service.addOrderItemTopping(orderItemID, topping.getToppingID(), 1);
            }
        }

        JOptionPane.showMessageDialog(this, "Item added to cart.");

        // Navigate to cart after adding
        navigator.showCart();
    }
}