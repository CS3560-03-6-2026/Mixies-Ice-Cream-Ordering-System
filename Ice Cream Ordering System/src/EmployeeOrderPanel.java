import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * EmployeeOrderPanel is a Swing-based UI panel that allows employees
 * to create and manage customer orders in the Mixies Ice Cream system.
 *
 * Features include:
 * - Creating and concluding orders
 * - Adding ice cream items with quantities
 * - Selecting and adding toppings to items
 * - Viewing order details (status, total, items)
 * - Refunding items after order completion
 *
 * This panel interacts with the MixiesService layer to perform all
 * business logic and data operations, while handling user input
 * and displaying results through a graphical interface.
 */
public class EmployeeOrderPanel extends JPanel {

    // Service layer used to access and update order, flavor, and topping data
    private final MixiesService service;

    // The employee currently logged into the system
    private final Employee loggedInEmployee;

    // Dropdown for selecting an ice cream flavor
    private final JComboBox<IceCreamFlavor> flavorBox = new JComboBox<>();

    // Text field for entering quantity of the selected flavor
    private final JTextField quantityField = new JTextField("1", 5);

    // Button to open the topping selection dialog
    private final JButton selectToppingsBtn = new JButton("Select Toppings");

    // Label showing which toppings are currently selected
    private final JLabel selectedToppingsLabel = new JLabel("No toppings selected");

    // Stores the toppings selected by the employee
    private List<Topping> selectedToppings = new ArrayList<>();

    // Label showing the current order's status
    private final JLabel orderStatusLabel = new JLabel("Order Status: None");

    // Label showing the current order's total cost
    private final JLabel orderTotalLabel = new JLabel("Order Total: 0.00");

    // Table model used to display order items in a non-editable JTable
    private final DefaultTableModel orderItemTableModel = new DefaultTableModel(
            new Object[] { "OrderItem ID", "Flavor", "Quantity", "Cost", "Refund Status" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Prevent user from editing table cells directly
        }
    };

    // Table used to display all items in the current order
    private final JTable orderItemTable = new JTable(orderItemTableModel);

    // Stores the ID of the currently active order; -1 means no active order
    private int currentOrderID = -1;

    // Constructor
    public EmployeeOrderPanel(MixiesService service, Employee loggedInEmployee) {
        this.service = service;
        this.loggedInEmployee = loggedInEmployee;

        // Main panel layout
        setLayout(new BorderLayout());

        // Top panel contains main order controls and order info
        JPanel topPanel = new JPanel();
        JButton createOrderBtn = new JButton("Create Order");
        JButton concludeOrderBtn = new JButton("Conclude Order");
        JButton refreshBtn = new JButton("Refresh");

        topPanel.add(createOrderBtn);
        topPanel.add(concludeOrderBtn);
        topPanel.add(refreshBtn);
        topPanel.add(orderStatusLabel);
        topPanel.add(orderTotalLabel);

        // Form panel for selecting flavor, quantity, and toppings
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.add(new JLabel("Flavor:"));
        formPanel.add(flavorBox);
        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(quantityField);
        formPanel.add(new JLabel("Toppings:"));
        formPanel.add(selectToppingsBtn);
        formPanel.add(new JLabel("Selected:"));
        formPanel.add(selectedToppingsLabel);

        // Bottom panel for item-related actions
        JPanel buttonPanel = new JPanel();
        JButton addItemBtn = new JButton("Add Order Item");
        JButton addToppingBtn = new JButton("Add Topping To Selected Item");
        JButton refundBtn = new JButton("Refund Selected Item");

        buttonPanel.add(addItemBtn);
        buttonPanel.add(addToppingBtn);
        buttonPanel.add(refundBtn);

        // Add components to the main panel
        add(topPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.WEST);
        add(new JScrollPane(orderItemTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Connect buttons to their corresponding methods
        createOrderBtn.addActionListener(e -> createOrder());
        concludeOrderBtn.addActionListener(e -> concludeOrder());
        refreshBtn.addActionListener(e -> refreshData());
        addItemBtn.addActionListener(e -> addOrderItem());
        addToppingBtn.addActionListener(e -> addToppingToSelectedItem());
        refundBtn.addActionListener(e -> refundSelectedItem());
        selectToppingsBtn.addActionListener(e -> openToppingSelector());

        // Load initial data into the panel
        refreshData();
    }

    // Reload flavors, table data, and order header info
    private void refreshData() {
        flavorBox.removeAllItems();

        List<IceCreamFlavor> flavors = service.getAllFlavors();
        for (IceCreamFlavor flavor : flavors) {
            flavorBox.addItem(flavor);
        }

        refreshOrderItemsTable();
        refreshOrderHeader();
    }

    // Update the labels showing the order status and total
    private void refreshOrderHeader() {
        if (currentOrderID == -1) {
            orderStatusLabel.setText("Order Status: None");
            orderTotalLabel.setText("Order Total: 0.00");
            return;
        }

        Order order = service.getOrder(currentOrderID);
        if (order != null) {
            orderStatusLabel.setText("Order Status: " + order.getOrderStatus());
            orderTotalLabel.setText(String.format("Order Total: %.2f", order.getTotal()));
        }
    }

    // Reload the order items table for the current order
    private void refreshOrderItemsTable() {
        orderItemTableModel.setRowCount(0); // Clear existing rows

        if (currentOrderID == -1) {
            return; // No order to display
        }

        List<OrderItem> items = service.getOrderItemsForOrder(currentOrderID);
        for (OrderItem item : items) {
            orderItemTableModel.addRow(new Object[] {
                    item.getOrderItemID(),
                    item.getFlavor().getFlavorName(),
                    item.getQuantity(),
                    service.getDisplayedOrderItemCost(item),
                    item.getRefundStatus()
            });
        }
    }

    // Create a new order for the logged-in employee
    private void createOrder() {
        currentOrderID = service.createOrder(loggedInEmployee, 0.0, 0.0);

        if (currentOrderID != -1) {
            JOptionPane.showMessageDialog(this, "Order created. ID: " + currentOrderID);
            refreshData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create order.");
        }
    }

    // Conclude the current order if it has at least one item
    private void concludeOrder() {
        if (currentOrderID == -1) {
            JOptionPane.showMessageDialog(this, "Create an order first.");
            return;
        }

        List<OrderItem> items = service.getOrderItemsForOrder(currentOrderID);
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cannot conclude an order with no items.");
            return;
        }

        boolean ok = service.concludeOrder(currentOrderID);
        JOptionPane.showMessageDialog(this, ok ? "Order concluded." : "Could not conclude order.");
        refreshData();
    }

    // Add a new ice cream item to the current order
    private void addOrderItem() {
        if (currentOrderID == -1) {
            JOptionPane.showMessageDialog(this, "Create an order first.");
            return;
        }

        // Make sure order is still open
        Order currentOrder = service.getOrder(currentOrderID);
        if (currentOrder == null || !"Ordering".equalsIgnoreCase(currentOrder.getOrderStatus())) {
            JOptionPane.showMessageDialog(this, "Cannot add items. Order is already completed.");
            return;
        }

        // Get selected flavor
        IceCreamFlavor flavor = (IceCreamFlavor) flavorBox.getSelectedItem();
        if (flavor == null) {
            JOptionPane.showMessageDialog(this, "Select a flavor.");
            return;
        }

        try {
            // Parse quantity input
            int quantity = Integer.parseInt(quantityField.getText().trim());

            // Prevent adding out-of-stock flavor
            if (flavor.isOutOfStock()) {
                JOptionPane.showMessageDialog(this, "This flavor is out of stock and cannot be added.");
                return;
            }

            // Add item to order through the service
            int orderItemID = service.addOrderItem(currentOrderID, flavor.getFlavorID(), quantity);

            if (orderItemID == -1) {
                JOptionPane.showMessageDialog(this, "Could not add item. Not enough stock or unavailable.");
                return;
            }

            // Reload updated flavor after stock changes
            IceCreamFlavor updatedFlavor = service.getFlavor(flavor.getFlavorID());

            // Warn if stock is now low
            if (updatedFlavor != null && updatedFlavor.isLowStock()) {
                JOptionPane.showMessageDialog(
                        this,
                        updatedFlavor.getFlavorName() + " has reached the remake threshold.",
                        "Low Stock Warning",
                        JOptionPane.WARNING_MESSAGE);
            }

            // Warn if flavor is now out of stock
            if (updatedFlavor != null && updatedFlavor.isOutOfStock()) {
                JOptionPane.showMessageDialog(
                        this,
                        updatedFlavor.getFlavorName() + " is now out of stock.",
                        "Out Of Stock",
                        JOptionPane.WARNING_MESSAGE);
            }

            refreshData();

        } catch (NumberFormatException ex) {
            // Handle invalid quantity input
            JOptionPane.showMessageDialog(this, "Quantity must be a valid number.");
        }
    }

    // Open a dialog allowing the employee to choose multiple toppings
    private void openToppingSelector() {
        List<Topping> allToppings = service.getAllToppings();

        // Create a list model to hold all toppings
        DefaultListModel<Topping> model = new DefaultListModel<>();
        for (Topping topping : allToppings) {
            model.addElement(topping);
        }

        // Create JList for multi-selection
        JList<Topping> toppingJList = new JList<>(model);
        toppingJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Show selection dialog
        int result = JOptionPane.showConfirmDialog(
                this,
                new JScrollPane(toppingJList),
                "Select Toppings",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        // Save selected toppings if user clicked OK
        if (result == JOptionPane.OK_OPTION) {
            selectedToppings = toppingJList.getSelectedValuesList();

            if (selectedToppings.isEmpty()) {
                selectedToppingsLabel.setText("No toppings selected");
            } else {
                // Build comma-separated list of topping names
                String names = selectedToppings.stream()
                        .map(Topping::getToppingName)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("No toppings selected");
                selectedToppingsLabel.setText(names);
            }
        }
    }

    // Add selected toppings to the currently selected order item
    private void addToppingToSelectedItem() {
        if (currentOrderID == -1) {
            JOptionPane.showMessageDialog(this, "Create an order first.");
            return;
        }

        // Order must still be open
        Order currentOrder = service.getOrder(currentOrderID);
        if (currentOrder == null || !"Ordering".equalsIgnoreCase(currentOrder.getOrderStatus())) {
            JOptionPane.showMessageDialog(this, "Cannot add toppings. Order is already completed.");
            return;
        }

        // Make sure an order item is selected
        int selectedRow = orderItemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an order item first.");
            return;
        }

        // Make sure toppings were selected first
        if (selectedToppings.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select at least one topping first.");
            return;
        }

        // Get the selected order item's ID from the table
        int orderItemID = (int) orderItemTableModel.getValueAt(selectedRow, 0);

        // Try to add all selected toppings
        boolean allAdded = true;
        for (Topping topping : selectedToppings) {
            boolean ok = service.addOrderItemTopping(orderItemID, topping.getToppingID(), 1);
            if (!ok) {
                allAdded = false;
            }
        }

        JOptionPane.showMessageDialog(this,
                allAdded ? "Toppings added." : "Some toppings could not be added.");

        // Clear topping selection after use
        selectedToppings.clear();
        selectedToppingsLabel.setText("No toppings selected");
        refreshData();
    }

    // Refund the selected order item, but only after the order has been completed
    private void refundSelectedItem() {
        if (currentOrderID == -1) {
            JOptionPane.showMessageDialog(this, "No active order.");
            return;
        }

        // Refunds are only allowed on completed orders
        Order currentOrder = service.getOrder(currentOrderID);
        if (currentOrder == null || !"Completed".equalsIgnoreCase(currentOrder.getOrderStatus())) {
            JOptionPane.showMessageDialog(this, "Items can only be refunded after the order is concluded.");
            return;
        }

        // Make sure a row is selected
        int selectedRow = orderItemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an order item first.");
            return;
        }

        // Get selected order item ID and process refund
        int orderItemID = (int) orderItemTableModel.getValueAt(selectedRow, 0);
        boolean ok = service.refundOrderItem(orderItemID, currentOrderID);

        JOptionPane.showMessageDialog(this, ok ? "Item refunded." : "Refund failed.");
        refreshData();
    }
}