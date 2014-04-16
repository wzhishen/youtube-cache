package test.edu.upenn.cis455.servletapi;

import javax.servlet.*;

import java.nio.file.Paths;
import java.util.*;

/**
 * Class for servlet context
 * @author Zhishen Wen (original auth: Nick Taylor)
 * @version Feb 20, 2013
 */
public class FakeContext implements ServletContext {
    private HashMap<String,Object> attributes;
    private HashMap<String,String> initParams;
    private HashMap<String,Object> contexts;
    private String rootDir;
    private String displayName;
    
    /**
     * Constructor for FakeContext
     */
    public FakeContext() {
        attributes = new HashMap<String,Object>();
        initParams = new HashMap<String,String>();
        contexts = new HashMap<String,Object>();
    }
    
    /**
     * Returns the servlet container attribute with 
     * the given name, or null if there is no attribute 
     * by that name.
     * @param name A String specifying the name of the attribute.
     * @return An Object containing the value of the attribute, 
     * or null if no attribute exists matching the given name.
     */
    public Object getAttribute(String name) {
        return attributes.get(name);
    }
    
    /**
     * Returns an Enumeration containing the attribute 
     * names available within this servlet context.
     * @return An Enumeration of attribute names.
     */
    public Enumeration getAttributeNames() {
        Set<String> keys = attributes.keySet();
        Vector<String> atts = new Vector<String>(keys);
        return atts.elements();
    }
    
    /**
     * Returns a ServletContext object that corresponds 
     * to a specified URL on the server.
     * @param uripath
     * @return the ServletContext object that corresponds to the named URL.
     */
    public ServletContext getContext(String uripath) {
        return (ServletContext) contexts.get(uripath);
    }
    
    /**
     * Returns a String containing the value of the named 
     * context-wide initialization parameter, or null if 
     * the parameter does not exist.
     * @param A String containing the name of the parameter 
     * whose value is requested.
     * @return A String containing the parameter value.
     */
    public String getInitParameter(String name) {
        return initParams.get(name);
    }
    
    /**
     * Returns the names of the context's initialization parameters 
     * as an Enumeration of String objects, or an empty Enumeration 
     * if the context has no initialization parameters.
     * @return An Enumeration of String objects containing the names 
     * of the context's initialization parameters.
     */
    public Enumeration getInitParameterNames() {
        Set<String> keys = initParams.keySet();
        Vector<String> atts = new Vector<String>(keys);
        return atts.elements();
    }
    
    /**
     * Returns the major version of the Java Servlet 
     * API that this servlet container supports.
     * @return 2
     */
    public int getMajorVersion() {
        return 2;
    }
    
    /**
     * Returns the MIME type of the specified file, 
     * or null if the MIME type is not known.
     */
    public String getMimeType(String file) {
        return null;
    }
    
    /**
     * Returns the minor version of the Servlet 
     * API that this servlet container supports.
     * @return 4
     */
    public int getMinorVersion() {
        return 4;
    }
    
    /**
     * Returns a RequestDispatcher object that acts as a wrapper 
     * for the named servlet.
     * @param name
     * @return null
     */
    public RequestDispatcher getNamedDispatcher(String name) {
        return null;
    }
    
    /**
     * Returns a String containing the real path for a given virtual path.
     * @param path A String specifying a virtual path
     * @return A String specifying the real path, or null if the translation 
     * cannot be performed
     */
    public String getRealPath(String path) {
        String realPath = path.startsWith("/") ? rootDir + path : rootDir+"/"+path;
        if (Paths.get(realPath).toFile().exists()) return realPath;
        return null;
    }
    
    /**
     * Returns a RequestDispatcher object that acts as a wrapper 
     * for the resource located at the given path.
     * @param path
     * @return null
     */
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }
    
    /**
     * Returns a URL to the resource that is mapped to a specified path.
     * @param path
     * @return null
     */
    public java.net.URL getResource(String path) {
        return null;
    }
    
    /**
     * Returns the resource located at the named path as an InputStream object.
     * @param path
     * @return null
     */
    public java.io.InputStream getResourceAsStream(String path) {
        return null;
    }
    
    /**
     * Returns a directory-like listing of all the paths to resources 
     * within the web application whose longest sub-path matches the 
     * supplied path argument.
     * @param path
     * @return null
     */
    public java.util.Set getResourcePaths(String path) {
        return null;
    }
    
    /**
     * Returns the name and version of the servlet container 
     * on which the servlet is running.
     * @return A String containing at least the servlet 
     * container name and version number.
     */
    public String getServerInfo() {
        return "Zhishen Wen's server/1.0";
    }
    
    /**
     * This method was originally defined to retrieve a servlet 
     * from a ServletContext.
     * @param name
     * @return null
     */
    @Deprecated
    public Servlet getServlet(String name) {
        return null;
    }
    
    /**
     * Returns the name of this web application corresponding 
     * to this ServletContext as specified in the deployment 
     * descriptor for this web application by the display-name 
     * element.
     */
    public String getServletContextName() {
        return displayName;
    }
    
    /**
     * This method was originally defined to return 
     * an Enumeration of all the servlet names known 
     * to this context.
     * @return null
     */
    @Deprecated
    public Enumeration getServletNames() {
        return null;
    }
    
    /**
     * This method was originally defined to return 
     * an Enumeration of all the servlets known to 
     * this servlet context.
     * @return null
     */
    @Deprecated
    public Enumeration getServlets() {
        return null;
    }
    
    /**
     * This method was originally defined to write an exception's 
     * stack trace and an explanatory error message to the 
     * servlet log file.
     * @param exception
     * @param msg
     */
    @Deprecated
    public void log(Exception exception, String msg) {
        log(msg, (Throwable) exception);
    }
    
    /**
     * Writes the specified message to a servlet log file, 
     * usually an event log.
     * @param msg Message to write to the log.
     */
    public void log(String msg) {
        System.err.println(msg);
    }
    
    /**
     * Writes an explanatory message and a stack trace for 
     * a given Throwable exception to the servlet log file.
     * @param msg Message to write to the log.
     * @param throwable Throwable to throw.
     */
    public void log(String message, Throwable throwable) {
        System.err.println(message);
        throwable.printStackTrace(System.err);
    }
    
    /**
     * Removes the attribute with the given name from the 
     * servlet context.
     * @param name A String specifying the name of the 
     * attribute to be removed.
     */
    public void removeAttribute(String name) {
        attributes.remove(name);
    }
    
    /**
     * Binds an object to a given attribute name in this 
     * servlet context.
     * @param name A String specifying the name of the 
     * attribute.
     * @param object An Object representing the attribute 
     * to be bound.
     */
    public void setAttribute(String name, Object object) {
        attributes.put(name, object);
    }
    
    //---------------------------------------------
    //   Helper not visible outside the package.
    //   But give it public visibility to enable
    //   JUnit tests.
    //---------------------------------------------
    public void setInitParam(String name, String value) {
        initParams.put(name, value);
    }
    
    public void setRealPathInfo(String dir) {
        rootDir = dir;
    }
    
    public void setDisplayName(String name) {
        displayName = name;
    }
    
}
