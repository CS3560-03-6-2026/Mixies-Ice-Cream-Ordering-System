import java.util.List;

/**
 * MixiesService acts as the business logic layer of the application.
 * 
 * It connects the UI (panels) with the DAO (data access) layer and
 * handles all core operations such as:
 * - Managing flavors and toppings
 * - Creating and updating orders
 * - Calculating totals and enforcing rules
 * - Validating manager permissions
 */
public class MixiesService {

    // Data Access Objects (DAO) for interacting with the database
    private final FlavorDAO flavorDAO = new FlavorDAO();
    private final ToppingDAO toppingDAO = new ToppingDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    /**
     * Retrieves an employee by their ID.
     */
    public Employee getEmployeeById(int employeeID) {
        return employeeDAO.getEmployeeById(employeeID);
    }

    /**
     * Checks if an employee has permission to access manager features.
     */
    public boolean canAccessManagerScreen(int employeeID) {
        Employee employee = employeeDAO.getEmployeeById(employeeID);
        return employee != null && employee.getEmployeeRole() == employeeRoles.MANAGER;
    }

    /**
     * Returns all ice cream flavors.
     */
    public List<IceCreamFlavor> getAllFlavors() {
        return flavorDAO.getAllFlavors();
    }

    /**
     * Returns all toppings.
     */
    public List<Topping> getAllToppings() {
        return toppingDAO.getAllToppings();
    }

    /**
     * Retrieves all order items for a specific order.
     */
    public List<OrderItem> getOrderItemsForOrder(int orderID) {
        return orderDAO.getOrderItemsForOrder(orderID, flavorDAO);
    }

    /**
     * Retrieves all order items for a specific order, including their toppings.
     */
    public List<OrderItem> getOrderItemsWithToppingsForOrder(int orderID) {
        return orderDAO.getOrderItemsWithToppingsForOrder(orderID, flavorDAO, toppingDAO);
    }

    /**
     * Retrieves a specific order by ID.
     */
    public Order getOrder(int orderID) {
        return orderDAO.getOrderById(orderID);
    }

    /**
     * Creates a new flavor (manager only).
     */
    public boolean createFlavor(Employee employee, String flavorName, String seasonality,
            int stockLevel, int remakeThreshold,
            String allergens, String availabilityStatus) {

        if (!isManager(employee))
            return false;

        return flavorDAO.createFlavor(
                flavorName, seasonality, stockLevel,
                remakeThreshold, allergens, availabilityStatus);
    }

    /**
     * Updates flavor availability (manager only).
     */
    public boolean updateFlavorAvailability(Employee employee, int flavorID, String newAvailability) {
        if (!isManager(employee))
            return false;

        return flavorDAO.updateFlavorAvailability(flavorID, newAvailability);
    }

    /**
     * Updates flavor seasonality (manager only).
     */
    public boolean updateFlavorSeasonality(Employee employee, int flavorID, String newSeasonality) {
        if (!isManager(employee))
            return false;

        return flavorDAO.updateFlavorSeasonality(flavorID, newSeasonality);
    }

    /**
     * Updates flavor stock level (manager only).
     */
    public boolean updateFlavorStock(Employee employee, int flavorID, int newStock) {
        if (!isManager(employee)) {
            return false;
        }
        return flavorDAO.updateFlavorStock(flavorID, newStock);
    }

    /**
     * Removes a flavor (manager only).
     */
    public boolean removeFlavor(Employee employee, int flavorID) {
        if (!isManager(employee))
            return false;

        return flavorDAO.removeFlavor(flavorID);
    }

    /**
     * Creates a new topping (manager only).
     */
    public boolean createTopping(Employee employee, String toppingName) {
        if (!isManager(employee))
            return false;

        return toppingDAO.createTopping(toppingName);
    }

    /**
     * Removes a topping (manager only).
     */
    public boolean removeTopping(Employee employee, int toppingID) {
        if (!isManager(employee))
            return false;

        return toppingDAO.removeTopping(toppingID);
    }

    /**
     * Creates a new order for an employee.
     */
    public int createOrder(Employee employee, double tip, double total) {
        return orderDAO.createOrder(employee.getEmployeeID(), tip, total);
    }

    /**
     * Adds an order item to an existing order.
     * Also handles stock reduction and total recalculation.
     */
    public int addOrderItem(int orderID, int flavorID, int quantity) {

        // Ensure order exists and has ordering status
        Order order = orderDAO.getOrderById(orderID);
        if (order == null || !"Ordering".equalsIgnoreCase(order.getOrderStatus())) {
            return -1;
        }

        // Check flavor availability
        IceCreamFlavor flavor = flavorDAO.getFlavorById(flavorID);
        if (flavor == null || flavor.isOutOfStock()) {
            return -1;
        }

        // Reduce stock before adding item
        boolean stockReduced = flavorDAO.decreaseStock(flavorID, quantity);
        if (!stockReduced) {
            return -1;
        }

        // Add item with base scoop price
        double itemCost = Prices.SCOOP_PRICES[quantity];
        int orderItemID = orderDAO.addOrderItem(orderID, flavorID, quantity, itemCost);

        // Update order total if successful
        if (orderItemID != -1) {
            refreshOrderTotal(orderID);
        }

        return orderItemID;
    }

    /**
     * Adds a topping to an order item and updates the total.
     */
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

    /**
     * Calculates the displayed cost of an order item (including toppings).
     */
    public double getDisplayedOrderItemCost(OrderItem item) {
        double baseCost = Prices.SCOOP_PRICES[item.getQuantity()];
        double toppingCost = orderDAO.calculateToppingTotalForOrderItem(item.getOrderItemID());
        return baseCost + toppingCost;
    }

    /**
     * Refunds an order item if the order is completed.
     */
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

    public boolean checkoutOrder(int orderID) {
        refreshOrderTotal(orderID);
        return orderDAO.checkoutOrder(orderID);
    }

    /**
     * Finalizes an order and updates the total before closing it.
     */
    public boolean concludeOrder(int orderID) {
        refreshOrderTotal(orderID);
        return orderDAO.concludeOrder(orderID);
    }

    public boolean cancelOrder(int orderID) {
        return orderDAO.cancelOrder(orderID);
    }

    public boolean timeoutOrder(int orderID) {
        return orderDAO.timeoutOrder(orderID);
    }

    /**
     * Calculates the total for an order and returns it.
     */
    public double getOrderTotal(int orderID) {
        double itemTotal = orderDAO.calculateOrderTotal(orderID);
        double toppingTotal = orderDAO.calculateOrderToppingTotal(orderID);
        double tip = orderDAO.getOrderTip(orderID);
        double subtotal = itemTotal + toppingTotal;
        double tax = subtotal * Prices.TAX_RATE;
        return itemTotal + toppingTotal + tax + tip;
    }

    /**
     * Recalculates and updates the total cost of an order.
     */
    public void refreshOrderTotal(int orderID) {
        orderDAO.updateOrderTotal(orderID, getOrderTotal(orderID));
    }

    public boolean updateOrderTip(int orderID, double tip) {
        return orderDAO.updateOrderTip(orderID, tip);
    }

    /**
     * Retrieves a flavor by ID.
     */
    public IceCreamFlavor getFlavor(int flavorID) {
        return flavorDAO.getFlavorById(flavorID);
    }

    /**
     * Retrieves all orders.
     */
    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }

    /**
     * Helper method to check if an employee is a manager.
     */
    private boolean isManager(Employee employee) {
        return employee != null &&
                (employee.getEmployeeRole() == employeeRoles.MANAGER);
    }

    public boolean removeOrderItem(int orderItemID, int orderID) {
        boolean removed = orderDAO.removeOrderItem(orderItemID);

        if (removed) {
            refreshOrderTotal(orderID);
        }

        return removed;
    }
}