package Mixie.Flavor;
/**
 * IceCreamFlavor represents a single ice cream flavor in the system.
 * 
 * It stores information such as:
 * - Flavor ID and name
 * - Seasonality (e.g., seasonal or year-round)
 * - Current stock level
 * - Threshold for when the flavor should be remade
 * - Allergen information
 * - Availability status
 * 
 * This class also provides helper methods to determine if a flavor
 * is low in stock or completely out of stock.
 */
public class IceCreamFlavor {

    // Unique identifier for the flavor
    private final int flavorID;

    // Name of the ice cream flavor
    private final String flavorName;

    // Indicates when the flavor is available (e.g., seasonal)
    private final String seasonality;

    // Current number of units available in stock
    private final int stockLevel;

    // Threshold at which the flavor should be remade
    private final int remakeThreshold;

    // Allergen information for the flavor
    private final String allergens;

    // Current availability status (e.g., Available, Unavailable)
    private final String availabilityStatus;

    /**
     * Constructor to initialize all fields of an IceCreamFlavor object
     */
    public IceCreamFlavor(int flavorID, String flavorName, String seasonality,
            int stockLevel, int remakeThreshold,
            String allergens, String availabilityStatus) {

        this.flavorID = flavorID;
        this.flavorName = flavorName;
        this.seasonality = seasonality;
        this.stockLevel = stockLevel;
        this.remakeThreshold = remakeThreshold;
        this.allergens = allergens;
        this.availabilityStatus = availabilityStatus;
    }

    // Returns the flavor ID
    public int getFlavorID() {
        return flavorID;
    }

    // Returns the flavor name
    public String getFlavorName() {
        return flavorName;
    }

    // Returns the seasonality of the flavor
    public String getSeasonality() {
        return seasonality;
    }

    // Returns the current stock level
    public int getStockLevel() {
        return stockLevel;
    }

    // Returns the remake threshold
    public int getRemakeThreshold() {
        return remakeThreshold;
    }

    // Returns allergen information
    public String getAllergens() {
        return allergens;
    }

    // Returns the availability status
    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    /**
     * Checks if the flavor is low in stock.
     * A flavor is considered low stock if its stock level
     * is less than or equal to the remake threshold but greater than zero.
     */
    public boolean isLowStock() {
        return stockLevel <= remakeThreshold && stockLevel > 0;
    }

    /**
     * Checks if the flavor is completely out of stock.
     */
    public boolean isOutOfStock() {
        return stockLevel == 0;
    }

    /**
     * Returns the flavor name when the object is printed,
     * which is useful for displaying in UI components like JComboBox.
     */
    @Override
    public String toString() {
        return flavorName;
    }
}