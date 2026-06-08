package org.kurylin.task4.dao;

import org.kurylin.task4.exception.DaoException;
import org.kurylin.task4.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> findById(int id) throws DaoException;
    Optional<User> findByUsername(String username) throws DaoException;
    Optional<User> findByEmail(String email) throws DaoException;
    List<User> findAll() throws DaoException;
    void save(User user) throws DaoException;
    void update(User user) throws DaoException;
    void delete(int id) throws DaoException;
}
