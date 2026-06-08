package org.kurylin.task4.dao;

import org.kurylin.task4.exception.DaoException;
import org.kurylin.task4.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderDao {

    Optional<Order> findById(int id) throws DaoException;
    List<Order> findAll() throws DaoException;
    List<Order> findByUserId(int userId) throws DaoException;
    void save(Order order) throws DaoException;
    void update(Order order) throws DaoException;
    void delete(int id) throws DaoException;
}
