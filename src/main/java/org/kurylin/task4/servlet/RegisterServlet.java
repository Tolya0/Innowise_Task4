package org.kurylin.task4.servlet;

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
import java.io.IOException;


@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(RegisterServlet.class);
    private UserService userService;

    @Override
    public void init() {
        userService = new UserServiceImpl();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        WebContext ctx = new WebContext(
                JavaxServletWebApplication.buildApplication(getServletContext())
                        .buildExchange(req, resp)
        );
        ctx.setVariable("error", req.getParameter("error"));
        ctx.setVariable("success", req.getParameter("success"));

        resp.setContentType("text/html;charset=UTF-8");
        ThymeleafUtil.getEngine().process("register", ctx, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = ValidationUtil.sanitize(req.getParameter("username"));
        String email = ValidationUtil.sanitize(req.getParameter("email"));
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");


        if (!password.equals(confirmPassword)) {
            resp.sendRedirect(req.getContextPath() + "/register?error=Passwords+do+not+match");
            return;
        }

        try {
            userService.register(username, email, password);
            logger.info("New user registered: {}", username);

            resp.sendRedirect(req.getContextPath() + "/login?success=Registration+successful!+Please+log+in.");
        } catch (Exception e) {
            logger.warn("Registration failed: {}", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/register?error=" + e.getMessage().replace(" ", "+"));
        }
    }
}
