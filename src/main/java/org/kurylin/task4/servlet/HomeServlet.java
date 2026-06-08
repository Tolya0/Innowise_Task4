package org.kurylin.task4.servlet;

import org.kurylin.task4.model.Product;
import org.kurylin.task4.service.ProductService;
import org.kurylin.task4.service.impl.ProductServiceImpl;
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
import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(HomeServlet.class);
    private ProductService productService;

    @Override
    public void init() {
        productService = new ProductServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<Product> products = productService.getAllProducts();

            WebContext ctx = new WebContext(
                    JavaxServletWebApplication.buildApplication(getServletContext())
                            .buildExchange(req, resp)
            );
            ctx.setVariable("products", products);
            ctx.setVariable("user", req.getSession(false) != null ? req.getSession(false).getAttribute("user") : null);

            resp.setContentType("text/html;charset=UTF-8");
            ThymeleafUtil.getEngine().process("home", ctx, resp.getWriter());
        } catch (Exception e) {
            logger.error("Error loading home page", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading page");
        }
    }
}
