package org.kurylin.task4.dao.impl;

import org.kurylin.task4.dao.OrderDao;
import org.kurylin.task4.exception.DaoException;
import org.kurylin.task4.model.Order;
import org.kurylin.task4.util.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDaoImpl implements OrderDao {

    private static final Logger logger = LoggerFactory.getLogger(OrderDaoImpl.class);

    private static final String FIND_BY_ID =
            "SELECT o.id, o.user_id, o.product_id, o.quantity, o.total_price, o.status, o.created_at, " +
                    "u.username, p.name AS product_name " +
                    "FROM orders o JOIN users u ON o.user_id = u.id JOIN products p ON o.product_id = p.id " +
                    "WHERE o.id = ?";
    private static final String FIND_ALL =
            "SELECT o.id, o.user_id, o.product_id, o.quantity, o.total_price, o.status, o.created_at, " +
                    "u.username, p.name AS product_name " +
                    "FROM orders o JOIN users u ON o.user_id = u.id JOIN products p ON o.product_id = p.id " +
                    "ORDER BY o.created_at DESC";
    private static final String FIND_BY_USER_ID =
            "SELECT o.id, o.user_id, o.product_id, o.quantity, o.total_price, o.status, o.created_at, " +
                    "u.username, p.name AS product_name " +
                    "FROM orders o JOIN users u ON o.user_id = u.id JOIN products p ON o.product_id = p.id " +
                    "WHERE o.user_id = ? ORDER BY o.created_at DESC";
    private static final String INSERT =
            "INSERT INTO orders (user_id, product_id, quantity, total_price, status) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE =
            "UPDATE orders SET status = ? WHERE id = ?";
    private static final String DELETE =
            "DELETE FROM orders WHERE id = ?";

    @Override
    public Optional<Order> findById(int id) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = null;
        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(FIND_BY_ID);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException | InterruptedException e) {
            logger.error("Error finding order by id={}", id, e);
            throw new DaoException("Failed to find order by id", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }

    @Override
    public List<Order> findAll() throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = null;
        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(FIND_ALL);
            ResultSet rs = ps.executeQuery();
            List<Order> orders = new ArrayList<>();
            while (rs.next()) {
                orders.add(mapRow(rs));
            }
            return orders;
        } catch (SQLException | InterruptedException e) {
            logger.error("Error fetching all orders", e);
            throw new DaoException("Failed to fetch all orders", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }

    @Override
    public List<Order> findByUserId(int userId) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = null;
        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(FIND_BY_USER_ID);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            List<Order> orders = new ArrayList<>();
            while (rs.next()) {
                orders.add(mapRow(rs));
            }
            return orders;
        } catch (SQLException | InterruptedException e) {
            logger.error("Error fetching orders for userId={}", userId, e);
            throw new DaoException("Failed to fetch orders for user", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }

    @Override
    public void save(Order order) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = null;
        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(INSERT);
            ps.setInt(1, order.getUserId());
            ps.setInt(2, order.getProductId());
            ps.setInt(3, order.getQuantity());
            ps.setBigDecimal(4, order.getTotalPrice());
            ps.setString(5, order.getStatus().name().toLowerCase());
            ps.executeUpdate();
            logger.info("Saved order for userId={}, productId={}", order.getUserId(), order.getProductId());
        } catch (SQLException | InterruptedException e) {
            logger.error("Error saving order", e);
            throw new DaoException("Failed to save order", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }

    @Override
    public void update(Order order) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = null;
        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(UPDATE);
            ps.setString(1, order.getStatus().name().toLowerCase());
            ps.setInt(2, order.getId());
            ps.executeUpdate();
            logger.info("Updated order id={} status={}", order.getId(), order.getStatus());
        } catch (SQLException | InterruptedException e) {
            logger.error("Error updating order id={}", order.getId(), e);
            throw new DaoException("Failed to update order", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }

    @Override
    public void delete(int id) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = null;
        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(DELETE);
            ps.setInt(1, id);
            ps.executeUpdate();
            logger.info("Deleted order id={}", id);
        } catch (SQLException | InterruptedException e) {
            logger.error("Error deleting order id={}", id, e);
            throw new DaoException("Failed to delete order", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setUserId(rs.getInt("user_id"));
        order.setProductId(rs.getInt("product_id"));
        order.setQuantity(rs.getInt("quantity"));
        order.setTotalPrice(rs.getBigDecimal("total_price"));
        order.setStatus(Order.Status.valueOf(rs.getString("status").toUpperCase()));
        order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        order.setUsername(rs.getString("username"));
        order.setProductName(rs.getString("product_name"));
        return order;
    }
}
