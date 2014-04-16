package test.edu.upenn.cis455;

import test.edu.upenn.cis455.servletapi.FakeConfig;
import test.edu.upenn.cis455.servletapi.FakeContext;
import test.edu.upenn.cis455.servletapi.FakeRequest;
import test.edu.upenn.cis455.servletapi.FakeResponse;
import edu.upenn.cis455.youtube.YouTubeSearch;
import junit.framework.TestCase;

/**
 * JUnit tests for the servlet front end
 * @author Zhishen Wen
 * @version Apr 2, 2013
 */
public class ServiceSystemTest_UI extends TestCase {
    FakeContext context;
    FakeConfig config;
    FakeRequest request;
    FakeResponse response;
    YouTubeSearch servlet;
    String testContent;

    protected void setUp() throws Exception {
        // servlet name
        final String SERVLET_NAME = "youtube";
        
        //---------- set request and response ----------
        request = new FakeRequest(null);
        response = new FakeResponse();
        
        //----------- set context properties -----------
        context = new FakeContext();
        context.setInitParam("cacheServer", "192.168.164.133");
        context.setInitParam("cacheServerPort", "9000");
        
        //----------- set config properties -----------
        config = new FakeConfig();
        config.setServletName(SERVLET_NAME);
        config.setServletContext(context);
        
        //----------- create servlet instances -----------
        String className = "edu.upenn.cis455.youtube.YouTubeSearch";
        Class servletClass = Class.forName(className);
        servlet = (YouTubeSearch) servletClass.newInstance();
        servlet.init(config); // initialize servlet before testing
    }
    
    protected void tearDown() throws Exception {
        servlet.destroy(); // destroy servlet after testing
    }

    public final void testResponse() throws Exception {
        // set expected test result
        String expected =
                "<html><head><title>YouTube Video Finder</title></head>\n" +
                "<body><h1>Jason Wen's YouTube Video Finder</h1><hr align=\"left\" width=\"650\"/>\n" +
                "<h3>Author: Zhishen Wen | PennID: wzhishen</h3>\n" +
                "<form method=\"post\" action=\"/youtube\">\n" +
                "<p>Keyword:<br><input type=\"text\" name=\"keyword\" size=\"30\">\n" +
                "<input type=\"submit\" value=\"Search It!\" style=\"font-size:16px\"></p></form>\n" +
                "<p>&nbsp;</p>\n" +
                "<p>&nbsp;</p>\n" +
                "<p>&nbsp;</p>\n" +
                "<p>&nbsp;</p>\n" +
                "<p>&nbsp;</p>\n" +
                "<p>&nbsp;</p>\n" +
                "<hr/><h5><em>Copyright (C) 2013 Zhishen (Jason) Wen.\n" +
                " All rights reserved.</em></h5>\n" +
                "<h5><em>Powered by </em><a href=\"http://www.youtube.com/\">\n" +
                "<img src=\"http://www.youtube.com/yt/img/logo_hh.png\" /></a>&nbsp;&nbsp;\n" +
                "<a href=\"https://developers.google.com/\">\n" +
                "<img src=\"https://developers.google.com/_static/images/developers-logo.png\" height=\"30\"/></a></h5>\n" +
                "</body></html>\n";
        
        // set up servlet test environment
        request.setMethod("GET");
        servlet.setJunitFlag(true);
        servlet.service(request, response);
        // test servlet
        assertEquals(expected, response.getContentOutput());
    }

}
