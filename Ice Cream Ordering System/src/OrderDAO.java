import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * OrderDAO handles all database operations related to orders,
 * order items, and toppings associated with order items.
 * 
 * Responsibilities include:
 * - Creating and retrieving orders
 * - Adding order items and toppings
 * - Calculating totals
 * - Updating order status and refunds
 */
public class OrderDAO {

    /**
     * Creates a new order in the database.
     * Automatically sets the order date and initial status to "Open".
     * 
     * @return the generated order ID, or -1 if failed
     */
    public int createOrder(int employeeID, double tip, double total) {
        String sql = "INSERT INTO Orders (employeeID, orderDate, tip, total, orderStatus) VALUES (?, NOW(), ?, ?, 'Ordering')";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, employeeID);
            stmt.setDouble(2, tip);
            stmt.setDouble(3, total);
            stmt.executeUpdate();

            // Retrieve auto-generated order ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Retrieves an order by its ID.
     */
    public Order getOrderById(int orderID) {
        String sql = "SELECT * FROM Orders WHERE orderID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Order(
                            rs.getInt("orderID"),
                            rs.getInt("employeeID"),
                            rs.getString("orderDate"),
                            rs.getDouble("tip"),
                            rs.getDouble("total"),
                            rs.getString("orderStatus"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public double getOrderTip(int orderID) {
        String sql = "SELECT tip FROM Orders WHERE orderID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("tip");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    /**
     * Adds a new item to an order.
     * 
     * @return the generated orderItemID, or -1 if failed
     */
    public int addOrderItem(int orderID, int flavorID, int quantity, double itemCost) {
        String sql = """
                INSERT INTO OrderItem (orderID, flavorID, quantity, itemCost, refundStatus)
                VALUES (?, ?, ?, ?, 'Not Refunded')
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, orderID);
            stmt.setInt(2, flavorID);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, itemCost);
            stmt.executeUpdate();

            // Return generated order item ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Adds a topping to a specific order item.
     */
    public boolean addOrderItemTopping(int orderItemID, int toppingID, int toppingQuantity) {
        String sql = """
                INSERT INTO OrderItemTopping (orderItemID, toppingID, toppingQuantity)
                VALUES (?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderItemID);
            stmt.setInt(2, toppingID);
            stmt.setInt(3, toppingQuantity);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Marks an order item as refunded if the order is completed.
     */
    public boolean refundOrderItem(int orderItemID) {
        String sql = """
                UPDATE OrderItem oi
                JOIN Orders o ON oi.orderID = o.orderID
                SET oi.refundStatus = 'Refunded'
                WHERE oi.orderItemID = ? AND o.orderStatus = 'Completed'
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderItemID);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all order items for a given order.
     */
    public List<OrderItem> getOrderItemsForOrder(int orderID, FlavorDAO flavorDAO) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM OrderItem WHERE orderID = ? ORDER BY orderItemID";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderID);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    // Retrieve flavor object using FlavorDAO
                    IceCreamFlavor flavor = flavorDAO.getFlavorById(rs.getInt("flavorID"));

                    if (flavor != null) {
                        items.add(new OrderItem(
                                rs.getInt("orderItemID"),
                                rs.getInt("orderID"),
                                flavor,
                                rs.getInt("quantity"),
                                rs.getDouble("itemCost"),
                                rs.getString("refundStatus")));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    /**
     * Retrieves all order items for a given order, including their toppings.
     * @param orderID
     * @return List of OrderItem objects with toppings populated
     */
    public List<OrderItem> getOrderItemsWithToppingsForOrder(int orderID, FlavorDAO flavorDAO, ToppingDAO toppingDAO) {
        List<OrderItem> items = getOrderItemsForOrder(orderID, flavorDAO);

        // For each order item, retrieve its toppings
        for (OrderItem item : items) {
            List<OrderItemTopping> toppings = getToppingsForOrderItem(item.getOrderItemID(), toppingDAO);
            item.getToppings().addAll(toppings);
        }

        return items;
    }

    public List<OrderItemTopping> getToppingsForOrderItem(int orderItemID, ToppingDAO toppingDAO) {
        List<OrderItemTopping> toppings = new ArrayList<>();
        String sql = "SELECT * FROM OrderItemTopping WHERE orderItemID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderItemID);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Retrieve topping object using ToppingDAO
                    Topping topping = toppingDAO.getToppingById(rs.getInt("toppingID"));

                    if (topping != null) {
                        toppings.add(new OrderItemTopping(
                                rs.getInt("orderItemID"),
                                topping,
                                rs.getInt("toppingQuantity")));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return toppings;
    }

    /**
     * Calculates total cost of all non-refunded items in an order.
     */
    public double calculateOrderTotal(int orderID) {
        String sql = """
                SELECT COALESCE(SUM(itemCost), 0) AS total
                FROM OrderItem
                WHERE orderID = ? AND refundStatus = 'Not Refunded'
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    /**
     * Updates the total cost of an order.
     */
    public boolean updateOrderTotal(int orderID, double newTotal) {
        String sql = "UPDATE Orders SET total = ? WHERE orderID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, newTotal);
            stmt.setInt(2, orderID);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Util method for changing order status
     * @param orderID The ID of the order to update
     * @param newStatus The new status to set (e.g., "Open", "Completed", "Cancelled")
     * @param validCurrentStatuses A list of valid current statuses that allow the update (e.g., ["Ordering"])
     * @return true if the order status was successfully updated, false otherwise
     */
    public boolean updateOrderStatus(int orderID, String newStatus, List<String> validCurrentStatuses) {
        String sql = "UPDATE Orders SET orderStatus = ? WHERE orderID = ? AND orderStatus IN ("
                + String.join(", ", validCurrentStatuses.stream().map(s -> "?").toArray(String[]::new)) + ")";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, orderID);
            for (int i = 0; i < validCurrentStatuses.size(); i++) {
                stmt.setString(3 + i, validCurrentStatuses.get(i));
            }

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks out an order, changing its status to 'Open'.
     */
    public boolean checkoutOrder(int orderID) {
        return updateOrderStatus(orderID, "Open", List.of("Ordering"));
    }

    /**
     * Marks an order as completed.
     */
    public boolean concludeOrder(int orderID) {
        return updateOrderStatus(orderID, "Completed", List.of("Open"));
    }

    /**
     * Cancels an order, changing its status to 'Cancelled'.
     */
    public boolean cancelOrder(int orderID) {
        return updateOrderStatus(orderID, "Cancelled", List.of("Ordering"));
    }

    /**
     * Retrieves the order ID associated with a given order item.
     */
    public Integer getOrderIdByOrderItemId(int orderItemID) {
        String sql = "SELECT orderID FROM OrderItem WHERE orderItemID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderItemID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("orderID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Calculates total cost of toppings for an entire order.
     */
    public double calculateOrderToppingTotal(int orderID) {
        String sql = """
                SELECT COALESCE(SUM(oit.toppingQuantity * ?), 0) AS toppingTotal
                FROM OrderItemTopping oit
                JOIN OrderItem oi ON oit.orderItemID = oi.orderItemID
                WHERE oi.orderID = ? AND oi.refundStatus = 'Not Refunded'
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, Prices.TOPPING_PRICE);
            stmt.setInt(2, orderID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("toppingTotal");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    /**
     * Calculates total cost of toppings for a specific order item.
     */
    public double calculateToppingTotalForOrderItem(int orderItemID) {
        String sql = """
                SELECT COALESCE(SUM(toppingQuantity * ?), 0) AS toppingTotal
                FROM OrderItemTopping
                WHERE orderItemID = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, Prices.TOPPING_PRICE);
            stmt.setInt(2, orderItemID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("toppingTotal");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    /**
     * Retrieves all orders sorted by most recent first.
     */
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM Orders ORDER BY orderID DESC";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("orderID"),
                        rs.getInt("employeeID"),
                        rs.getString("orderDate"),
                        rs.getDouble("tip"),
                        rs.getDouble("total"),
                        rs.getString("orderStatus")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    public boolean updateOrderTip(int orderID, double tip) {
        String sql = "UPDATE Orders SET tip = ? WHERE orderID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, tip);
            stmt.setInt(2, orderID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeOrderItem(int orderItemID) {
        String sql = "DELETE FROM OrderItem WHERE orderItemID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderItemID);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}