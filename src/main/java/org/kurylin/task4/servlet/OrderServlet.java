package org.kurylin.task4.servlet;

import org.kurylin.task4.model.Order;
import org.kurylin.task4.model.User;
import org.kurylin.task4.service.OrderService;
import org.kurylin.task4.service.impl.OrderServiceImpl;
import org.kurylin.task4.util.ThymeleafUtil;
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
import java.util.List;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(OrderServlet.class);
    private OrderService orderService;

    @Override
    public void init() {
        orderService = new OrderServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");

        try {
            List<Order> orders;
            if (user.isAdmin()) {
                orders = orderService.getAllOrders();
            } else {
                orders = orderService.getOrdersByUser(user.getId());
            }

            WebContext ctx = new WebContext(
                    JavaxServletWebApplication.buildApplication(getServletContext())
                            .buildExchange(req, resp)
            );
            ctx.setVariable("orders", orders);
            ctx.setVariable("user", user);
            ctx.setVariable("error", req.getParameter("error"));
            ctx.setVariable("success", req.getParameter("success"));

            resp.setContentType("text/html;charset=UTF-8");
            ThymeleafUtil.getEngine().process("orders", ctx, resp.getWriter());
        } catch (Exception e) {
            logger.error("Error loading orders page", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");
        String action = req.getParameter("action");

        if ("cancel".equals(action)) {
            try {
                int orderId = Integer.parseInt(req.getParameter("orderId"));
                orderService.cancelOrder(orderId, user.getId());
                logger.info("Order {} cancelled by user {}", orderId, user.getUsername());
                resp.sendRedirect(req.getContextPath() + "/orders?success=Order+cancelled+successfully");
            } catch (Exception e) {
                logger.error("Failed to cancel order", e);
                resp.sendRedirect(req.getContextPath() + "/orders?error=" + e.getMessage().replace(" ", "+"));
            }
        }
    }
}
