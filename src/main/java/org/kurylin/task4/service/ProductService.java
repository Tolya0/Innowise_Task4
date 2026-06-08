package org.kurylin.task4.service;

import org.kurylin.task4.exception.ServiceException;
import org.kurylin.task4.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {


    List<Product> getAllProducts() throws ServiceException;
    Optional<Product> findById(int id) throws ServiceException;
    void addProduct(Product product) throws ServiceException;
    void updateProduct(Product product) throws ServiceException;
    void deleteProduct(int id) throws ServiceException;
}
