import java.util.ArrayList;
import java.util.List;

/**
 * OrderItem represents a single item within an order.
 * 
 * It contains:
 * - The associated flavor
 * - Quantity ordered
 * - Cost per item
 * - Refund status
 * - Any toppings added to the item
 * 
 * This class is used to model individual items in an order
 * and is typically retrieved from the database via the DAO layer.
 */
public class OrderItem {

    // Unique identifier for this order item
    private final int orderItemID;

    // ID of the order this item belongs to
    private final int orderID;

    // The ice cream flavor associated with this item
    private final IceCreamFlavor flavor;

    // Quantity of this flavor ordered
    private final int quantity;

    // Cost per item (base price before toppings)
    private final double itemCost;

    // Refund status (e.g., "Not Refunded", "Refunded")
    private final String refundStatus;

    // List of toppings added to this order item
    private final List<OrderItemTopping> toppings;

    /**
     * Constructor initializes all fields and creates an empty list for toppings.
     */
    public OrderItem(int orderItemID, int orderID, IceCreamFlavor flavor,
                     int quantity, double itemCost, String refundStatus) {
        this.orderItemID = orderItemID;
        this.orderID = orderID;
        this.flavor = flavor;
        this.quantity = quantity;
        this.itemCost = itemCost;
        this.refundStatus = refundStatus;
        this.toppings = new ArrayList<>();
    }

    /**
     * Overloaded constructor allows initializing with a list of toppings.
     */
    public OrderItem(int orderItemID, int orderID, IceCreamFlavor flavor,
                     int quantity, double itemCost, String refundStatus, List<OrderItemTopping> toppings) {
        this.orderItemID = orderItemID;
        this.orderID = orderID;
        this.flavor = flavor;
        this.quantity = quantity;
        this.itemCost = itemCost;
        this.refundStatus = refundStatus;
        this.toppings = toppings;
    }

    // Returns the order item ID
    public int getOrderItemID() {
        return orderItemID;
    }

    // Returns the order ID this item belongs to
    public int getOrderID() {
        return orderID;
    }

    // Returns the flavor of this order item
    public IceCreamFlavor getFlavor() {
        return flavor;
    }

    // Returns the quantity ordered
    public int getQuantity() {
        return quantity;
    }

    // Returns the base cost per item
    public double getItemCost() {
        return itemCost;
    }

    // Returns the refund status
    public String getRefundStatus() {
        return refundStatus;
    }

    // Returns the list of toppings for this item
    public List<OrderItemTopping> getToppings() {
        return toppings;
    }

    /**
     * Adds a topping to this order item.
     */
    public void addTopping(OrderItemTopping topping) {
        toppings.add(topping);
    }

    /**
     * Placeholder method (currently unused).
     * Could be removed or implemented depending on future needs.
     */
    public void addItem(IceCreamFlavor flavor) {
    }
}