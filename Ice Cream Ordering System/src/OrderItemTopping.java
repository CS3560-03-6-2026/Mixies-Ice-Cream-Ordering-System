public class OrderItemTopping {
    private final int orderItemID;
    private final Topping topping;
    private final int toppingQuantity;

    public OrderItemTopping(int orderItemID, Topping topping, int toppingQuantity) {
        this.orderItemID = orderItemID;
        this.topping = topping;
        this.toppingQuantity = toppingQuantity;
    }

    public int getOrderItemID() {
        return orderItemID;
    }

    public Topping getTopping() {
        return topping;
    }

    public int getToppingQuantity() {
        return toppingQuantity;
    }
}