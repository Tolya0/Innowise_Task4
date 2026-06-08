package org.kurylin.task4.dao;

import org.kurylin.task4.exception.DaoException;
import org.kurylin.task4.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductDao {

    Optional<Product> findById(int id) throws DaoException;
    List<Product> findAll() throws DaoException;
    void save(Product product) throws DaoException;
    void update(Product product) throws DaoException;
    void delete(int id) throws DaoException;
}
