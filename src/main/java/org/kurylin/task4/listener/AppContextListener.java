package org.kurylin.task4.listener;

import org.kurylin.task4.util.ConnectionPool;
import org.kurylin.task4.util.ThymeleafUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        logger.info("Application starting up...");

        ThymeleafUtil.initialize(ctx);
        logger.info("Thymeleaf initialized");

        ConnectionPool.getInstance();
        logger.info("Connection pool initialized");

        logger.info("Application startup complete");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Application shutting down...");
        ConnectionPool.getInstance().destroyPool();
        logger.info("Application shutdown complete");
    }
}
