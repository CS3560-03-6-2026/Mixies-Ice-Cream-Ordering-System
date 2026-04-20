import java.util.ArrayList;
import java.util.List;

public class Order {
    private final int orderID;
    private final int employeeID;
    private final String orderDate;
    private final double tip;
    private final double total;
    private final String orderStatus;
    private final List<OrderItem> items;

    public Order(int orderID, int employeeID, String orderDate, double tip, double total, String orderStatus) {
        this.orderID = orderID;
        this.employeeID = employeeID;
        this.orderDate = orderDate;
        this.tip = tip;
        this.total = total;
        this.orderStatus = orderStatus;
        this.items = new ArrayList<>();
    }

    public int getOrderID() {
        return orderID;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public double getTip() {
        return tip;
    }

    public double getTotal() {
        return total;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void addItem(OrderItem item) {
        items.add(item);
    }
}