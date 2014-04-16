package test.edu.upenn.cis455.servletapi;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionContext;

/**
 * Class for HTTP session
 * @author Zhishen Wen (original auth: Todd J. Green)
 * @version Feb 20, 2013
 */
public class FakeSession implements HttpSession {
    
    /* members */
    private String id;
    private int defaultId = new Random().nextInt(10000);
    private long creationTime;
    private long lastAccessedTime;
    private int maxInactiveInterval; 
    
    private boolean isValid = true;
    private boolean isNew = true;
    
    private Properties attributes = new Properties();
    private ServletContext servletContext;

    /**
     * Constructors for FakeSession
     */
    public FakeSession(String id, ServletContext servletContext) {
        this.id = id;
        this.servletContext = servletContext;
        lastAccessedTime  = creationTime = System.currentTimeMillis();
    }
    
    public FakeSession() {
        id = String.format("MYID%07d", defaultId);
        lastAccessedTime  = creationTime = System.currentTimeMillis();
    }

    /**
     * Returns the time when this session was created, measured 
     * in milliseconds since midnight January 1, 1970 GMT.
     * @return A long specifying when this session was created, 
     * expressed in milliseconds since 1/1/1970 GMT.
     */
    public long getCreationTime() {
        if (!isValid())
            throw new IllegalStateException(
                    "Method is called on an invalidated session");
        return creationTime;
    }

    /**
     * Returns a string containing the unique identifier assigned 
     * to this session.
     * @return a string specifying the identifier assigned to 
     * this session.
     */
    public String getId() {
        if (!isValid())
            throw new IllegalStateException(
                "Method is called on an invalidated session");
        return id;
    }

    /**
     * Returns the last time the client sent a request associated 
     * with this session, as the number of milliseconds since midnight 
     * January 1, 1970 GMT, and marked by the time the container received 
     * the request.
     * @return a long representing the last time the client sent a request 
     * associated with this session, expressed in milliseconds since 
     * 1/1/1970 GMT.
     */
    public long getLastAccessedTime() {
        if (!isValid())
            throw new IllegalStateException(
                "Method is called on an invalidated session");
        return lastAccessedTime;
    }

    /**
     * Returns the ServletContext to which this session belongs.
     * @return The ServletContext object for the web application.
     */
    public ServletContext getServletContext() {
        return servletContext;
    }

    /**
     * Specifies the time, in seconds, between client requests 
     * before the servlet container will invalidate this session. 
     * A negative time indicates the session should never timeout.
     * @param interval An integer specifying the number of seconds.
     */
    public void setMaxInactiveInterval(int interval) {
        maxInactiveInterval = interval;

    }

    /**
     * Returns the maximum time interval, in seconds, that the 
     * servlet container will keep this session open between 
     * client accesses.
     * @return An integer specifying the number of seconds this 
     * session remains open between client requests.
     */
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }
    
    /**
     * As of Version 2.1, this method is deprecated and has no replacement. 
     * It will be removed in a future version of the Java Servlet API.
     * @return null
     */
    @Deprecated
    public HttpSessionContext getSessionContext() {
        return null;
    }

    /**
     * Returns the object bound with the specified name in this session, 
     * or null if no object is bound under the name.
     * @param name A string specifying the name of the object.
     * @return The object with the specified name.
     */
    public Object getAttribute(String name) {
        if (!isValid())
            throw new IllegalStateException(
                "Method is called on an invalidated session");
        return attributes.get(name);
    }

    /**
     * As of Version 2.2, this method is replaced by 
     * getAttribute(java.lang.String).
     * @param name A string specifying the name of the object.
     * @return The object with the specified name.
     */
    @Deprecated
    public Object getValue(String name) {
        if (!isValid())
            throw new IllegalStateException(
                "Method is called on an invalidated session");
        return attributes.get(name);
    }

    /**
     * Returns an Enumeration of String objects containing 
     * the names of all the objects bound to this session.
     * @return An Enumeration of String objects specifying 
     * the names of all the objects bound to this session
     */
    public Enumeration getAttributeNames() {
        if (!isValid())
            throw new IllegalStateException(
                "Method is called on an invalidated session");
        return attributes.keys();
    }

    /**
     * As of Version 2.2, this method is replaced by getAttributeNames()
     * @return An array of String objects specifying the names of all the
     * objects bound to this session.
     */
    @Deprecated
    public String[] getValueNames() {
        if (!isValid())
            throw new IllegalStateException(
                "Method is called on an invalidated session");
        return attributes.keySet().toArray(new String[attributes.size()]);
    }

    /**
     * Binds an object to this session, using the name specified. If an object of 
     * the same name is already bound to the session, the object is replaced.
     * @param name The name to which the object is bound; cannot be null.
     * @param value The object to be bound.
     */
    public void setAttribute(String name, Object value) {
        if (!isValid())
            throw new IllegalStateException(
                "Method is called on an invalidated session");
        if (value == null) removeAttribute(name);
        else {
            attributes.put(name, value);
            try {
                HttpSessionBindingListener listener = (HttpSessionBindingListener) value;
                listener.valueBound(new HttpSessionBindingEvent(this, name, value));
                listener.notifyAll();
            }
            catch (ClassCastException e) { }
        }
    }

    /**
     * As of Version 2.2, this method is replaced by 
     * setAttribute(java.lang.String, java.lang.Object).
     * @param name The name to which the object is bound; cannot be null.
         * @param value The object to be bound.
     */
    @Deprecated
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    /**
     * Removes the object bound with the specified name from this session. 
     * If the session does not have an object bound with the specified name, 
     * this method does nothing.
     * @param name The name of the object to remove from this session.
     */
    public void removeAttribute(String name) {
        if (!isValid())
            throw new IllegalStateException(
                "Method is called on an invalidated session");
        Object value = attributes.remove(name);
        try {
            HttpSessionBindingListener listener = (HttpSessionBindingListener) value;
            listener.valueUnbound(new HttpSessionBindingEvent(this, name, value));
            listener.notifyAll();
        }
        catch (ClassCastException e) { }
    }

    /**
     * As of Version 2.2, this method is replaced by 
     * removeAttribute(java.lang.String).
     * @param The name of the object to remove from this session.
     */
    @Deprecated
    public void removeValue(String name) {
        removeAttribute(name);
    }

    /**
     * Invalidates this session then unbinds any 
     * objects bound to it.
     */
    public void invalidate() {
        cleanUpAttributes();
        isValid = false;
    }
    
    /**
     * Returns true if the client does not yet know about the 
     * session or if the client chooses not to join the session.
     * @return True if the server has created a session, but the 
     * client has not yet joined.
     */
    public boolean isNew() {
        if (!isValid())
            throw new IllegalStateException(
                "Method is called on an invalidated session");
        return isNew;
    }
    
    //---------------------------------------------
    //   Helpers not visible outside the server.
    //   But some might be given public visibility
    //   to enable JUnit tests.
    //---------------------------------------------
    void access() {
        lastAccessedTime = System.currentTimeMillis();
        setNew(false);
    }
    
    /* special constructor that enables JUnit tests */
    public FakeSession(int time) {
        this.maxInactiveInterval = time;
    }

    public void setServletContext(FakeContext context) {
        servletContext = context;
    }
    
    String getid() {
        return id;
    }
    
    boolean isValid() {
        return isValid;
    }
    
    private void setNew(boolean b) {
        isNew = b;
    }
    
    private void cleanUpAttributes() {
        if (!isValid())
            throw new IllegalStateException(
                "Method is called on an invalidated session");
        Iterator iter = attributes.entrySet().iterator();
        while (iter.hasNext()) {
            Entry e = (Entry) iter.next();
            String name = e.getKey() + "";
            Object value = e.getValue();
            iter.remove();
            try {
                HttpSessionBindingListener listener = (HttpSessionBindingListener) value;
                listener.valueUnbound(new HttpSessionBindingEvent(this, name, value));
                listener.notifyAll();
            }
            catch (ClassCastException err) { }
        }
    }
}
