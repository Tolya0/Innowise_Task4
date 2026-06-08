package org.kurylin.task4.util;

import org.kurylin.task4.exception.DaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;


public final class ValidationUtil {

    private static final Logger logger = LoggerFactory.getLogger(ValidationUtil.class);

    private ValidationUtil() {}

    public static String sanitize(String input) {
        if (input == null) return null;
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }


    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }


    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}
