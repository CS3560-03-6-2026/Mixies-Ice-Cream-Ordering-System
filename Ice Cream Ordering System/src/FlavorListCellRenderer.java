import javax.swing.*;
import java.awt.*;

/**
 * FlavorListCellRenderer customizes how IceCreamFlavor objects are displayed
 * inside a JList. It formats each item to show flavor name, stock level,
 * and availability status, and applies color styling for low or out-of-stock items.
 */
public class FlavorListCellRenderer extends DefaultListCellRenderer {

    /**
     * Overrides the default rendering method to customize each list cell.
     *
     * @param list The JList being rendered
     * @param value The current item (expected to be an IceCreamFlavor)
     * @param index The index of the item in the list
     * @param isSelected Whether the item is currently selected
     * @param cellHasFocus Whether the item has focus
     * @return A Component (JLabel) used to render the list cell
     */
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {

        // Get the default label rendering from the parent class
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus
        );

        // Ensure the value is an IceCreamFlavor before casting
        if (value instanceof IceCreamFlavor flavor) {

            // Build display text with flavor name, stock, and availability
            String text = flavor.getFlavorName()
                    + " | Stock: " + flavor.getStockLevel()
                    + " | " + flavor.getAvailabilityStatus();

            // If flavor is out of stock, mark and color it red
            if (flavor.isOutOfStock()) {
                text += " [OUT]";
                label.setForeground(isSelected ? Color.WHITE : Color.RED);

            // If flavor is low stock, mark and color it orange
            } else if (flavor.isLowStock()) {
                text += " [REMAKE]";
                label.setForeground(isSelected ? Color.WHITE : new Color(200, 120, 0));
            }

            // Set the final formatted text on the label
            label.setText(text);
        }

        // Return the customized label component
        return label;
    }
}