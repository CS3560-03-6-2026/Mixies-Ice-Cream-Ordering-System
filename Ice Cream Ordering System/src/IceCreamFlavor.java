public class IceCreamFlavor {
    private final int flavorID;
    private final String flavorName;
    private final String seasonality;
    private final int stockLevel;
    private final int remakeThreshold;
    private final String allergens;
    private final String availabilityStatus;

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

    public int getFlavorID() {
        return flavorID;
    }

    public String getFlavorName() {
        return flavorName;
    }

    public String getSeasonality() {
        return seasonality;
    }

    public int getStockLevel() {
        return stockLevel;
    }

    public int getRemakeThreshold() {
        return remakeThreshold;
    }

    public String getAllergens() {
        return allergens;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public boolean isLowStock() {
        return stockLevel <= remakeThreshold && stockLevel > 0;
    }

    public boolean isOutOfStock() {
        return stockLevel <= 0 || "Unavailable".equalsIgnoreCase(availabilityStatus);
    }

    @Override
    public String toString() {
        return flavorName;
    }
}