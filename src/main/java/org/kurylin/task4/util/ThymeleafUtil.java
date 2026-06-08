package org.kurylin.task4.util;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;

import javax.servlet.ServletContext;
import java.util.concurrent.atomic.AtomicReference;


public final class ThymeleafUtil {

    private static final AtomicReference<TemplateEngine> engineRef = new AtomicReference<>();

    private ThymeleafUtil() {}


    public static void initialize(ServletContext servletContext) {
        JavaxServletWebApplication application = JavaxServletWebApplication.buildApplication(servletContext);
        WebApplicationTemplateResolver resolver = new WebApplicationTemplateResolver(application);

        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setPrefix("/WEB-INF/templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);

        engineRef.set(engine);
    }


    public static TemplateEngine getEngine() {
        TemplateEngine engine = engineRef.get();
        if (engine == null) {
            throw new IllegalStateException("ThymeleafUtil is not initialized. Call initialize() first.");
        }
        return engine;
    }
}
