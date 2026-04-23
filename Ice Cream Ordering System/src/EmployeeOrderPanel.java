import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeOrderPanel extends JPanel {
    private final MixiesService service;
    private final Employee loggedInEmployee;

    private final JComboBox<IceCreamFlavor> flavorBox = new JComboBox<>();
    private final JTextField quantityField = new JTextField("1", 5);
    private final JButton selectToppingsBtn = new JButton("Select Toppings");
    private final JLabel selectedToppingsLabel = new JLabel("No toppings selected");
    private List<Topping> selectedToppings = new ArrayList<>();

    private final JLabel orderStatusLabel = new JLabel("Order Status: None");
    private final JLabel orderTotalLabel = new JLabel("Order Total: 0.00");

    private final DefaultTableModel orderItemTableModel = new DefaultTableModel(
            new Object[] { "OrderItem ID", "Flavor", "Quantity", "Cost", "Refund Status" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable orderItemTable = new JTable(orderItemTableModel);
    private int currentOrderID = -1;

    public EmployeeOrderPanel(MixiesService service, Employee loggedInEmployee) {
        this.service = service;
        this.loggedInEmployee = loggedInEmployee;

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JButton createOrderBtn = new JButton("Create Order");
        JButton concludeOrderBtn = new JButton("Conclude Order");
        JButton refreshBtn = new JButton("Refresh");

        topPanel.add(createOrderBtn);
        topPanel.add(concludeOrderBtn);
        topPanel.add(refreshBtn);
        topPanel.add(orderStatusLabel);
        topPanel.add(orderTotalLabel);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.add(new JLabel("Flavor:"));
        formPanel.add(flavorBox);
        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(quantityField);
        formPanel.add(new JLabel("Toppings:"));
        formPanel.add(selectToppingsBtn);
        formPanel.add(new JLabel("Selected:"));
        formPanel.add(selectedToppingsLabel);

        JPanel buttonPanel = new JPanel();
        JButton addItemBtn = new JButton("Add Order Item");
        JButton addToppingBtn = new JButton("Add Topping To Selected Item");
        JButton refundBtn = new JButton("Refund Selected Item");

        buttonPanel.add(addItemBtn);
        buttonPanel.add(addToppingBtn);
        buttonPanel.add(refundBtn);

        add(topPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.WEST);
        add(new JScrollPane(orderItemTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        createOrderBtn.addActionListener(e -> createOrder());
        concludeOrderBtn.addActionListener(e -> concludeOrder());
        refreshBtn.addActionListener(e -> refreshData());
        addItemBtn.addActionListener(e -> addOrderItem());
        addToppingBtn.addActionListener(e -> addToppingToSelectedItem());
        refundBtn.addActionListener(e -> refundSelectedItem());
        selectToppingsBtn.addActionListener(e -> openToppingSelector());

        refreshData();
    }

    private void refreshData() {
        flavorBox.removeAllItems();

        List<IceCreamFlavor> flavors = service.getAllFlavors();
        for (IceCreamFlavor flavor : flavors) {
            flavorBox.addItem(flavor);
        }

        refreshOrderItemsTable();
        refreshOrderHeader();
    }

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

    private void refreshOrderItemsTable() {
        orderItemTableModel.setRowCount(0);

        if (currentOrderID == -1) {
            return;
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

    private void createOrder() {
        currentOrderID = service.createOrder(loggedInEmployee, 0.0, 0.0);

        if (currentOrderID != -1) {
            JOptionPane.showMessageDialog(this, "Order created. ID: " + currentOrderID);
            refreshData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create order.");
        }
    }

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

    private void addOrderItem() {
        if (currentOrderID == -1) {
            JOptionPane.showMessageDialog(this, "Create an order first.");
            return;
        }

        Order currentOrder = service.getOrder(currentOrderID);
        if (currentOrder == null || !"Open".equalsIgnoreCase(currentOrder.getOrderStatus())) {
            JOptionPane.showMessageDialog(this, "Cannot add items. Order is already completed.");
            return;
        }

        IceCreamFlavor flavor = (IceCreamFlavor) flavorBox.getSelectedItem();
        if (flavor == null) {
            JOptionPane.showMessageDialog(this, "Select a flavor.");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());

            if (flavor.isOutOfStock()) {
                JOptionPane.showMessageDialog(this, "This flavor is out of stock and cannot be added.");
                return;
            }

            int orderItemID = service.addOrderItem(currentOrderID, flavor.getFlavorID(), quantity);

            if (orderItemID == -1) {
                JOptionPane.showMessageDialog(this, "Could not add item. Not enough stock or unavailable.");
                return;
            }

            IceCreamFlavor updatedFlavor = service.getFlavor(flavor.getFlavorID());

            if (updatedFlavor != null && updatedFlavor.isLowStock()) {
                JOptionPane.showMessageDialog(
                        this,
                        updatedFlavor.getFlavorName() + " has reached the remake threshold.",
                        "Low Stock Warning",
                        JOptionPane.WARNING_MESSAGE);
            }

            if (updatedFlavor != null && updatedFlavor.isOutOfStock()) {
                JOptionPane.showMessageDialog(
                        this,
                        updatedFlavor.getFlavorName() + " is now out of stock.",
                        "Out Of Stock",
                        JOptionPane.WARNING_MESSAGE);
            }

            refreshData();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must be a valid number.");
        }
    }

    private void openToppingSelector() {
        List<Topping> allToppings = service.getAllToppings();

        DefaultListModel<Topping> model = new DefaultListModel<>();
        for (Topping topping : allToppings) {
            model.addElement(topping);
        }

        JList<Topping> toppingJList = new JList<>(model);
        toppingJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        int result = JOptionPane.showConfirmDialog(
                this,
                new JScrollPane(toppingJList),
                "Select Toppings",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            selectedToppings = toppingJList.getSelectedValuesList();

            if (selectedToppings.isEmpty()) {
                selectedToppingsLabel.setText("No toppings selected");
            } else {
                String names = selectedToppings.stream()
                        .map(Topping::getToppingName)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("No toppings selected");
                selectedToppingsLabel.setText(names);
            }
        }
    }

    private void addToppingToSelectedItem() {
        if (currentOrderID == -1) {
            JOptionPane.showMessageDialog(this, "Create an order first.");
            return;
        }

        Order currentOrder = service.getOrder(currentOrderID);
        if (currentOrder == null || !"Open".equalsIgnoreCase(currentOrder.getOrderStatus())) {
            JOptionPane.showMessageDialog(this, "Cannot add toppings. Order is already completed.");
            return;
        }

        int selectedRow = orderItemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an order item first.");
            return;
        }

        if (selectedToppings.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select at least one topping first.");
            return;
        }

        int orderItemID = (int) orderItemTableModel.getValueAt(selectedRow, 0);

        boolean allAdded = true;
        for (Topping topping : selectedToppings) {
            boolean ok = service.addOrderItemTopping(orderItemID, topping.getToppingID(), 1);
            if (!ok) {
                allAdded = false;
            }
        }

        JOptionPane.showMessageDialog(this,
                allAdded ? "Toppings added." : "Some toppings could not be added.");

        selectedToppings.clear();
        selectedToppingsLabel.setText("No toppings selected");
        refreshData();
    }

    private void refundSelectedItem() {
        if (currentOrderID == -1) {
            JOptionPane.showMessageDialog(this, "No active order.");
            return;
        }

        Order currentOrder = service.getOrder(currentOrderID);
        if (currentOrder == null || !"Completed".equalsIgnoreCase(currentOrder.getOrderStatus())) {
            JOptionPane.showMessageDialog(this, "Items can only be refunded after the order is concluded.");
            return;
        }

        int selectedRow = orderItemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an order item first.");
            return;
        }

        int orderItemID = (int) orderItemTableModel.getValueAt(selectedRow, 0);
        boolean ok = service.refundOrderItem(orderItemID, currentOrderID);

        JOptionPane.showMessageDialog(this, ok ? "Item refunded." : "Refund failed.");
        refreshData();
    }
}