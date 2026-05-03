package Mixie.Orders;
import Mixie.Toppings.Topping;

/**
 * OrderItemTopping represents a topping added to a specific order item.
 * 
 * It stores:
 * - The ID of the order item the topping belongs to
 * - The topping itself
 * - The quantity of that topping
 * 
 * This class is used to associate toppings with order items
 * and helps calculate additional costs.
 */
public class OrderItemTopping {

    // ID of the order item this topping is attached to
    private final int orderItemID;

    // The topping object (e.g., sprinkles, chocolate chips)
    private final Topping topping;

    // Quantity of this topping added to the order item
    private final int toppingQuantity;

    /**
     * Constructor initializes all fields for an OrderItemTopping.
     */
    public OrderItemTopping(int orderItemID, Topping topping, int toppingQuantity) {
        this.orderItemID = orderItemID;
        this.topping = topping;
        this.toppingQuantity = toppingQuantity;
    }

    // Returns the order item ID
    public int getOrderItemID() {
        return orderItemID;
    }

    // Returns the topping object
    public Topping getTopping() {
        return topping;
    }

    // Returns the quantity of the topping
    public int getToppingQuantity() {
        return toppingQuantity;
    }
}