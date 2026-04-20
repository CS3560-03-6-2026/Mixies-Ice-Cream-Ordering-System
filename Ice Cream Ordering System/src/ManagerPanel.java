import javax.swing.*;
import java.awt.*;

public class ManagerPanel extends JPanel {
    private final MixiesService service;
    private final Employee loggedInEmployee;

    private final DefaultListModel<IceCreamFlavor> flavorModel = new DefaultListModel<>();
    private final JList<IceCreamFlavor> flavorList = new JList<>(flavorModel);

    private final DefaultListModel<Topping> toppingModel = new DefaultListModel<>();
    private final JList<Topping> toppingList = new JList<>(toppingModel);

    public ManagerPanel(MixiesService service, Employee loggedInEmployee) {
        this.service = service;
        this.loggedInEmployee = loggedInEmployee;

        setLayout(new BorderLayout());

        flavorList.setCellRenderer(new FlavorListCellRenderer());

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(flavorList),
                new JScrollPane(toppingList)
        );
        splitPane.setResizeWeight(0.7);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        JButton refreshBtn = new JButton("Refresh");
        JButton addFlavorBtn = new JButton("Create Flavor");
        JButton removeFlavorBtn = new JButton("Remove Flavor");
        JButton updateAvailabilityBtn = new JButton("Update Availability");
        JButton updateSeasonalityBtn = new JButton("Update Seasonality");
        JButton addToppingBtn = new JButton("Create Topping");
        JButton removeToppingBtn = new JButton("Remove Topping");

        buttonPanel.add(refreshBtn);
        buttonPanel.add(addFlavorBtn);
        buttonPanel.add(removeFlavorBtn);
        buttonPanel.add(updateAvailabilityBtn);
        buttonPanel.add(updateSeasonalityBtn);
        buttonPanel.add(addToppingBtn);
        buttonPanel.add(removeToppingBtn);

        add(splitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> refreshData());
        addFlavorBtn.addActionListener(e -> createFlavor());
        removeFlavorBtn.addActionListener(e -> removeFlavor());
        updateAvailabilityBtn.addActionListener(e -> updateAvailability());
        updateSeasonalityBtn.addActionListener(e -> updateSeasonality());
        addToppingBtn.addActionListener(e -> createTopping());
        removeToppingBtn.addActionListener(e -> removeTopping());

        refreshData();
    }

    private void refreshData() {
        flavorModel.clear();
        toppingModel.clear();

        for (IceCreamFlavor flavor : service.getAllFlavors()) {
            flavorModel.addElement(flavor);
        }

        for (Topping topping : service.getAllToppings()) {
            toppingModel.addElement(topping);
        }
    }

    private void createFlavor() {
        JTextField nameField = new JTextField();
        JTextField seasonalityField = new JTextField();
        JTextField stockField = new JTextField();
        JTextField thresholdField = new JTextField();
        JTextField allergensField = new JTextField();
        JTextField availabilityField = new JTextField();

        Object[] fields = {
                "Flavor Name:", nameField,
                "Seasonality:", seasonalityField,
                "Stock Level:", stockField,
                "Remake Threshold:", thresholdField,
                "Allergens:", allergensField,
                "Availability:", availabilityField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Create Flavor",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                boolean ok = service.createFlavor(
                        loggedInEmployee,
                        nameField.getText().trim(),
                        seasonalityField.getText().trim(),
                        Integer.parseInt(stockField.getText().trim()),
                        Integer.parseInt(thresholdField.getText().trim()),
                        allergensField.getText().trim(),
                        availabilityField.getText().trim()
                );

                JOptionPane.showMessageDialog(this, ok ? "Flavor created." : "Failed.");
                refreshData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Stock and threshold must be numbers.");
            }
        }
    }

    private void removeFlavor() {
        IceCreamFlavor selected = flavorList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a flavor first.");
            return;
        }

        boolean ok = service.removeFlavor(loggedInEmployee, selected.getFlavorID());
        JOptionPane.showMessageDialog(this, ok ? "Flavor removed." : "Failed. Flavor may be referenced by orders.");
        refreshData();
    }

    private void updateAvailability() {
        IceCreamFlavor selected = flavorList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a flavor first.");
            return;
        }

        String newValue = JOptionPane.showInputDialog(this,
                "Enter new availability:", selected.getAvailabilityStatus());
        if (newValue == null || newValue.isBlank()) return;

        boolean ok = service.updateFlavorAvailability(loggedInEmployee, selected.getFlavorID(), newValue.trim());
        JOptionPane.showMessageDialog(this, ok ? "Availability updated." : "Failed.");
        refreshData();
    }

    private void updateSeasonality() {
        IceCreamFlavor selected = flavorList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a flavor first.");
            return;
        }

        String newValue = JOptionPane.showInputDialog(this,
                "Enter new seasonality:", selected.getSeasonality());
        if (newValue == null || newValue.isBlank()) return;

        boolean ok = service.updateFlavorSeasonality(loggedInEmployee, selected.getFlavorID(), newValue.trim());
        JOptionPane.showMessageDialog(this, ok ? "Seasonality updated." : "Failed.");
        refreshData();
    }

    private void createTopping() {
        String name = JOptionPane.showInputDialog(this, "Enter topping name:");
        if (name == null || name.isBlank()) return;

        boolean ok = service.createTopping(loggedInEmployee, name.trim());
        JOptionPane.showMessageDialog(this, ok ? "Topping created." : "Failed.");
        refreshData();
    }

    private void removeTopping() {
        Topping selected = toppingList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a topping first.");
            return;
        }

        boolean ok = service.removeTopping(loggedInEmployee, selected.getToppingID());
        JOptionPane.showMessageDialog(this, ok ? "Topping removed." : "Failed. Topping may be referenced by orders.");
        refreshData();
    }
}