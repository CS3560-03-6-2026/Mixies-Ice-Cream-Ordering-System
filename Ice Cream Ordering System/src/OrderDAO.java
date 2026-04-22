import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public int createOrder(int employeeID, double tip, double total) {
        String sql = "INSERT INTO Orders (employeeID, orderDate, tip, total, orderStatus) VALUES (?, NOW(), ?, ?, 'Open')";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, employeeID);
            stmt.setDouble(2, tip);
            stmt.setDouble(3, total);
            stmt.executeUpdate();

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
                            rs.getString("orderStatus")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

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

    public List<OrderItem> getOrderItemsForOrder(int orderID, FlavorDAO flavorDAO) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM OrderItem WHERE orderID = ? ORDER BY orderItemID";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderID);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    IceCreamFlavor flavor = flavorDAO.getFlavorById(rs.getInt("flavorID"));
                    if (flavor != null) {
                        items.add(new OrderItem(
                                rs.getInt("orderItemID"),
                                rs.getInt("orderID"),
                                flavor,
                                rs.getInt("quantity"),
                                rs.getDouble("itemCost"),
                                rs.getString("refundStatus")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public double calculateOrderTotal(int orderID) {
        String sql = """
                SELECT COALESCE(SUM(quantity * itemCost), 0) AS total
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

    public boolean concludeOrder(int orderID) {
        String sql = "UPDATE Orders SET orderStatus = 'Completed' WHERE orderID = ? AND orderStatus = 'Open'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderID);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

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

    public double calculateOrderToppingTotal(int orderID) {
        String sql = """
                SELECT COALESCE(SUM(oit.toppingQuantity * ?), 0) AS toppingTotal
                FROM OrderItemTopping oit
                JOIN OrderItem oi ON oit.orderItemID = oi.orderItemID
                WHERE oi.orderID = ? AND oi.refundStatus = 'Not Refunded'
                """;

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, MixiesService.TOPPING_PRICE);
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
}