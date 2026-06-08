package org.kurylin.task4.dao.impl;

import org.kurylin.task4.dao.ProductDao;
import org.kurylin.task4.exception.DaoException;
import org.kurylin.task4.model.Product;
import org.kurylin.task4.util.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDaoImpl implements ProductDao {

    private static final Logger logger = LoggerFactory.getLogger(ProductDaoImpl.class);

    private static final String FIND_BY_ID =
            "SELECT id, name, description, price, stock FROM products WHERE id = ?";
    private static final String FIND_ALL =
            "SELECT id, name, description, price, stock FROM products ORDER BY id";
    private static final String INSERT =
            "INSERT INTO products (name, description, price, stock) VALUES (?, ?, ?, ?)";
    private static final String UPDATE =
            "UPDATE products SET name = ?, description = ?, price = ?, stock = ? WHERE id = ?";
    private static final String DELETE =
            "DELETE FROM products WHERE id = ?";

    @Override
    public Optional<Product> findById(int id) throws DaoException {
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
            logger.error("Error finding product by id={}", id, e);
            throw new DaoException("Failed to find product by id", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }

    @Override
    public List<Product> findAll() throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = null;
        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(FIND_ALL);
            ResultSet rs = ps.executeQuery();
            List<Product> products = new ArrayList<>();
            while (rs.next()) {
                products.add(mapRow(rs));
            }
            return products;
        } catch (SQLException | InterruptedException e) {
            logger.error("Error fetching all products", e);
            throw new DaoException("Failed to fetch all products", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }

    @Override
    public void save(Product product) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = null;
        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(INSERT);
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setInt(4, product.getStock());
            ps.executeUpdate();
            logger.info("Saved product: {}", product.getName());
        } catch (SQLException | InterruptedException e) {
            logger.error("Error saving product: {}", product.getName(), e);
            throw new DaoException("Failed to save product", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }

    @Override
    public void update(Product product) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = null;
        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(UPDATE);
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setInt(4, product.getStock());
            ps.setInt(5, product.getId());
            ps.executeUpdate();
            logger.info("Updated product id={}", product.getId());
        } catch (SQLException | InterruptedException e) {
            logger.error("Error updating product id={}", product.getId(), e);
            throw new DaoException("Failed to update product", e);
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
            logger.info("Deleted product id={}", id);
        } catch (SQLException | InterruptedException e) {
            logger.error("Error deleting product id={}", id, e);
            throw new DaoException("Failed to delete product", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setStock(rs.getInt("stock"));
        return product;
    }
}
