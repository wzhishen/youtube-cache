package test.edu.upenn.cis455.servletapi;

import javax.servlet.*;
import java.util.*;

/**
 * Class for servlet config
 * @author Zhishen Wen (original auth: Nick Taylor)
 * @version Feb 20, 2013
 */
public class FakeConfig implements ServletConfig {
    private String servletName;
    private ServletContext servletContext;
    private HashMap<String,String> initParams;
    
    /**
     * Constructor for FakeConfig
     */
    public FakeConfig() {
        initParams = new HashMap<String,String>();
    }
    
    /**
     * Returns a String containing the value of the named 
     * initialization parameter, or null if the parameter 
     * does not exist.
     * @param name A String specifying the name of the 
     * initialization parameter.
     * @return A String containing the value of the 
     * initialization parameter.
     */
    public String getInitParameter(String name) {
        return initParams.get(name);
    }
    
    /**
     * Returns the names of the servlet's initialization 
     * parameters as an Enumeration of String objects, or 
     * an empty Enumeration if the servlet has no initialization 
     * parameters.
     * @return An Enumeration of String objects containing the 
     * names of the servlet's initialization parameters.
     */
    public Enumeration getInitParameterNames() {
        Set<String> keys = initParams.keySet();
        Vector<String> atts = new Vector<String>(keys);
        return atts.elements();
    }
    
    /**
     * Returns a reference to the ServletContext in 
     * which the caller is executing.
     * @return A ServletContext object, used by the 
     * caller to interact with its servlet container.
     */
    public ServletContext getServletContext() {
        return servletContext;
    }
    
    /**
     * Returns the name of this servlet instance.
     * @return the name of the servlet instance.
     */
    public String getServletName() {
        return servletName;
    }
    
    //---------------------------------------------
    //   Helpers not visible outside the server.
    //   But give public visibility to enable
    //   JUnit tests.
    //---------------------------------------------
    public void setInitParam(String name, String value) {
        initParams.put(name, value);
    }
    
    public void setServletName(String name) {
        servletName = name;
    }
    
    public void setServletContext(FakeContext context) {
        servletContext = context;
    }
}
