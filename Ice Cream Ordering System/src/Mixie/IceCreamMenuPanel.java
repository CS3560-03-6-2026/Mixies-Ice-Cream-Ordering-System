package Mixie;
import javax.swing.*;

import Mixie.Flavor.IceCreamFlavor;
import Mixie.Kiosk.KioskNavigator;
import Mixie.Kiosk.KioskSession;

import java.awt.*;

// Ice Cream Menu Panel
// Displays all available ice cream flavors
// Implementing a grid layout and scrollablity
public class IceCreamMenuPanel extends JPanel {
    
    // Service used to fetch flavor data
    public final MixiesService service;

    // Shared kiosk session (stores selected flavor)
    private final KioskSession session;

    // Navigator used to switch between kiosk screens
    private final KioskNavigator navigator;
    
    // Main panel
    public IceCreamMenuPanel(MixiesService service, KioskSession session, KioskNavigator navigator) {
        this.service = service;
        this.session = session;
        this.navigator = navigator;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        
        // Header
        JLabel header = new JLabel("Mixies Ice Cream", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 22));

        // Cart button
        JButton cartButton = new JButton("Cart/Checkout");
        
        cartButton.addActionListener(e -> navigator.showCart());        

        // Panel for flavor cards
        JPanel flavorGrid = new JPanel(new GridBagLayout());
        flavorGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 
        flavorGrid.setBackground(new Color(245, 245, 245));

        // Add header panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 245, 245));
        topPanel.add(header, BorderLayout.CENTER);
        topPanel.add(cartButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Card position
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        int col = 0;
        int row = 0;
        int maxCol = 3; 
        

        // Fetch flavors from the service and create cards
        for (IceCreamFlavor flavor : service.getAllFlavors()) {

            // Skip flavors that are marked unavailable
            if (!flavor.getAvailabilityStatus().equalsIgnoreCase("Unavailable")) {
                
                gbc.gridx = col;
                gbc.gridy = row;
                
                // Add a flavor card to the grid
                flavorGrid.add(createFlavorCard(flavor), gbc);

                // Increment
                col++;

                // Move to next row after reaching max columns
                if (col >= maxCol) {
                    col = 0;
                    row++;
                }
            }
        }

        // Vertical scrolling support
        gbc.gridx = 0;
        gbc.gridy = row + 1;
        gbc.weighty = 1.0;
        flavorGrid.add(Box.createVerticalGlue(), gbc);

        JScrollPane scrollPane = new JScrollPane(flavorGrid);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add scroll pane to the main panel
        add(scrollPane, BorderLayout.CENTER);
    }


    // Creates a card for flavors
    // Get an image and display it
    private JPanel createFlavorCard(IceCreamFlavor flavor) {

        // Panel representing one flavor card
        JPanel card = new JPanel(new BorderLayout());
        
        // Set card size and styling
        card.setPreferredSize(new Dimension(180, 200));
        card.setMinimumSize(new Dimension(180, 200));
        card.setMaximumSize(new Dimension(180, 200));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Load flavor image
        ImageIcon icon = null;
        JLabel imageLabel = new JLabel();
        try {
            // Build image file path based on flavor name
            String path = "src/images/" + flavor.getFlavorName()
                .toLowerCase()
                .replaceAll(" ", "") + ".png";
            
            // Scale image to fit the card
            Image img = new ImageIcon(path).getImage()
                    .getScaledInstance(150, 150, Image.SCALE_SMOOTH);

            if (img != null) {
                icon = new ImageIcon(img);
                imageLabel = new JLabel(icon);
            } else {
                imageLabel = new JLabel("No Image", SwingConstants.CENTER);
                // Debug message if image is missing
                System.out.println("Image not found for flavor: " + flavor.getFlavorName());
            }
        } catch (Exception e) {
            // Use empty placeholder if loading fails
            icon = new ImageIcon();
        }

        // Label to display the image
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Label to display the flavor name
        JLabel nameLabel = new JLabel(flavor.getFlavorName(), SwingConstants.CENTER);
        
        // Button to add flavor to order
        JButton addButton = new JButton("Customize");
        addButton.addActionListener(e -> {
            // Check stock before adding
            if (flavor.getStockLevel() > 0) {

                // Store selected flavor in session
                session.setSelectedFlavor(flavor);

                // Navigate to customization panel
                navigator.showCustomize();

            } else {
                // Show message if flavor is out of stock
                JOptionPane.showMessageDialog(this, 
                    flavor.getFlavorName() + " is out of stock. Sorry!");
            }
        });

        // Add components to the card
        card.add(nameLabel, BorderLayout.NORTH);
        card.add(imageLabel, BorderLayout.CENTER);
        card.add(addButton, BorderLayout.SOUTH);

        return card;
    }
}
