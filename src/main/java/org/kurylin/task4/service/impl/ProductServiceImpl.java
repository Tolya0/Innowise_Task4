package org.kurylin.task4.service.impl;

import org.kurylin.task4.dao.ProductDao;
import org.kurylin.task4.dao.impl.ProductDaoImpl;
import org.kurylin.task4.exception.DaoException;
import org.kurylin.task4.exception.ServiceException;
import org.kurylin.task4.model.Product;
import org.kurylin.task4.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductDao productDao;

    public ProductServiceImpl() {
        this.productDao = new ProductDaoImpl();
    }

    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public List<Product> getAllProducts() throws ServiceException {
        try {
            return productDao.findAll();
        } catch (DaoException e) {
            logger.error("Failed to retrieve all products", e);
            throw new ServiceException("Failed to retrieve products", e);
        }
    }

    @Override
    public Optional<Product> findById(int id) throws ServiceException {
        try {
            return productDao.findById(id);
        } catch (DaoException e) {
            logger.error("Failed to find product id={}", id, e);
            throw new ServiceException("Failed to find product", e);
        }
    }

    @Override
    public void addProduct(Product product) throws ServiceException {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new ServiceException("Product name cannot be blank");
        }
        if (product.getPrice() == null || product.getPrice().signum() < 0) {
            throw new ServiceException("Product price must be non-negative");
        }
        try {
            productDao.save(product);
        } catch (DaoException e) {
            logger.error("Failed to add product: {}", product.getName(), e);
            throw new ServiceException("Failed to add product", e);
        }
    }

    @Override
    public void updateProduct(Product product) throws ServiceException {
        try {
            productDao.update(product);
        } catch (DaoException e) {
            logger.error("Failed to update product id={}", product.getId(), e);
            throw new ServiceException("Failed to update product", e);
        }
    }

    @Override
    public void deleteProduct(int id) throws ServiceException {
        try {
            productDao.delete(id);
        } catch (DaoException e) {
            logger.error("Failed to delete product id={}", id, e);
            throw new ServiceException("Failed to delete product", e);
        }
    }
}
