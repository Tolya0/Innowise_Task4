package org.kurylin.task4.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kurylin.task4.dao.OrderDao;
import org.kurylin.task4.dao.ProductDao;
import org.kurylin.task4.exception.DaoException;
import org.kurylin.task4.exception.ServiceException;
import org.kurylin.task4.model.Order;
import org.kurylin.task4.model.Product;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private ProductDao productDao;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderDao, productDao);
    }

    @Test
    @DisplayName("Should place an order when stock is sufficient")
    void placeOrder_ShouldSaveOrder_WhenStockSufficient() throws DaoException, ServiceException {
        Product product = new Product(1, "Laptop", "desc", new BigDecimal("999.99"), 10);
        when(productDao.findById(1)).thenReturn(Optional.of(product));

        orderService.placeOrder(2, 1, 3);

        verify(orderDao, times(1)).save(any(Order.class));
        verify(productDao, times(1)).update(any(Product.class));
        assertEquals(7, product.getStock());
    }

    @Test
    @DisplayName("Should throw ServiceException when stock is insufficient")
    void placeOrder_ShouldThrow_WhenNotEnoughStock() throws DaoException {
        Product product = new Product(1, "Laptop", "desc", new BigDecimal("999.99"), 2);
        when(productDao.findById(1)).thenReturn(Optional.of(product));

        assertThrows(ServiceException.class,
                () -> orderService.placeOrder(2, 1, 5));

        verify(orderDao, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ServiceException when quantity is zero or negative")
    void placeOrder_ShouldThrow_WhenQuantityIsInvalid() {
        assertThrows(ServiceException.class,
                () -> orderService.placeOrder(1, 1, 0));
        assertThrows(ServiceException.class,
                () -> orderService.placeOrder(1, 1, -3));
    }

    @Test
    @DisplayName("Should throw ServiceException when product not found")
    void placeOrder_ShouldThrow_WhenProductNotFound() throws DaoException {
        when(productDao.findById(99)).thenReturn(Optional.empty());

        assertThrows(ServiceException.class,
                () -> orderService.placeOrder(1, 99, 1));
    }

    @Test
    @DisplayName("Should cancel an order belonging to the user")
    void cancelOrder_ShouldUpdateStatus_WhenUserOwnsOrder() throws DaoException, ServiceException {
        Order order = new Order();
        order.setId(1);
        order.setUserId(5);
        order.setProductId(1);
        order.setQuantity(2);
        order.setStatus(Order.Status.PENDING);
        when(orderDao.findById(1)).thenReturn(Optional.of(order));
        when(productDao.findById(1)).thenReturn(Optional.empty());

        orderService.cancelOrder(1, 5);

        assertEquals(Order.Status.CANCELLED, order.getStatus());
        verify(orderDao, times(1)).update(order);
    }

    @Test
    @DisplayName("Should throw ServiceException when user does not own the order")
    void cancelOrder_ShouldThrow_WhenUserDoesNotOwnOrder() throws DaoException {
        Order order = new Order();
        order.setId(1);
        order.setUserId(5);
        order.setStatus(Order.Status.PENDING);
        when(orderDao.findById(1)).thenReturn(Optional.of(order));

        assertThrows(ServiceException.class,
                () -> orderService.cancelOrder(1, 999));
    }
}
