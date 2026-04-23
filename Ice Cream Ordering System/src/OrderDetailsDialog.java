import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderDetailsDialog extends JDialog {
    private final MixiesService service;
    private final int orderID;

    private final DefaultTableModel itemsTableModel = new DefaultTableModel(
            new Object[] { "OrderItem ID", "Flavor", "Quantity", "Cost", "Refund Status" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable itemsTable = new JTable(itemsTableModel);

    public OrderDetailsDialog(Frame owner, MixiesService service, int orderID) {
        super(owner, "Order Details - " + orderID, true);
        this.service = service;
        this.orderID = orderID;

        setSize(700, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JButton refundButton = new JButton("Refund Selected Item");
        JButton refreshButton = new JButton("Refresh");

        JPanel top = new JPanel();
        top.add(refreshButton);
        top.add(refundButton);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(itemsTable), BorderLayout.CENTER);

        refreshButton.addActionListener(e -> refreshItems());
        refundButton.addActionListener(e -> refundSelectedItem());

        refreshItems();
    }

    private void refreshItems() {
        itemsTableModel.setRowCount(0);

        List<OrderItem> items = service.getOrderItemsForOrder(orderID);
        for (OrderItem item : items) {
            itemsTableModel.addRow(new Object[] {
                    item.getOrderItemID(),
                    item.getFlavor().getFlavorName(),
                    item.getQuantity(),
                    service.getDisplayedOrderItemCost(item),
                    item.getRefundStatus()
            });
        }
    }

    private void refundSelectedItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an item first.");
            return;
        }

        int orderItemID = (int) itemsTableModel.getValueAt(selectedRow, 0);
        boolean ok = service.refundOrderItem(orderItemID, orderID);

        JOptionPane.showMessageDialog(this, ok ? "Item refunded." : "Refund failed.");
        refreshItems();
    }
}