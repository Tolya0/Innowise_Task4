package org.kurylin.task4.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kurylin.task4.dao.UserDao;
import org.kurylin.task4.exception.DaoException;
import org.kurylin.task4.exception.ServiceException;
import org.kurylin.task4.model.User;
import org.kurylin.task4.util.PasswordUtil;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userDao);
    }

    @Test
    @DisplayName("Should successfully register a new user")
    void register_ShouldSaveUser_WhenValidInput() throws DaoException, ServiceException {
        when(userDao.findByUsername("john")).thenReturn(Optional.empty());
        when(userDao.findByEmail("john@example.com")).thenReturn(Optional.empty());

        userService.register("john", "john@example.com", "password123");

        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw ServiceException when username is already taken")
    void register_ShouldThrow_WhenUsernameExists() throws DaoException {
        User existing = new User();
        existing.setUsername("john");
        when(userDao.findByUsername("john")).thenReturn(Optional.of(existing));

        assertThrows(ServiceException.class,
                () -> userService.register("john", "other@email.com", "password123"));

        verify(userDao, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ServiceException when email is invalid")
    void register_ShouldThrow_WhenEmailIsInvalid() {
        assertThrows(ServiceException.class,
                () -> userService.register("john", "not-an-email", "password123"));
    }

    @Test
    @DisplayName("Should throw ServiceException when password is too short")
    void register_ShouldThrow_WhenPasswordTooShort() {
        assertThrows(ServiceException.class,
                () -> userService.register("john", "john@example.com", "123"));
    }

    @Test
    @DisplayName("Should return user on successful login")
    void login_ShouldReturnUser_WhenCredentialsAreCorrect() throws DaoException, ServiceException {
        User user = new User();
        user.setUsername("john");
        user.setPasswordHash(PasswordUtil.hash("password123"));
        when(userDao.findByUsername("john")).thenReturn(Optional.of(user));

        Optional<User> result = userService.login("john", "password123");

        assertTrue(result.isPresent());
        assertEquals("john", result.get().getUsername());
    }

    @Test
    @DisplayName("Should return empty Optional when password is wrong")
    void login_ShouldReturnEmpty_WhenPasswordIsWrong() throws DaoException, ServiceException {
        User user = new User();
        user.setUsername("john");
        user.setPasswordHash(PasswordUtil.hash("correctPassword"));
        when(userDao.findByUsername("john")).thenReturn(Optional.of(user));

        Optional<User> result = userService.login("john", "wrongPassword");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty Optional when user does not exist")
    void login_ShouldReturnEmpty_WhenUserNotFound() throws DaoException, ServiceException {
        when(userDao.findByUsername("nobody")).thenReturn(Optional.empty());

        Optional<User> result = userService.login("nobody", "password123");

        assertTrue(result.isEmpty());
    }
}
