package org.kurylin.task4.service;

import org.kurylin.task4.exception.ServiceException;
import org.kurylin.task4.model.User;

import java.util.List;
import java.util.Optional;


public interface UserService {

    void register(String username, String email, String password) throws ServiceException;
    Optional<User> login(String username, String password) throws ServiceException;
    List<User> getAllUsers() throws ServiceException;
    Optional<User> findById(int id) throws ServiceException;
    void update(User user) throws ServiceException;
}
