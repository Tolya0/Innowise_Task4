package org.kurylin.task4.service.impl;

import org.kurylin.task4.dao.OrderDao;
import org.kurylin.task4.dao.ProductDao;
import org.kurylin.task4.dao.impl.OrderDaoImpl;
import org.kurylin.task4.dao.impl.ProductDaoImpl;
import org.kurylin.task4.exception.DaoException;
import org.kurylin.task4.exception.ServiceException;
import org.kurylin.task4.model.Order;
import org.kurylin.task4.model.Product;
import org.kurylin.task4.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderDao orderDao;
    private final ProductDao productDao;

    public OrderServiceImpl() {
        this.orderDao = new OrderDaoImpl();
        this.productDao = new ProductDaoImpl();
    }

    public OrderServiceImpl(OrderDao orderDao, ProductDao productDao) {
        this.orderDao = orderDao;
        this.productDao = productDao;
    }

    @Override
    public List<Order> getAllOrders() throws ServiceException {
        try {
            return orderDao.findAll();
        } catch (DaoException e) {
            logger.error("Failed to retrieve all orders", e);
            throw new ServiceException("Failed to retrieve orders", e);
        }
    }

    @Override
    public List<Order> getOrdersByUser(int userId) throws ServiceException {
        try {
            return orderDao.findByUserId(userId);
        } catch (DaoException e) {
            logger.error("Failed to retrieve orders for userId={}", userId, e);
            throw new ServiceException("Failed to retrieve user orders", e);
        }
    }

    @Override
    public void placeOrder(int userId, int productId, int quantity) throws ServiceException {
        if (quantity <= 0) {
            throw new ServiceException("Quantity must be greater than 0");
        }
        try {
            Optional<Product> optProduct = productDao.findById(productId);
            if (optProduct.isEmpty()) {
                throw new ServiceException("Product not found with id=" + productId);
            }
            Product product = optProduct.get();
            if (product.getStock() < quantity) {
                throw new ServiceException("Not enough stock. Available: " + product.getStock());
            }

            BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));

            Order order = new Order();
            order.setUserId(userId);
            order.setProductId(productId);
            order.setQuantity(quantity);
            order.setTotalPrice(totalPrice);
            order.setStatus(Order.Status.PENDING);

            orderDao.save(order);

            product.setStock(product.getStock() - quantity);
            productDao.update(product);

            logger.info("Order placed: userId={}, productId={}, qty={}", userId, productId, quantity);
        } catch (DaoException e) {
            logger.error("Failed to place order for userId={}, productId={}", userId, productId, e);
            throw new ServiceException("Failed to place order", e);
        }
    }

    @Override
    public void cancelOrder(int orderId, int userId) throws ServiceException {
        try {
            Optional<Order> optOrder = orderDao.findById(orderId);
            if (optOrder.isEmpty()) {
                throw new ServiceException("Order not found with id=" + orderId);
            }
            Order order = optOrder.get();
            if (order.getUserId() != userId) {
                throw new ServiceException("You do not have permission to cancel this order");
            }
            if (order.getStatus() == Order.Status.CANCELLED) {
                throw new ServiceException("Order is already cancelled");
            }

            order.setStatus(Order.Status.CANCELLED);
            orderDao.update(order);

            Optional<Product> optProduct = productDao.findById(order.getProductId());
            optProduct.ifPresent(product -> {
                product.setStock(product.getStock() + order.getQuantity());
                try {
                    productDao.update(product);
                } catch (DaoException e) {
                    logger.error("Failed to restore stock for productId={}", order.getProductId(), e);
                }
            });

            logger.info("Order cancelled: orderId={}, userId={}", orderId, userId);
        } catch (DaoException e) {
            logger.error("Failed to cancel order id={}", orderId, e);
            throw new ServiceException("Failed to cancel order", e);
        }
    }
}
