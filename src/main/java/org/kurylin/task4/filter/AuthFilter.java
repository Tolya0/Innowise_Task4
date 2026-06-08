package org.kurylin.task4.filter;

import org.kurylin.task4.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter({"/orders", "/profile", "/admin/*", "/product/add", "/product/edit", "/product/delete"})
public class AuthFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("AuthFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        boolean loggedIn = session != null && session.getAttribute("user") != null;

        if (!loggedIn) {
            logger.warn("Unauthenticated access to: {}", req.getRequestURI());
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }


        String uri = req.getRequestURI();
        if (uri.contains("/admin")) {
            User user = (User) session.getAttribute("user");
            if (!user.isAdmin()) {
                logger.warn("Non-admin user '{}' attempted to access admin route: {}", user.getUsername(), uri);
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
