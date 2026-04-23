import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrdersPanel extends JPanel {
    private final MixiesService service;

    private final DefaultTableModel ordersTableModel = new DefaultTableModel(
            new Object[] { "Order ID", "Total", "Status" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable ordersTable = new JTable(ordersTableModel);

    public OrdersPanel(MixiesService service) {
        this.service = service;
        setLayout(new BorderLayout());

        JButton refreshButton = new JButton("Refresh Orders");
        JButton viewDetailsButton = new JButton("View Order Details");

        JPanel top = new JPanel();
        top.add(refreshButton);
        top.add(viewDetailsButton);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(ordersTable), BorderLayout.CENTER);

        refreshButton.addActionListener(e -> refreshOrders());
        viewDetailsButton.addActionListener(e -> viewSelectedOrder());

        refreshOrders();
    }

    private void refreshOrders() {
        ordersTableModel.setRowCount(0);

        List<Order> orders = service.getAllOrders();
        for (Order order : orders) {
            ordersTableModel.addRow(new Object[] {
                    order.getOrderID(),
                    order.getTotal(),
                    order.getOrderStatus()
            });
        }
    }

    private void viewSelectedOrder() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an order first.");
            return;
        }

        int orderID = (int) ordersTableModel.getValueAt(selectedRow, 0);
        new OrderDetailsDialog((Frame) SwingUtilities.getWindowAncestor(this), service, orderID).setVisible(true);
        refreshOrders();
    }
}