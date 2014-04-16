package test.edu.upenn.cis455.servletapi;

import java.io.*;
import java.util.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Class for HTTP response
 * @author Zhishen Wen (original auth: tjgreen)
 * @version Feb 20, 2013
 */
public class FakeResponse implements HttpServletResponse {
    
    /* members */
    private Map<Cookie, Long> cookies;
    private List<Cookie> tmpCookies;
    private List<Cookie> sessionCookies;
    private List<Header> headers;
    
    private OutputStream out;
    private ByteArrayOutputStream headerInfo = new ByteArrayOutputStream();
    private ByteArrayOutputStream content = new ByteArrayOutputStream();
    private PrintWriter writer;
    
    private int status = HttpServletResponse.SC_OK;
    private String statusMsg = "OK";
    private String bodyErrMsg = "";
    private String redirectedUrl;
    private String charEncoding = "ISO-8859-1";
    private String contentType = "text/html";
    private String rawHttpPath;
    private String httpVersion;
    private int contentLength = 0;
    private int bufferSize = 0;
    private Locale locale = Locale.getDefault();
    
    private boolean responseCommitted = false;
    private boolean errorSent = false;
    private boolean redirectSent = false;
    private boolean getWriteIsCalled = false;
        
    /**
     * Constructor for FakeResponse
     */
    public FakeResponse() {
        sessionCookies = new ArrayList<Cookie>();
        tmpCookies = new ArrayList<Cookie>();
        headers = new ArrayList<Header>();
        headerInfo = new ByteArrayOutputStream();
        content = new ByteArrayOutputStream();
    }

    /**
     * Adds the specified cookie to the response. This method can 
     * be called multiple times to set more than one cookie.
     * @param the Cookie to return to the client.
     */
    public void addCookie(Cookie cookie) {
        if (cookie.getMaxAge() >= 0) {
            tmpCookies.add(cookie);
        }
        else
            sessionCookies.add(cookie);
    }

    /** 
     * Returns a boolean indicating whether the named response header has already been set.
     * @param name The header name.
     * @return True if the named response header has already been set; false otherwise.
     */
    public boolean containsHeader(String name) {
        return findHeader(name) != null;
    }

    /**
     * Encodes the specified URL by including the session ID in it, 
     * or, if encoding is not needed, returns the URL unchanged.
     * @param url The url to be encoded.
     * @return The encoded URL if encoding is needed; the unchanged 
     * URL otherwise.
     */
    public String encodeURL(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } 
        catch (UnsupportedEncodingException e) {
            return url;
        }
    }

    /**
     * Encodes the specified URL by including the session ID in it, 
     * or, if encoding is not needed, returns the URL unchanged.
     * @param url The url to be encoded.
     * @return The encoded URL if encoding is needed; the unchanged 
     * URL otherwise.
     */
    public String encodeRedirectURL(String url) {
        try {
                return URLEncoder.encode(url, "UTF-8");
        } 
        catch (UnsupportedEncodingException e) {
                return url;
        }
    }

    /**
     * As of version 2.1, use encodeURL(String url) instead.
     * @param url the url to be encoded.
     * @return The encoded URL if encoding is needed; the unchanged URL otherwise.
     */
    @Deprecated
    public String encodeUrl(String url) {
        return url;
    }

    /**
     * As of version 2.1, use encodeRedirectURL(String url) instead.
     * @param url the url to be encoded.
     * @return The encoded URL if encoding is needed; the unchanged URL otherwise.
     */
    @Deprecated
    public String encodeRedirectUrl(String url) {
        return url;
    }

    /**
     * Sends an error response to the client using the specified status.
     * @param sc The error status code.
     * @param msg The descriptive message.
     */
    public void sendError(int sc, String msg) throws IOException {
        bodyErrMsg += msg;
        sendError(sc);
        
    }

    /**
     * Sends an error response to the client using the specified status.
     * @param sc The error status code.
     */
    public void sendError(int sc) throws IOException {
        errorSent = true;
        if (isCommitted())
            throw new IllegalStateException("Response already committed.");
        status = sc;
        statusMsg = getStatusMsg(sc);
        setCommitted(true);

    }

    /**
     * Sends a temporary redirect response to the client using the 
     * specified redirect location URL. 
     * @param location The redirect location URL.
     */
    public void sendRedirect(String location) throws IOException {
        if (isCommitted())
            throw new IllegalStateException("Response already committed.");
        redirectSent = true;
        redirectedUrl  = parseRedirectLocation(location);
        setStatus(302);
        setCommitted(true);
    }

    /**
     * Sets a response header with the given name and date-value. 
     * The date is specified in terms of milliseconds since the epoch. 
     * @param name The header name.
     * @param date The assigned date value.
     */
    public void setDateHeader(String name, long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
        String value = dateFormat.format(date) + " GMT";
        if(!containsHeader(name)) 
            headers.add(new Header(name, value));
        else 
            findHeader(name).setValue(value);
                
    }

    /**
     * Adds a response header with the given name and date-value. 
     * The date is specified in terms of milliseconds since the epoch. 
     * This method allows response headers to have multiple values.
     * @param name The header name.
     * @param date The assigned date value.
     */
    public void addDateHeader(String name, long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
        String value = dateFormat.format(date) + " GMT";
        headers.add(new Header(name, value));

    }

    /**
     * Sets a response header with the given name and value. 
     * If the header had already been set, the new value 
     * overwrites the previous one.
     * @param name The header name.
     * @param The value assigned.
     */
    public void setHeader(String name, String value) {
        if(!containsHeader(name)) 
            headers.add(new Header(name, value));
        else 
            findHeader(name).setValue(value);

    }

    /**
     * Adds a response header with the given name and value. 
     * This method allows response headers to have multiple values.
     * @param name The header name.
     * @param The value assigned.
     */
    public void addHeader(String name, String value) {
        headers.add(new Header(name, value));     

    }

    /**
     * Sets a response header with the given name and integer value. 
     * If the header had already been set, the new value overwrites the previous one.
     * @param name The header name.
     * @param The integer value assigned. 
     */
    public void setIntHeader(String name, int value) {
        if(!containsHeader(name)) 
            headers.add(new Header(name, value + ""));
        else 
            findHeader(name).setValue(value + "");

    }

    /**
     * Adds a response header with the given name and integer value. 
     * This method allows response headers to have multiple values.
     * @param name The header name.
     * @param The integer value assigned. 
     */
    public void addIntHeader(String name, int value) {
        headers.add(new Header(name, value + ""));

    }

    /**
     * Sets the status code for this response. This method is used 
     * to set the return status code when there is no error.
     * @param sc The status code.
     */
    public void setStatus(int sc) {
        status = sc;
        statusMsg = getStatusMsg(sc);
        content.reset();
    }

    /**
     * As of version 2.1, due to ambiguous meaning of the message parameter. 
     * @param sc The status code.
     * @param sm The status message.
     */
    @Deprecated
    public void setStatus(int sc, String sm) {
        status = sc;
        statusMsg = sm;
    }

    /**
     * Returns the name of the character encoding (MIME charset) 
     * used for the body sent in this response. Our implementation
     * only returns ISO-8859-1 standard.
     * @return A String specifying the name of the character encoding.
     */
    public String getCharacterEncoding() {
        return "ISO-8859-1";
    }

    /**
     * Returns the content type used for the MIME body sent in this response.
     * Our implementation only returns "text/html" by default, and the results 
     * of setContentType if it was previously called.
     * @return A String specifying the content type.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Returns a ServletOutputStream suitable for writing binary data 
     * in the response. Not required in the spec.
     */
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    /**
     * Returns a PrintWriter object that can send character text to the client.
     * @return A PrintWriter object that can return character data to the client.
     */
    public PrintWriter getWriter() throws IOException {
        getWriteIsCalled = true;
        if (writer == null) {
                Writer newWriter;
                newWriter = new OutputStreamWriter(content, getCharacterEncoding());
                writer = new MyPrintWriter(newWriter);
            }
        return writer;
    }

    /**
     * Sets the character encoding (MIME charset) of the response 
     * being sent to the client.
     * @param charset A String specifying only the character set 
     * defined by IANA Character Sets.
     */
    public void setCharacterEncoding(String charset) {
        if (!isCommitted() && !getWriteIsCalled &&
                (charset.equalsIgnoreCase("ISO-8859-1") ||
                 charset.equalsIgnoreCase("utf-8") ||
                 charset.equalsIgnoreCase("us-ascii")))
            charEncoding = charset;
    }

    /**
     * Sets the length of the content body in the response In 
     * HTTP servlets, this method sets the HTTP Content-Length 
     * header.
     * @param len An integer specifying the length of the content 
     * being returned to the client; sets the Content-Length header
     */
    public void setContentLength(int len) {
        contentLength = len;
    }      

    /**
     * Sets the content type of the response being sent to the client, 
     * if the response has not been committed yet.
     * @param type A String specifying the MIME type of the content.
     */
    public void setContentType(String type) {
        if (!isCommitted()) {
        contentType = type;
            int index = type.toLowerCase().indexOf("charset=");
            if (type != null && index != -1) {
                setCharacterEncoding(type.substring(index + "charset=".length()));
            }
        }
    }

    /**
     * Sets the preferred buffer size for the body of the response.
     * @param size The preferred buffer size.
     */
    public void setBufferSize(int size) {
        if (content.size() > 0 || isCommitted())
            throw new IllegalStateException(
                "Buffer size cannot be set after content written or response committed.");
        bufferSize = size;

    }

    /**
     * Returns the actual buffer size used for the response. 
     * If no buffering is used, this method returns 0.
     * @return The actual buffer size used.
     */
    public int getBufferSize() {
        return bufferSize;
    }

    /**
     * Forces any content in the buffer to be written to the client. 
     * A call to this method automatically commits the response, 
     * meaning the status code and headers will be written.
     */
    public void flushBuffer() throws IOException {
        setCommitted(true);
        writeHeaderOutToClient();
        writeContentOutToClient();
        content.reset();
    }

    /**
     * Clears the content of the underlying buffer in the 
     * response without clearing headers or status code.
     */
    public void resetBuffer() {
        if (isCommitted()) 
            throw new IllegalStateException("Response already committed.");
        content.reset();
    }

    /**
     * Returns a boolean indicating if the response has been committed. 
     * A committed response has already had its status code and headers 
     * written.
     * @return A boolean indicating if the response has been committed.
     */
    public boolean isCommitted() {
        return responseCommitted;
    }

    /**
     * Clears any data that exists in the buffer 
     * as well as the status code and headers.
     */
    public void reset() {
        resetBuffer();
        status = HttpServletResponse.SC_OK;
        statusMsg = getStatusMsg(status);
        charEncoding = "ISO-8859-1";
        contentType = "text/html";
        contentLength = 0;
        cookies.clear();
        headers.clear();
    }

    /**
     * Sets the locale of the response, if the 
     * response has not been committed yet.
     * @param loc The locale of the response.
     */
    public void setLocale(Locale loc) {
        if (!isCommitted()) locale = loc;
    }

    /**
     * Returns the locale specified for this response 
     * using the setLocale(java.util.Locale) method.
     * @return The locale specified for this response.
     */
    public Locale getLocale() {
        return locale;
    }
    
    //---------------------------------------------
    //   Helpers not visible outside the server.
    //   But give it public visibility to enable
    //   JUnit tests.
    //---------------------------------------------
    public String getContentOutput() {
        return content.toString();
    }
    
    void setOutputStream(OutputStream out) {
        this.out = out;
    }
    
    void setRawHttpPath(String rawHttpPath) {
        this.rawHttpPath = rawHttpPath;
    }
    
    void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }
    
    void writeHeaderOutToClient() throws IOException {
        if (!isCommitted()) {
            writeOutHeaders();
            headerInfo.write("\r\n".getBytes());
            headerInfo.writeTo(out);          
        }
    }
        
    void writeContentOutToClient() throws IOException {
        content.writeTo(out);
    }
    
    void writeErrOutToClient() throws IOException {
        writeOutHeaders();
        String bodyMsg = "";
        bodyMsg += "<html><body><h1>" + status + " " + statusMsg + "</h1>";
        if (bodyErrMsg.length() != 0)
            bodyMsg += "<h2>" + bodyErrMsg + "</h2></body></html>";
        headerInfo.write(("Content-Length: " + bodyMsg.length() + "\r\n").getBytes());
        headerInfo.write("\r\n".getBytes());
        
        headerInfo.writeTo(out);
        out.write(bodyMsg.getBytes());
    }
    
    void writeRedirectOutToClient() throws IOException {
        writeOutHeaders();
        headerInfo.write("\r\n".getBytes());
        headerInfo.writeTo(out);
    }
    
    void writeServerErrOutToClient() throws IOException {
        out.write((httpVersion + " 500 Internal Server Error\r\n").getBytes());
        out.write("\r\n".getBytes());
        String bodyMsg = "<html><body><h1>500 Internal Server Error</h1>";
        bodyMsg += "<h2>Exception occurred in servlet.</h2></body></html>";
        out.write(bodyMsg.getBytes());
        out.write("\r\n".getBytes());
    }
    
    boolean hasSentError() {
        return errorSent;
    }
    
    boolean hasSentRedirect() {
        return redirectSent;
    }
    
    public Header findHeader(String name) {
        for (Header header : headers)
            if (header.getName().equalsIgnoreCase(name)) return header;
        return null;
    }
    
    private String parseRedirectLocation(String url) {
        String buffer = "";
        if (url.startsWith("/"))
            buffer += "http://localhost"+url;
        else if (url.toLowerCase().startsWith("http://"))
            buffer += url;
        else {
            int index = rawHttpPath.lastIndexOf("/");
            rawHttpPath = rawHttpPath.substring(0, index);
            buffer += rawHttpPath + "/" + url;
        }
        return buffer;
    }
    
    private void setCommitted(boolean b) {
        responseCommitted = b;
    }
    
    private void setCommittedByBufferSize() {
        if (getBufferSize() > 0 && content.size() > getBufferSize()) {
            try { 
                writeHeaderOutToClient();
                writeContentOutToClient();
                setCommitted(true);
                content.reset();
            } 
            catch (IOException e) { }
        }
    }
    
    private void writeOutHeaders() throws IOException {
        headerInfo.write((httpVersion + " "+ status + " " + statusMsg + "\r\n").getBytes());
        if (redirectSent)
            headerInfo.write(("Location: "+ redirectedUrl + "\r\n").getBytes());
        if (contentType.trim().length() != 0)
            headerInfo.write(("Content-Type: "+ contentType + "\r\n").getBytes());
        if (contentLength != 0 && !errorSent)
            headerInfo.write(("Content-Length: "+ contentLength + "\r\n").getBytes());
        if (!headers.isEmpty()) {
            for (String name : getHeaderNames())
                if (findHeaders(name).size() == 1)
                    headerInfo.write((name + ": " + findHeader(name).getValue() + "\r\n").getBytes());
                else
                    headerInfo.write((name + ": " + getAllValues(findHeader(name)) + "\r\n").getBytes());
            }
        if (!cookies.isEmpty()) 
            writeOutCookieHeaders(tmpCookies);
        if (!sessionCookies.isEmpty())
            writeOutCookieHeaders(sessionCookies);
    }
    
    private HashSet<String> getHeaderNames() {
        HashSet<String> set = new HashSet<String>();
        for (Header h : headers) set.add(h.getName());
        return set;
    }
    
    private ArrayList<Header> findHeaders(String name) {
        ArrayList<Header> hs = new ArrayList<Header>();
        for (Header h : headers) {
            if (h.getName().equalsIgnoreCase(name)) hs.add(h);
        }
        return hs;
    }
    
    private String getAllValues(Header header) {
        String buffer = "";
        for (Header h : headers) {
            if (header.getName().equalsIgnoreCase(h.getName()))
                buffer += h.getValue() + ", ";
        }
        return buffer.substring(0, buffer.length()-2);
    }
    
    private void writeOutCookieHeaders(Collection<Cookie> col) throws IOException {
        String buffer = "";
        for (Cookie c : col) {
            if (c.getMaxAge() != 0) {
                buffer +=  "Set-Cookie: ";
                buffer += c.getName() + "=" + c.getValue();
                if(c.getMaxAge() > 0)
                    buffer += "; expires=" + setCookieDate(c);
                if(c.getPath() != null)
                    buffer += "; path=" + c.getPath();
                if(c.getDomain() != null)
                    buffer += "; domain=" + c.getDomain();
                if(c.getComment() != null)
                    buffer += "; comment=" + c.getComment();
                if(c.getSecure())
                    buffer += "; secure";
                buffer += "\r\n";
            }
        }
        headerInfo.write(buffer.getBytes());
    }
    
    private String setCookieDate(Cookie c) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss");
        return dateFormat.format(cookies.get(c)) + " GMT";
    }
    
    private String getStatusMsg(int code) {
        switch (code) {
            case 100:return "Continue"; case 101:return "Switching Protocols";
            case 200:return "OK"; case 201: return "Created";
            case 202:return "Accepted"; case 203: return "Non-Authoritative Information";
            case 204:return "No Content"; case 205: return "Reset Content";
            case 206:return "Partial Content"; case 300: return "Multiple Choices";
            case 301:return "Moved Permanently"; case 302: return "Found";
            case 303:return "See Other"; case 304: return "Not Modified";
            case 305:return "Use Proxy"; case 307: return "Temporary Redirect";
            case 400:return "Bad Request"; case 401: return "Unauthorized";
            case 403:return "Forbidden"; case 404: return "Not Found";
            case 405:return "Method Not Allowed"; case 406: return "Not Acceptable";
            case 407:return "Proxy Authentication Required"; case 408: return "Request Timeout";
            case 409:return "Conflict"; case 410: return "Not Acceptable";
            case 411:return "Length Required"; case 412: return "Precondition Failed";
            case 413:return "Request Entity Too Large"; case 414: return "Request URI Too Long";
            case 415:return "Unsupported Media Type"; case 416: return "Requested Range Not Satisfiable";
            case 417:return "Expectation Failed"; case 500: return "Internal Server Error";
            case 501:return "Not Implemented"; case 502: return "Bad Gateway";
            case 503:return "Not Implemented"; case 504: return "Gateway Timeout";
            case 505: return "HTTP Version Not Supported";
        }
        assert false;
        return null;
    }
    
    /**
     * Inner class that acts as a 
     * wrapper of PrintWriter
     */
    class MyPrintWriter extends PrintWriter {
        
        public MyPrintWriter(Writer out) {
                super(out, true);
        }
        
        public void flush() {
            super.flush();
            try { 
                writeHeaderOutToClient();
                writeContentOutToClient(); 
                setCommitted(true);
                content.reset();
            } 
            catch (IOException e) { }
        }
        
        public void print(Object obj) {
            super.print(obj);
            super.flush();
            setCommittedByBufferSize();
        }
        
        public void print(String s) {
            super.print(s);
            super.flush();
            setCommittedByBufferSize();
        }
        
        public void println(Object obj) {
            super.println(obj);
            super.flush();
            setCommittedByBufferSize();
        }
        
        public void println(String s) {
            print(s + System.lineSeparator());
        }
        
        public void write(int c) {
            super.write(c);
            super.flush();
            setCommittedByBufferSize();
        }
        
        public void write(char buf[], int off, int len) {
            super.write(buf, off, len);
            super.flush();
            setCommittedByBufferSize();
        }
    }
    
    /* Header class stub */
    class Header { 
        Header(String name, String val) { }
        String getName() {return null;}
        String getValue() {return null;}
        void setValue(String val) { }
    }
}
