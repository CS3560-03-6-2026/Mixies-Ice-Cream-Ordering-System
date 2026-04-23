import java.util.List;

public class MixiesService {
    public static final double TOPPING_PRICE = 0.50;
    public static final double SCOOP_PRICE = 3.50;
    private final FlavorDAO flavorDAO = new FlavorDAO();
    private final ToppingDAO toppingDAO = new ToppingDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    public Employee getEmployeeById(int employeeID) {
        return employeeDAO.getEmployeeById(employeeID);
    }

    public boolean canAccessManagerScreen(int employeeID) {
        Employee employee = employeeDAO.getEmployeeById(employeeID);
        return employee != null && employee.getEmployeeRole() == employeeRoles.MANAGER;
    }

    public List<IceCreamFlavor> getAllFlavors() {
        return flavorDAO.getAllFlavors();
    }

    public List<Topping> getAllToppings() {
        return toppingDAO.getAllToppings();
    }

    public List<OrderItem> getOrderItemsForOrder(int orderID) {
        return orderDAO.getOrderItemsForOrder(orderID, flavorDAO);
    }

    public Order getOrder(int orderID) {
        return orderDAO.getOrderById(orderID);
    }

    public boolean createFlavor(Employee employee, String flavorName, String seasonality,
            int stockLevel, int remakeThreshold,
            String allergens, String availabilityStatus) {
        if (!isManager(employee))
            return false;
        return flavorDAO.createFlavor(flavorName, seasonality, stockLevel, remakeThreshold, allergens,
                availabilityStatus);
    }

    public boolean updateFlavorAvailability(Employee employee, int flavorID, String newAvailability) {
        if (!isManager(employee))
            return false;
        return flavorDAO.updateFlavorAvailability(flavorID, newAvailability);
    }

    public boolean updateFlavorSeasonality(Employee employee, int flavorID, String newSeasonality) {
        if (!isManager(employee))
            return false;
        return flavorDAO.updateFlavorSeasonality(flavorID, newSeasonality);
    }

    public boolean updateFlavorStock(Employee employee, int flavorID, int newStock) {
        if (!isManager(employee)) {
            return false;
        }
        return flavorDAO.updateFlavorStock(flavorID, newStock);
    }

    public boolean removeFlavor(Employee employee, int flavorID) {
        if (!isManager(employee))
            return false;
        return flavorDAO.removeFlavor(flavorID);
    }

    public boolean createTopping(Employee employee, String toppingName) {
        if (!isManager(employee))
            return false;
        return toppingDAO.createTopping(toppingName);
    }

    public boolean removeTopping(Employee employee, int toppingID) {
        if (!isManager(employee))
            return false;
        return toppingDAO.removeTopping(toppingID);
    }

    public int createOrder(Employee employee, double tip, double total) {
        return orderDAO.createOrder(employee.getEmployeeID(), tip, total);
    }

    public int addOrderItem(int orderID, int flavorID, int quantity) {
        Order order = orderDAO.getOrderById(orderID);
        if (order == null || !"Open".equalsIgnoreCase(order.getOrderStatus())) {
            return -1;
        }

        IceCreamFlavor flavor = flavorDAO.getFlavorById(flavorID);
        if (flavor == null || flavor.isOutOfStock()) {
            return -1;
        }

        boolean stockReduced = flavorDAO.decreaseStock(flavorID, quantity);
        if (!stockReduced) {
            return -1;
        }

        double itemCost = SCOOP_PRICE;
        int orderItemID = orderDAO.addOrderItem(orderID, flavorID, quantity, itemCost);

        if (orderItemID != -1) {
            refreshOrderTotal(orderID);
        }

        return orderItemID;
    }

    public boolean addOrderItemTopping(int orderItemID, int toppingID, int toppingQuantity) {
        boolean added = orderDAO.addOrderItemTopping(orderItemID, toppingID, toppingQuantity);

        if (added) {
            Integer orderID = orderDAO.getOrderIdByOrderItemId(orderItemID);
            if (orderID != null) {
                refreshOrderTotal(orderID);
            }
        }

        return added;
    }

    public double getDisplayedOrderItemCost(OrderItem item) {
        double baseCost = item.getQuantity() * item.getItemCost();
        double toppingCost = orderDAO.calculateToppingTotalForOrderItem(item.getOrderItemID());
        return baseCost + toppingCost;
    }

    public boolean refundOrderItem(int orderItemID, int orderID) {
        Order order = orderDAO.getOrderById(orderID);
        if (order == null || !"Completed".equalsIgnoreCase(order.getOrderStatus())) {
            return false;
        }

        boolean refunded = orderDAO.refundOrderItem(orderItemID);
        if (refunded) {
            refreshOrderTotal(orderID);
        }
        return refunded;
    }

    public boolean concludeOrder(int orderID) {
        refreshOrderTotal(orderID);
        return orderDAO.concludeOrder(orderID);
    }

    public void refreshOrderTotal(int orderID) {
        double itemTotal = orderDAO.calculateOrderTotal(orderID);
        double toppingTotal = orderDAO.calculateOrderToppingTotal(orderID);
        double total = itemTotal + toppingTotal;
        orderDAO.updateOrderTotal(orderID, total);
    }

    public IceCreamFlavor getFlavor(int flavorID) {
        return flavorDAO.getFlavorById(flavorID);
    }

    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }

    private boolean isManager(Employee employee) {
        return employee != null && (employee.getEmployeeRole() == employeeRoles.MANAGER);
    }
}
