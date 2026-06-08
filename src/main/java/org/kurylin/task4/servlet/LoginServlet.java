package org.kurylin.task4.servlet;

import org.kurylin.task4.model.User;
import org.kurylin.task4.service.UserService;
import org.kurylin.task4.service.impl.UserServiceImpl;
import org.kurylin.task4.util.ThymeleafUtil;
import org.kurylin.task4.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;


@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private UserService userService;

    @Override
    public void init() {
        userService = new UserServiceImpl();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        WebContext ctx = new WebContext(
                JavaxServletWebApplication.buildApplication(getServletContext())
                        .buildExchange(req, resp)
        );
        ctx.setVariable("error", req.getParameter("error"));

        resp.setContentType("text/html;charset=UTF-8");
        ThymeleafUtil.getEngine().process("login", ctx, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = ValidationUtil.sanitize(req.getParameter("username"));
        String password = req.getParameter("password");

        try {
            Optional<User> optUser = userService.login(username, password);
            if (optUser.isPresent()) {
                HttpSession session = req.getSession(true);
                session.setAttribute("user", optUser.get());
                logger.info("User '{}' logged in successfully", username);
                resp.sendRedirect(req.getContextPath() + "/home");
            } else {
                logger.warn("Failed login attempt for username: {}", username);
                resp.sendRedirect(req.getContextPath() + "/login?error=Invalid+username+or+password");
            }
        } catch (Exception e) {
            logger.error("Login error for user: {}", username, e);
            resp.sendRedirect(req.getContextPath() + "/login?error=Server+error+occurred");
        }
    }
}
