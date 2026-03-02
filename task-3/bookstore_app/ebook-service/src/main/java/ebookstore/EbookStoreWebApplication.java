package ebookstore;

import ebookstore.configuration.WebConfig;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Инициализируем приложение для запуска через внутренний томкат
 */
public class EbookStoreWebApplication {

    private static final int port = 8080;

    public static void main(String[] args) throws LifecycleException {

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector();
        Context tomcatContext = tomcat.addContext("", null);

        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(WebConfig.class);

        context.setServletContext(tomcatContext.getServletContext());

        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);

        Tomcat.addServlet(tomcatContext, "dispatcher", dispatcherServlet);
        tomcatContext.addServletMappingDecoded("/", "dispatcher");

        tomcat.start();

        context.refresh();
        System.out.println("EbookStore started on http://localhost:" + port);
        tomcat.getServer().await();
    }
}
