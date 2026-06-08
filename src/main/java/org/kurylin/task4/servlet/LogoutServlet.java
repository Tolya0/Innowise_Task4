package org.kurylin.task4.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LogoutServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            String username = session.getAttribute("user") != null
                    ? ((org.kurylin.task4.model.User) session.getAttribute("user")).getUsername()
                    : "unknown";
            session.invalidate();
            logger.info("User '{}' logged out", username);
        }
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
