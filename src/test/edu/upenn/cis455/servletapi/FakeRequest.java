package test.edu.upenn.cis455.servletapi;

import java.io.*;
import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Class for HTTP request
 * @author Zhishen Wen (original auth: Todd J. Green)
 * @version Feb 20, 2013
 */
public class FakeRequest implements HttpServletRequest {
        
        /* members */
        private List<Header> headers;
        private Set<Cookie> cookies;
        private Map<String, ArrayList<String>> parameters;
        private byte[] content;
        private Properties attributes;
        private FakeSession session = null;
        
        private String method;
        private String requestUri;
        private String pathInfo;
        private String queryString;
        private String serverName = "localhost";
        private String scheme = "http";
        private String protocol = "HTTP/1.1";
        private String charEncoding = "ISO-8859-1";
        private String remoteAddr = "127.0.0.1";
        private String remoteHost = "localhost";
        private String localName = "localhost";
        private String localAddr = "127.0.0.1";
        private String contentType = "text/html";
        private String servletPath = "";
        
        private int serverPort = 80;
        private int remotePort = 80;
        private int localPort = 80;
        
        /**
         * Constructor for FakeSession
         */
	public FakeRequest(FakeSession session) {
		this.session = session;
		headers = new ArrayList<Header>();
	        parameters = new HashMap<String, ArrayList<String>>();
	        attributes = new Properties();
	}
	
	/**
	 * Returns the name of the authentication scheme used to 
	 * protect the servlet. Our implementation only returns
	 * BASIC AUTH ("BASIC").
	 * @return The name of the authentication scheme.
	 */
	public String getAuthType() {
	        return BASIC_AUTH;
	}

	/**
	 * Returns an array containing all of the Cookie objects the client 
	 * sent with this request. This method returns null if no cookies 
	 * were sent.
	 * @return An array of all the Cookies included with this request, 
	 * or null if the request has no cookies
	 */
	public Cookie[] getCookies() {
	    if (cookies.isEmpty()) return null;
	    Cookie[] c = new Cookie[cookies.size()];
	    int i = 0;
	    for (Cookie cookie : cookies) c[i++] = cookie;
	    return c;
	}

	/**
	 * Returns the value of the specified request header as a long value 
	 * that represents a Date object. 
	 * @param name The header name.
	 * @return A long value representing the date specified in the header 
	 * expressed as the number of milliseconds since January 1, 1970 GMT, 
	 * or -1 if the named header was not included with the request.
	 */
	public long getDateHeader(String name) {
	    Header header = findHeader(name);
	    Object value;
	    if (header == null) value = null;
	    else value = header.getValue();
	    
	    if (value instanceof Date) return ((Date) value).getTime();
	    if (value instanceof Number) return ((Number) value).longValue();
	    if (value != null) throw new IllegalArgumentException("'" + value +"' cannot be converted to a date.");
	    return -1;
	}

	/**
	 * Returns the value of the specified request header as a String. 
	 * If the request did not include a header of the specified name, 
	 * this method returns null. If there are multiple headers with 
	 * the same name, this method returns the first head in the 
	 * request.
	 * @param name The header name.
	 * @return A String containing the value of the requested header, 
	 * or null if the request does not have a header of that name.
	 */
	public String getHeader(String name) {
	    Header header = findHeader(name);
            Object value;
            if (header == null) value = null;
            else value = header.getValue();
            return value + "";
	}

	/**
	 * Returns all the values of the specified request header 
	 * as an Enumeration of String objects.
	 * @param name A String specifying the header name.
	 * @return An Enumeration containing the values of the 
	 * requested header.
	 */
	public Enumeration getHeaders(String name) {
	        if (headers.isEmpty()) 
                    return Collections.enumeration(Collections.EMPTY_LIST);
	        ArrayList<Header> hs = findHeaders(name);
	        ArrayList<String> values = new ArrayList<String>();
	        for (Header h : hs)
	            values.add(h.getValue());
	        return Collections.enumeration(values);
	}

	/**
	 * Returns an enumeration of all the header names this request contains. 
	 * If the request has no headers, this method returns an empty enumeration.
	 * @return An enumeration of all the header names sent with this request; 
	 * if the request has no headers, an empty enumeration.
	 */
	public Enumeration getHeaderNames() {
	        if (headers.isEmpty()) 
                    return Collections.enumeration(Collections.EMPTY_LIST);
	        ArrayList<String> names = new ArrayList<String>();
	        for (Header h : headers)
	            names.add(h.getName());
		return Collections.enumeration(names);
	}

	/**
	 * Returns the value of the specified request header as an int. 
	 * If the request does not have a header of the specified name, 
	 * this method returns -1.
	 * @param name A String specifying the name of a request header.
	 * @return An integer expressing the value of the request header 
	 * or -1 if the request doesn't have a header of this name.
	 */
	public int getIntHeader(String name) {
                Header header = findHeader(name);
	        Object value;
	        if (header == null) value = null;
                else value = header.getValue();
	        
	        if (value instanceof String) return Integer.parseInt(value + "");
	        if (value instanceof Number) return ((Number) value).intValue();
	        if (value != null) throw new NumberFormatException("'" + value +"' cannot be converted to an int.");
	        return -1;
	}

	/**
	 * Returns the name of the HTTP method with which this request was made.
	 * @return A String specifying the name of the method with which this 
	 * request was made.
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Returns any extra path information associated with the URL the 
	 * client sent when it made this request.
	 * @return A  String, decoded by the web container, specifying 
	 * extra path information that comes after the servlet path but 
	 * before the query string in the request URL; or null if the 
	 * URL does not have any extra path information.
	 */
	public String getPathInfo() {
		return pathInfo;
	}

	/**
	 * Returns any extra path information after the servlet name 
	 * but before the query string, and translates it to a real path.
	 * Not required in the spec.
	 * @return null
	 */
	public String getPathTranslated() {
		return null;
	}

	/**
	 * Returns the portion of the request URI that indicates the 
	 * context of the request. Our implementation only returns an 
	 * empty string.
	 * @return A String specifying the portion of the request URI 
	 * that indicates the context of the request
	 */
	public String getContextPath() {
		return "";
	}

	/**
	 * Returns the query string that is contained in the request URL 
	 * after the path. This method returns null if the URL does not 
	 * have a query string.
	 * @return A String containing the query string or null if the 
	 * URL contains no query string. The value is not decoded by 
	 * the container.
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * Returns the login of the user making this request, if the 
	 * user has been authenticated, or null if the user has not 
	 * been authenticated. Our implementation only returns null.
	 * @return null
	 */
	public String getRemoteUser() {
		return null;
	}

	/**
	 * Returns a boolean indicating whether the authenticated 
	 * user is included in the specified logical "role".
	 * Not required in the spec.
	 * @return false
	 */
	public boolean isUserInRole(String arg0) {
		return false;
	}

	/**
	 * Returns a java.security.Principal object containing 
	 * the name of the current authenticated user.
	 * Not required in the spec.
	 */
	public java.security.Principal getUserPrincipal() {
		return null;
	}

	/**
	 * Returns the session ID specified by the client. This 
	 * may not be the same as the ID of the current valid 
	 * session for this request. If the client did not specify 
	 * a session ID, this method returns null.
	 * @return A String specifying the session ID, or null 
	 * if the request did not specify a session ID.
	 */
	public String getRequestedSessionId() {
	        HttpSession session = this.getSession(false);
	        if (session != null) return session.getId();
		return null;
	}

	/**
	 * Returns the part of this request's URL from the protocol name 
	 * up to the query string in the first line of the HTTP request.
	 * @return A String containing the part of the URL from the protocol 
	 * name up to the query string.
	 */
	public String getRequestURI() {
		return requestUri;
	}

	/**
	 * Reconstructs the URL the client used to make the request. 
	 * The returned URL contains a protocol, server name, port 
	 * number, and server path, but it does not include query 
	 * string parameters.
	 * @return A StringBuffer object containing the reconstructed URL.
	 */
	public StringBuffer getRequestURL() {
	        StringBuffer buffer = new StringBuffer();
	        buffer.append("http://").append(serverName);
	        buffer.append(":").append(serverPort);
	        buffer.append(getRequestURI());
	        return buffer;
	}

	/**
	 * Returns the part of this request's URL that calls the servlet.
	 * @return A String containing the name or path of the servlet 
	 * being called, as specified in the request URL, decoded, or 
	 * an empty string if the servlet used to process the request 
	 * is matched using the "/*" pattern.
	 */
	public String getServletPath() {
		return servletPath;
	}

	/**
	 * Returns the current HttpSession associated with this request 
	 * or, if there is no current session and create is true, 
	 * returns a new session.
	 * @param create true to create a new session for this request 
	 * if necessary; false to return null if there's no current session.
	 * @return the HttpSession associated with this request or null if 
	 * create is false and the request has no valid session.
	 */
	public HttpSession getSession(boolean create) {
		if (create) {
			if (! hasSession()) {
				session = new FakeSession();
			}
		} else {
			if (! hasSession()) {
				session = null;
			}
		}
		return session;
	}

	/**
	 * Returns the current session associated with this request, 
	 * or if the request does not have a session, creates one.
	 * @return The HttpSession associated with this request.
	 */
	public HttpSession getSession() {
		return getSession(true);
	}

	/**
	 * Checks whether the requested session ID came in as a cookie.
	 * @return true if the session ID came in as a cookie; 
	 * otherwise, false.
	 */
	public boolean isRequestedSessionIdValid() {
		return session != null && session.isValid();
	}

	/**
	 * Checks whether the requested session ID came in as a cookie.
	 * @return true if the session ID came in as a cookie; 
	 * otherwise, false.
	 */
	public boolean isRequestedSessionIdFromCookie() {
		return true;
	}

	/**
	 * Checks whether the requested session ID came in as 
	 * part of the request URL.
	 * @return true if the session ID came in as part of a URL; 
	 * otherwise, false.
	 */
	public boolean isRequestedSessionIdFromURL() {
		return false;
	}

	/**
	 * As of Version 2.1 of the Java Servlet API, 
	 * use isRequestedSessionIdFromURL() instead.
	 * @return false
	 */
	@Deprecated
	public boolean isRequestedSessionIdFromUrl() {
		return false;
	}

	/**
	 * Returns the value of the named attribute as an Object, 
	 * or null if no attribute of the given name exists.
	 * @param name A String specifying the name of the attribute.
	 * @return Aan Object containing the value of the attribute, 
	 * or null if the attribute does not exist
	 */
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	/**
	 * Returns an Enumeration containing the names of 
	 * the attributes available to this request. This 
	 * method returns an empty Enumeration if the 
	 * request has no attributes available to it.
	 * @return An Enumeration of strings containing 
	 * the names of the request's attributes.
	 */
	public Enumeration getAttributeNames() {
		return attributes.keys();
	}

	/**
	 * Returns the name of the character encoding used in the 
	 * body of this request. This method returns null if the 
	 * request does not specify a character encoding.
	 * @return String containing the name of the character 
	 * encoding, or null if the request does not specify 
	 * a character encoding.
	 */
	public String getCharacterEncoding() {
		return charEncoding;
	}

	/**
	 * Overrides the name of the character encoding used in 
	 * the body of this request. This method must be called 
	 * prior to reading request parameters or reading input 
	 * using getReader().
	 * @param env A String containing the name of the character 
	 * encoding.
	 */
	public void setCharacterEncoding(String env)
			throws UnsupportedEncodingException {
	    if(env.equalsIgnoreCase("ISO-8859-1") ||
	            env.equalsIgnoreCase("utf-8") ||
	            env.equalsIgnoreCase("us-ascii")) {
	        charEncoding = env;
	        return;
	    }
	    throw new UnsupportedEncodingException();
	}

	/**
	 * Returns the length, in bytes, of the request body 
	 * and made available by the input stream, or -1 if 
	 * the length is not known.
	 * @return An integer containing the length of the 
	 * request body or -1 if the length is not known.
	 */
	public int getContentLength() {
		if (content != null) return content.length;
		return -1;
	}

	/**
	 * Returns the MIME type of the body of the request, 
	 * or null if the type is not known. Our implementation
	 * returns "text/html" by default.
	 * @return A String containing the name of the MIME type 
	 * of the request, or null if the type is not known.
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Retrieves the body of the request as binary data using 
	 * a ServletInputStream. Either this method or getReader() 
	 * may be called to read the body, not both.
	 * Not required in the spec.
	 * @return null
	 */
	public ServletInputStream getInputStream() throws IOException {
		return null;
	}

	/**
	 * Returns the name and version of the protocol the request 
	 * uses in the form protocol/majorVersion.minorVersion.
	 * @return A String containing the protocol name and version 
	 * number.
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * Returns the name of the scheme used to make this request.
	 * Our implementation only returns "http".
	 * @return A String containing the name of the scheme used 
	 * to make this request.
	 */
	public String getScheme() {
		return scheme;
	}

	/**
	 * Returns the host name of the server to which the request 
	 * was sent. It is the value of the part before ":" in the 
	 * Host header value, if any, or the resolved server name, 
	 * or the server IP address.
	 * @return A String containing the name of the server.
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * Returns the port number to which the request was sent. It 
	 * is the value of the part after ":" in the Host header value, 
	 * if any, or the server port where the client connection was 
	 * accepted on.
	 * @return An integer specifying the port number.
	 */
	public int getServerPort() {
		return serverPort;
	}

	/**
	 * Returns the Internet Protocol (IP) address of the client 
	 * or last proxy that sent the request. 
	 * @ A String containing the IP address of the client that 
	 * sent the request.
	 */
	public String getRemoteAddr() {
		return remoteAddr;
	}

	/**
	 * Returns the fully qualified name of the client or the 
	 * last proxy that sent the request.
	 */
	public String getRemoteHost() {
		return remoteHost;
	}

	/**
	 * Stores an attribute in this request. Attributes 
	 * are reset between requests. This method is most 
	 * often used in conjunction with RequestDispatcher.
	 * @param name A String specifying the name of the 
	 * attribute.
	 * @param o The Object to be stored.
	 */
	public void setAttribute(String name, Object o) {
	        if (o != null) attributes.put(name, o);
	        else attributes.remove(name);
	}

	/**
	 * Removes an attribute from this request. This 
	 * method is not generally needed as attributes 
	 * only persist as long as the request is being 
	 * handled.
	 * @param name A String specifying the name of 
	 * the attribute to remove.
	 */
	public void removeAttribute(String name) {
	        attributes.remove(name);
	}

	/**
	 * Returns the preferred Locale that the client will 
	 * accept content in, based on the Accept-Language 
	 * header. Our implementation only returns null.
	 * @return The preferred Locale for the client.
	 */
	public Locale getLocale() {
		return null;
	}

	/**
	 * Returns an Enumeration of Locale objects indicating, 
	 * in decreasing order starting with the preferred locale, 
	 * the locales that are acceptable to the client based 
	 * on the Accept-Language header.
	 * Not required in the spec.
	 * @return null
	 */
	public Enumeration getLocales() {
		return null;
	}

	/**
	 * Returns a boolean indicating whether this request 
	 * was made using a secure channel. Our implementation 
	 * only return false.
	 * @return A boolean indicating if the request was 
	 * made using a secure channel.
	 */
	public boolean isSecure() {
		return false;
	}

	/**
	 * Returns a RequestDispatcher object that acts as a wrapper 
	 * for the resource located at the given path.
	 * Not required in the spec.
	 * @return null
	 */
	public RequestDispatcher getRequestDispatcher(String path) {
		return null;
	}

	/**
	 * As of Version 2.1 of the Java Servlet API, use 
	 * ServletContext.getRealPath(java.lang.String) instead.
	 * @param String path
	 * @return null
	 */
	@Deprecated
	public String getRealPath(String path) {
		return null;
	}

	/**
	 * Returns the Internet Protocol (IP) source port of 
	 * the client or last proxy that sent the request.
	 * @return An integer specifying the port number.
	 */
	public int getRemotePort() {
		return remotePort;
	}

	/**
	 * Returns the host name of the Internet Protocol (IP) 
	 * interface on which the request was received.
	 * @return A String containing the host name of the 
	 * IP on which the request was received.
	 */
	public String getLocalName() {
		return localName;
	}

	/**
	 * Returns the Internet Protocol (IP) address 
	 * of the interface on which the request was 
	 * received.
	 * @return A String containing the IP address 
	 * on which the request was received.
	 */
	public String getLocalAddr() {
		return localAddr;
	}

	/**
	 * Returns the Internet Protocol (IP) port number of 
	 * the interface on which the request was received.
	 */
	public int getLocalPort() {
		return localPort;
	}
	
	/**
	 * Returns the value of a request parameter as a String, 
	 * or null if the parameter does not exist. Request 
	 * parameters are extra information sent with the request.
	 * @param name A String specifying the name of the parameter.
	 * @return A String representing the single value of the 
	 * parameter.
	 */
        public String getParameter(String name) {
                return getParameterValues(name)[0];
        }

        /**
         * Returns an Enumeration of String objects containing the 
         * names of the parameters contained in this request. If 
         * the request has no parameters, the method returns an 
         * empty Enumeration.
         * @return an Enumeration of String objects, each String 
         * containing the name of a request parameter; or an empty 
         * Enumeration if the request has no parameters.
         */
        public Enumeration getParameterNames() {
                if (parameters.isEmpty()) 
                    return Collections.enumeration(Collections.EMPTY_LIST);
                return Collections.enumeration(parameters.keySet());
        }
        
        /**
         * Returns an array of String objects containing all of 
         * the values the given request parameter has, or null 
         * if the parameter does not exist.
         * @param name A String containing the name of the 
         * parameter whose value is requested.
         * @return An array of String objects containing the 
         * parameter's values.
         */
        public String[] getParameterValues(String name) {
                List<String> list = parameters.get(name);
                if (list == null) return null;
                return list.toArray(new String[list.size()]);
        }

        /**
         * Returns a java.util.Map of the parameters of this request.
         * @return An immutable java.util.Map containing parameter 
         * names as keys and parameter values as map values. The 
         * keys in the parameter map are of type String. The values 
         * in the parameter map are of type String array.
         */
        public Map getParameterMap() {
                Map<String, String[]> map = new HashMap<String, String[]>();
                for (String name : parameters.keySet()) {
                    List<String> list = parameters.get(name);
                    map.put(name, list.toArray(new String[list.size()]));
                }
                return map;
        }
        
        /**
         * Retrieves the body of the request as character data using a 
         * BufferedReader. The reader translates the character data 
         * according to the character encoding used on the body. 
         * @return A BufferedReader containing the body of the request.
         */
        public BufferedReader getReader() throws IOException {
                BufferedReader reader = null;
                if (content != null) {
                        InputStream in = new ByteArrayInputStream(content);
                        if (getCharacterEncoding() == null) 
                                reader = new BufferedReader(
                                         new InputStreamReader(in));
                        else 
                                reader = new BufferedReader(
                                         new InputStreamReader(in, getCharacterEncoding()));
                }
                return reader;
        }
	
        //---------------------------------------------
        //   Helpers not visible outside the server.
        //   But give it public visibility to enable
        //   JUnit tests.
        //---------------------------------------------
	public void setMethod(String method) {
		this.method = method;
	}
	
	public void setParameter(String name, String value) {
            ArrayList<String> values = parameters.get(name);
            if (values == null) parameters.put(name, new ArrayList<String>());
            parameters.get(name).add(value);
        }
	
	public void setHeader(Header header) {
            headers.add(header);
        }
	
	void setPathInfo(String pathInfo) {
	        this.pathInfo = pathInfo;
	}
	
        void setQueryString(String queryString) {
                this.queryString = queryString;
	}
        
        void setRequestURI(String requestUri) {
                this.requestUri = requestUri;
        }
        
        void setServletPath(String servletPath) {
                this.servletPath = servletPath;
        }
        
        void setProtocol(String protocol) {
                this.protocol = protocol;
        }
        
	void setServerPort(int serverPort) {
                this.serverPort = serverPort;
	}
	
	void setRemotePort(int remotePort) {
                this.remotePort = remotePort;
        }
	
	void setContentType(String contentType) {
                this.contentType = contentType;
        }

	void setContent(byte[] content) {
	        this.content = content;
	}
	
	void setLocalAddr(String localAddr) {
                this.localAddr = localAddr;
	}

	void setLocalPort(int localPort) {
                this.localPort = localPort;
	}
	
	void setRemoteAddr(String addr) {
	    this.remoteAddr = addr;
	}
	
	void setCookie(Cookie cookie) {
	        cookies.add(cookie);
	}
	
	void setHeaders(List<Header> headers) {
	        this.headers = headers;
	}
	
	boolean hasSession() {
		return ((session != null) && session.isValid());
	}
	
	private Header findHeader(String name) {
	        for (Header header : headers)
	            if (header.getName().equalsIgnoreCase(name)) return header;
	        return null;
	}
	
	private ArrayList<Header> findHeaders(String name) {
	    ArrayList<Header> hs = new ArrayList<Header>();
            for (Header header : headers)
                if (header.getName().equalsIgnoreCase(name)) hs.add(header);
            return hs;
        }
	
	/* Header class stub */
	class Header { 
	    String getName() {return null;}
	    String getValue() {return null;}
	}
		
}
