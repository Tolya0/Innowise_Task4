package org.kurylin.task4.servlet;

import org.kurylin.task4.model.Product;
import org.kurylin.task4.model.User;
import org.kurylin.task4.service.OrderService;
import org.kurylin.task4.service.ProductService;
import org.kurylin.task4.service.impl.OrderServiceImpl;
import org.kurylin.task4.service.impl.ProductServiceImpl;
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
import java.math.BigDecimal;
import java.util.Optional;


@WebServlet("/product")
public class ProductServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ProductServlet.class);
    private ProductService productService;
    private OrderService orderService;

    @Override
    public void init() {
        productService = new ProductServiceImpl();
        orderService = new OrderServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        HttpSession session = req.getSession(false);
        User user = session != null ? (User) session.getAttribute("user") : null;

        try {
            WebContext ctx = new WebContext(
                    JavaxServletWebApplication.buildApplication(getServletContext())
                            .buildExchange(req, resp)
            );
            ctx.setVariable("user", user);

            if ("edit".equals(action) && user != null && user.isAdmin()) {
                int productId = Integer.parseInt(req.getParameter("id"));
                Optional<Product> product = productService.findById(productId);
                product.ifPresent(p -> ctx.setVariable("product", p));
                resp.setContentType("text/html;charset=UTF-8");
                ThymeleafUtil.getEngine().process("product-form", ctx, resp.getWriter());
            } else if ("add".equals(action) && user != null && user.isAdmin()) {
                resp.setContentType("text/html;charset=UTF-8");
                ThymeleafUtil.getEngine().process("product-form", ctx, resp.getWriter());
            } else {
                int productId = Integer.parseInt(req.getParameter("id"));
                Optional<Product> product = productService.findById(productId);
                if (product.isEmpty()) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found");
                    return;
                }
                ctx.setVariable("product", product.get());
                ctx.setVariable("error", req.getParameter("error"));
                resp.setContentType("text/html;charset=UTF-8");
                ThymeleafUtil.getEngine().process("product-detail", ctx, resp.getWriter());
            }
        } catch (Exception e) {
            logger.error("Error in ProductServlet GET", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");

        try {
            switch (action) {
                case "order" -> {
                    int productId = Integer.parseInt(req.getParameter("productId"));
                    int quantity = Integer.parseInt(req.getParameter("quantity"));
                    orderService.placeOrder(user.getId(), productId, quantity);
                    resp.sendRedirect(req.getContextPath() + "/orders?success=Order+placed+successfully");
                }
                case "add", "update" -> {
                    if (user == null || !user.isAdmin()) {
                        resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                        return;
                    }
                    Product product = new Product();
                    if ("update".equals(action)) {
                        product.setId(Integer.parseInt(req.getParameter("id")));
                    }
                    product.setName(ValidationUtil.sanitize(req.getParameter("name")));
                    product.setDescription(ValidationUtil.sanitize(req.getParameter("description")));
                    product.setPrice(new BigDecimal(req.getParameter("price")));
                    product.setStock(Integer.parseInt(req.getParameter("stock")));

                    if ("add".equals(action)) {
                        productService.addProduct(product);
                    } else {
                        productService.updateProduct(product);
                    }
                    resp.sendRedirect(req.getContextPath() + "/home");
                }
                case "delete" -> {
                    if (user == null || !user.isAdmin()) {
                        resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                        return;
                    }
                    int productId = Integer.parseInt(req.getParameter("id"));
                    productService.deleteProduct(productId);
                    resp.sendRedirect(req.getContextPath() + "/home");
                }
                default -> resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
            }
        } catch (Exception e) {
            logger.error("Error in ProductServlet POST action={}", action, e);
            resp.sendRedirect(req.getContextPath() + "/home?error=" + e.getMessage().replace(" ", "+"));
        }
    }
}
