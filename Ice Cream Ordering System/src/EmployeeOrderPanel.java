import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmployeeOrderPanel extends JPanel {
    private final MixiesService service;
    private final Employee loggedInEmployee;

    private final JComboBox<IceCreamFlavor> flavorBox = new JComboBox<>();
    private final JComboBox<Topping> toppingBox = new JComboBox<>();
    private final JTextField quantityField = new JTextField("1", 5);
    private final JTextField itemCostField = new JTextField("3.50", 5);
    private final JTextField toppingQtyField = new JTextField("1", 5);

    private final JLabel orderStatusLabel = new JLabel("Order Status: None");
    private final JLabel orderTotalLabel = new JLabel("Order Total: 0.00");

    private final DefaultTableModel orderItemTableModel = new DefaultTableModel(
            new Object[]{"OrderItem ID", "Flavor", "Quantity", "Cost", "Refund Status"}, 0
    ) {
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

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.add(new JLabel("Flavor:"));
        formPanel.add(flavorBox);
        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(quantityField);
        formPanel.add(new JLabel("Item Cost:"));
        formPanel.add(itemCostField);
        formPanel.add(new JLabel("Topping:"));
        formPanel.add(toppingBox);
        formPanel.add(new JLabel("Topping Quantity:"));
        formPanel.add(toppingQtyField);

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

        refreshData();
    }

    private void refreshData() {
        flavorBox.removeAllItems();
        toppingBox.removeAllItems();

        List<IceCreamFlavor> flavors = service.getAllFlavors();
        for (IceCreamFlavor flavor : flavors) {
            flavorBox.addItem(flavor);
        }

        for (Topping topping : service.getAllToppings()) {
            toppingBox.addItem(topping);
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

        if (currentOrderID == -1) return;

        List<OrderItem> items = service.getOrderItemsForOrder(currentOrderID);
        for (OrderItem item : items) {
            orderItemTableModel.addRow(new Object[]{
                    item.getOrderItemID(),
                    item.getFlavor().getFlavorName(),
                    item.getQuantity(),
                    item.getItemCost(),
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
            double itemCost = Double.parseDouble(itemCostField.getText().trim());

            if (flavor.isOutOfStock()) {
                JOptionPane.showMessageDialog(this, "This flavor is out of stock and cannot be added.");
                return;
            }

            int orderItemID = service.addOrderItem(currentOrderID, flavor.getFlavorID(), quantity, itemCost);

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
                        JOptionPane.WARNING_MESSAGE
                );
            }

            if (updatedFlavor != null && updatedFlavor.isOutOfStock()) {
                JOptionPane.showMessageDialog(
                        this,
                        updatedFlavor.getFlavorName() + " is now out of stock.",
                        "Out Of Stock",
                        JOptionPane.WARNING_MESSAGE
                );
            }

            refreshData();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity and cost must be valid numbers.");
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

        Topping topping = (Topping) toppingBox.getSelectedItem();
        if (topping == null) {
            JOptionPane.showMessageDialog(this, "Select a topping.");
            return;
        }

        try {
            int toppingQty = Integer.parseInt(toppingQtyField.getText().trim());
            int orderItemID = (int) orderItemTableModel.getValueAt(selectedRow, 0);

            boolean ok = service.addOrderItemTopping(orderItemID, topping.getToppingID(), toppingQty);
            JOptionPane.showMessageDialog(this, ok ? "Topping added." : "Failed to add topping.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Topping quantity must be a number.");
        }
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