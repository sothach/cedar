package org.seefin.cedar.webui;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/**
 * Helper to access the spring context from the servlet's context
 *
 * @author phillipsr
 */
public class SpringContextHelper {

    private ApplicationContext context;

    public SpringContextHelper(ServletContext servletContext) {
        /*ServletContext servletContext = 
                ((WebApplicationContext) application.getContext())
                .getHttpSession().getServletContext();*/
        context = WebApplicationContextUtils.
                getRequiredWebApplicationContext(servletContext);
    }

    public Object getBean(final String beanRef) {
        return context.getBean(beanRef);
    }
}