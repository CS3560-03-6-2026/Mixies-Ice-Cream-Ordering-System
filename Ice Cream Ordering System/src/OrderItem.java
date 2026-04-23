import java.util.ArrayList;
import java.util.List;

public class OrderItem {
    private final int orderItemID;
    private final int orderID;
    private final IceCreamFlavor flavor;
    private final int quantity;
    private final double itemCost;
    private final String refundStatus;
    private final List<OrderItemTopping> toppings;

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

    public int getOrderItemID() {
        return orderItemID;
    }

    public int getOrderID() {
        return orderID;
    }

    public IceCreamFlavor getFlavor() {
        return flavor;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getItemCost() {
        return itemCost;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public List<OrderItemTopping> getToppings() {
        return toppings;
    }

    public void addTopping(OrderItemTopping topping) {
        toppings.add(topping);
    }

    public void addItem(IceCreamFlavor flavor) {
    }
}