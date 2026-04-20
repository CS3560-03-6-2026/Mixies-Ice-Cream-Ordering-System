import javax.swing.*;
import java.awt.*;

public class FlavorListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus
        );

        if (value instanceof IceCreamFlavor flavor) {
            String text = flavor.getFlavorName()
                    + " | Stock: " + flavor.getStockLevel()
                    + " | " + flavor.getAvailabilityStatus();

            if (flavor.isOutOfStock()) {
                text += " [OUT]";
                label.setForeground(isSelected ? Color.WHITE : Color.RED);
            } else if (flavor.isLowStock()) {
                text += " [REMAKE]";
                label.setForeground(isSelected ? Color.WHITE : new Color(200, 120, 0));
            }

            label.setText(text);
        }

        return label;
    }
}