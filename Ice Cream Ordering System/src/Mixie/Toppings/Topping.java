package Mixie.Toppings;
/**
 * Topping represents a topping that can be added to an ice cream order.
 * 
 * It stores:
 * - A unique topping ID
 * - The name of the topping
 * 
 * This class is used throughout the system to display and manage
 * available toppings for order items.
 */
public class Topping {

    // Unique identifier for the topping
    private final int toppingID;

    // Name of the topping (e.g., sprinkles, chocolate chips)
    private final String toppingName;

    /**
     * Constructor initializes the topping with its ID and name.
     */
    public Topping(int toppingID, String toppingName) {
        this.toppingID = toppingID;
        this.toppingName = toppingName;
    }

    // Returns the topping ID
    public int getToppingID() {
        return toppingID;
    }

    // Returns the topping name
    public String getToppingName() {
        return toppingName;
    }

    /**
     * Returns the topping name when the object is printed.
     * This is useful for displaying toppings in UI components
     * like JComboBox or JList.
     */
    @Override
    public String toString() {
        return toppingName;
    }
}