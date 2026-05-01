import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order represents a customer order in the system.
 * 
 * It stores:
 * - Order details (ID, employee, date, status)
 * - Financial information (tip, total)
 * - A list of associated OrderItem objects
 * 
 * This class acts as a data model for orders and is used
 * throughout the application to display and manage order data.
 */
public class Order {

    // Unique identifier for the order
    private final int orderID;

    // ID of the employee who created the order
    private final int employeeID;

    // Date the order was created
    private final LocalDateTime orderDate;

    // Tip amount added to the order
    private final double tip;

    // Total cost of the order (including items and toppings)
    private final double total;

    // Current status of the order (e.g., Open, Completed)
    private final String orderStatus;

    // List of items included in the order
    private final List<OrderItem> items;

    /**
     * Constructor initializes all order details and
     * creates an empty list for order items.
     */
    public Order(int orderID, int employeeID, LocalDateTime orderDate, double tip, double total, String orderStatus) {
        this.orderID = orderID;
        this.employeeID = employeeID;
        this.orderDate = orderDate;
        this.tip = tip;
        this.total = total;
        this.orderStatus = orderStatus;
        this.items = new ArrayList<>();
    }

    // Returns the order ID
    public int getOrderID() {
        return orderID;
    }

    // Returns the employee ID associated with the order
    public int getEmployeeID() {
        return employeeID;
    }

    // Returns the order date
    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    // Returns the tip amount
    public double getTip() {
        return tip;
    }

    // Returns the total cost of the order
    public double getTotal() {
        return total;
    }

    // Returns the order status
    public String getOrderStatus() {
        return orderStatus;
    }

    // Returns the list of order items
    public List<OrderItem> getItems() {
        return items;
    }

    /**
     * Adds an OrderItem to this order.
     */
    public void addItem(OrderItem item) {
        items.add(item);
    }
}