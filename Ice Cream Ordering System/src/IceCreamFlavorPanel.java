import javax.swing.*;
import java.awt.*;


public class IceCreamFlavorPanel extends JPanel{
    public final MixiesService service;
    
    // Constructor to initialize the panel with the service and order item
    public IceCreamFlavorPanel(MixiesService service) {
        this.service = service;
        
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // Layout settings
        JPanel flavorGrid = new JPanel(new GridBagLayout());
        flavorGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 
        flavorGrid.setBackground(new Color(245, 245, 245));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        int col = 0;
        int row = 0;
        int maxCol = 3; 

        // Fetch flavors from the service and create cards
        for (IceCreamFlavor flavor : service.getAllFlavors()) {
            if (!flavor.getAvailabilityStatus().equalsIgnoreCase("Unavailable")) {
                
                gbc.gridx = col;
                gbc.gridy = row;
                
                flavorGrid.add(createFlavorCard(flavor), gbc);

                col++;
                if (col >= maxCol) {
                    col = 0;
                    row++;
                }
            }
        }

        // Scroll pane 
        gbc.gridx = 0;
        gbc.gridy = row + 1;
        gbc.weighty = 1.0;
        flavorGrid.add(Box.createVerticalGlue(), gbc);

        JScrollPane scrollPane = new JScrollPane(flavorGrid);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    // Method for creating a card for each flavor
    private JPanel createFlavorCard(IceCreamFlavor flavor) {
        JPanel card = new JPanel(new BorderLayout());
        
        // Card properties
        card.setPreferredSize(new Dimension(180, 200));
        card.setMinimumSize(new Dimension(180, 200));
        card.setMaximumSize(new Dimension(180, 200));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Image
        ImageIcon icon = null;
        try {
            String path = "src/images/" + flavor.getFlavorName()
                .toLowerCase()
                .replaceAll(" ", "") + ".png";
            
            Image img = new ImageIcon(path).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);

            if (img != null) {
                icon = new ImageIcon(img);
            } else {
                System.out.println("Image not found for flavor: " + flavor.getFlavorName());
            }
        } catch (Exception e) {
            // Placeholder for missing image
            icon = new ImageIcon();
        }

        JLabel imageLabel = new JLabel(icon);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Flavor name
        JLabel nameLabel = new JLabel(flavor.getFlavorName(), SwingConstants.CENTER);
        
        // Add to Order button
        JButton addButton = new JButton("Add to Order");
        addButton.addActionListener(e -> {
            if (flavor.getStockLevel() > 0) {
                //CustomOrderItem.addItem(flavor);
                JOptionPane.showMessageDialog(this, flavor.getFlavorName() + " added to order!");
            } else {
                JOptionPane.showMessageDialog(this, flavor.getFlavorName() + " is out of stock.");
            }
        });

        card.add(nameLabel, BorderLayout.NORTH);
        card.add(imageLabel, BorderLayout.CENTER);
        card.add(addButton, BorderLayout.SOUTH);

        return card;
    }

}
