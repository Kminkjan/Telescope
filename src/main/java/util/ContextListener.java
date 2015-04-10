package util;

import model.Model;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Sets up and tears down the server. Also initializes the model.
 * Created by Kris on 16-3-2015.
 */
public class ContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Model model = new Model();
        servletContextEvent.getServletContext().setAttribute("model", model);
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ((Model) servletContextEvent.getServletContext().getAttribute("model")).destroy();
    }
}