public class Topping {
    private final int toppingID;
    private final String toppingName;

    public Topping(int toppingID, String toppingName) {
        this.toppingID = toppingID;
        this.toppingName = toppingName;
    }

    public int getToppingID() {
        return toppingID;
    }

    public String getToppingName() {
        return toppingName;
    }

    @Override
    public String toString() {
        return toppingName;
    }
}