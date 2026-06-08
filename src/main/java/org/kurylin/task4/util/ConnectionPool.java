package org.kurylin.task4.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ConnectionPool {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);
    private static final int DEFAULT_POOL_SIZE = 10;

    private static ConnectionPool instance;
    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    private BlockingQueue<Connection> pool;

    private ConnectionPool() {}

    public static ConnectionPool getInstance() {
        if (!initialized.get()) {
            synchronized (ConnectionPool.class) {
                if (!initialized.get()) {
                    instance = new ConnectionPool();
                    instance.initialize();
                    initialized.set(true);
                }
            }
        }
        return instance;
    }

    private void initialize() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new RuntimeException("db.properties not found in classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        String driver = props.getProperty("db.driver");
        int poolSize = Integer.parseInt(props.getProperty("db.pool.size", String.valueOf(DEFAULT_POOL_SIZE)));

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("JDBC driver not found: " + driver, e);
        }

        pool = new ArrayBlockingQueue<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            try {
                Connection connection = DriverManager.getConnection(url, user, password);
                pool.offer(connection);
            } catch (SQLException e) {
                logger.error("Failed to create connection #{}", i, e);
            }
        }
        logger.info("Connection pool initialized with {} connections", pool.size());
    }

    public Connection getConnection() throws InterruptedException {
        return pool.take();
    }

    public void releaseConnection(Connection connection) {
        if (connection != null) {
            pool.offer(connection);
        }
    }

    public void destroyPool() {
        for (Connection connection : pool) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Error closing connection during pool destroy", e);
            }
        }
        logger.info("Connection pool destroyed");
    }
}
