package org.kurylin.task4.service.impl;

import org.kurylin.task4.dao.UserDao;
import org.kurylin.task4.dao.impl.UserDaoImpl;
import org.kurylin.task4.exception.DaoException;
import org.kurylin.task4.exception.ServiceException;
import org.kurylin.task4.model.User;
import org.kurylin.task4.service.UserService;
import org.kurylin.task4.util.PasswordUtil;
import org.kurylin.task4.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;

    public UserServiceImpl() {
        this.userDao = new UserDaoImpl();
    }

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void register(String username, String email, String password) throws ServiceException {
        if (!ValidationUtil.isNotBlank(username)) {
            throw new ServiceException("Username cannot be blank");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new ServiceException("Invalid email address");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            throw new ServiceException("Password must be at least 6 characters");
        }

        try {
            if (userDao.findByUsername(username).isPresent()) {
                throw new ServiceException("Username '" + username + "' is already taken");
            }
            if (userDao.findByEmail(email).isPresent()) {
                throw new ServiceException("Email '" + email + "' is already registered");
            }

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPasswordHash(PasswordUtil.hash(password));
            user.setRole(User.Role.CLIENT);

            userDao.save(user);
            logger.info("New user registered: {}", username);
        } catch (DaoException e) {
            logger.error("Registration failed for user: {}", username, e);
            throw new ServiceException("Registration failed due to a database error", e);
        }
    }

    @Override
    public Optional<User> login(String username, String password) throws ServiceException {
        try {
            Optional<User> optUser = userDao.findByUsername(username);
            if (optUser.isEmpty()) {
                return Optional.empty();
            }
            User user = optUser.get();
            if (PasswordUtil.matches(password, user.getPasswordHash())) {
                logger.info("User logged in: {}", username);
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (DaoException e) {
            logger.error("Login failed for user: {}", username, e);
            throw new ServiceException("Login failed due to a database error", e);
        }
    }

    @Override
    public List<User> getAllUsers() throws ServiceException {
        try {
            return userDao.findAll();
        } catch (DaoException e) {
            logger.error("Failed to retrieve all users", e);
            throw new ServiceException("Failed to retrieve users", e);
        }
    }

    @Override
    public Optional<User> findById(int id) throws ServiceException {
        try {
            return userDao.findById(id);
        } catch (DaoException e) {
            logger.error("Failed to find user id={}", id, e);
            throw new ServiceException("Failed to find user", e);
        }
    }

    @Override
    public void update(User user) throws ServiceException {
        try {
            userDao.update(user);
        } catch (DaoException e) {
            logger.error("Failed to update user id={}", user.getId(), e);
            throw new ServiceException("Failed to update user", e);
        }
    }
}
