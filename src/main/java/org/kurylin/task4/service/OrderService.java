package org.kurylin.task4.service;

import org.kurylin.task4.exception.ServiceException;
import org.kurylin.task4.model.Order;

import java.util.List;


public interface OrderService {

    List<Order> getAllOrders() throws ServiceException;
    List<Order> getOrdersByUser(int userId) throws ServiceException;
    void placeOrder(int userId, int productId, int quantity) throws ServiceException;
    void cancelOrder(int orderId, int userId) throws ServiceException;
}
