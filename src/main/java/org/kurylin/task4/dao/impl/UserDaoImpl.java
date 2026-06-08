package org.kurylin.task4.dao.impl;

import org.kurylin.task4.dao.UserDao;
import org.kurylin.task4.exception.DaoException;
import org.kurylin.task4.model.User;
import org.kurylin.task4.util.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class UserDaoImpl implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    private static final String FIND_BY_ID =
            "SELECT id, username, email, password_hash, role FROM users WHERE id = ?";
    private static final String FIND_BY_USERNAME =
            "SELECT id, username, email, password_hash, role FROM users WHERE username = ?";
    private static final String FIND_BY_EMAIL =
            "SELECT id, username, email, password_hash, role FROM users WHERE email = ?";
    private static final String FIND_ALL =
            "SELECT id, username, email, password_hash, role FROM users ORDER BY id";
    private static final String INSERT =
            "INSERT INTO users (username, email, password_hash, role) VALUES (?, ?, ?, ?)";
    private static final String UPDATE =
            "UPDATE users SET username = ?, email = ?, password_hash = ?, role = ? WHERE id = ?";
    private static final String DELETE =
            "DELETE FROM users WHERE id = ?";

    @Override
    public Optional<User> findById(int id) throws DaoException {
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
            logger.error("Error finding user by id={}", id, e);
            throw new DaoException("Failed to find user by id", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = null;
        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(FIND_BY_USERNAME);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException | InterruptedException e) {
            logger.error("Error finding user by username={}", username, e);
            throw new DaoException("Failed to find user by username", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = null;
        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(FIND_BY_EMAIL);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException | InterruptedException e) {
            logger.error("Error finding user by email={}", email, e);
            throw new DaoException("Failed to find user by email", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }

    @Override
    public List<User> findAll() throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = null;
        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(FIND_ALL);
            ResultSet rs = ps.executeQuery();
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(mapRow(rs));
            }
            return users;
        } catch (SQLException | InterruptedException e) {
            logger.error("Error fetching all users", e);
            throw new DaoException("Failed to fetch all users", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }

    @Override
    public void save(User user) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = null;
        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(INSERT);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole().name().toLowerCase());
            ps.executeUpdate();
            logger.info("Saved user: {}", user.getUsername());
        } catch (SQLException | InterruptedException e) {
            logger.error("Error saving user: {}", user.getUsername(), e);
            throw new DaoException("Failed to save user", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }

    @Override
    public void update(User user) throws DaoException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = null;
        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(UPDATE);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole().name().toLowerCase());
            ps.setInt(5, user.getId());
            ps.executeUpdate();
            logger.info("Updated user id={}", user.getId());
        } catch (SQLException | InterruptedException e) {
            logger.error("Error updating user id={}", user.getId(), e);
            throw new DaoException("Failed to update user", e);
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
            logger.info("Deleted user id={}", id);
        } catch (SQLException | InterruptedException e) {
            logger.error("Error deleting user id={}", id, e);
            throw new DaoException("Failed to delete user", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }


    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(User.Role.valueOf(rs.getString("role").toUpperCase()));
        return user;
    }
}
