package edu.upenn.cis455.youtube;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Class for the web interface.
 * @author Zhishen Wen
 * @version Apr 1, 2013
 */
@SuppressWarnings("serial")
public class YouTubeSearch extends HttpServlet{
    private boolean junitFlag = false;
    
    /** Handles GET request */
    protected void doGet(HttpServletRequest request, 
                         HttpServletResponse response) 
                                 throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><head><title>YouTube Video Finder</title></head>");
        out.println("<body><h1>Jason Wen's YouTube Video Finder</h1><hr align=\"left\" width=\"650\"/>");
        out.println("<h3>Author: Zhishen Wen | PennID: wzhishen</h3>");
        out.println("<form method=\"post\" action=\""+(junitFlag?"":request.getContextPath())+"/youtube\">");
        out.println("<p>Keyword:<br><input type=\"text\" name=\"keyword\" size=\"30\">");
        out.println("<input type=\"submit\" value=\"Search It!\" style=\"font-size:16px\"></p></form>");
        copyright(6, out);
        out.println("</body></html>");
        out.close();
    }
    
    /** Handles POST request */
    protected void doPost(HttpServletRequest request, 
                          HttpServletResponse response)
                                  throws IOException {
        String keyword = request.getParameter("keyword");
        if (keyword == null || keyword.isEmpty()) {
            doGet(request, response);
            return;
        }
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try {
            // establish connection to the Pastry ring
            ServletConfig config = getServletConfig();
            ServletContext context = config.getServletContext();
            String host = context.getInitParameter("cacheServer");
            int port = Integer.parseInt(context.getInitParameter("cacheServerPort"));
            Socket socket = new Socket(host, port);
            
            // make a query to the Pastry ring
            PrintWriter outToRing = null;
            BufferedReader inFromRing = null;
            outToRing = new PrintWriter(socket.getOutputStream(), true);
            inFromRing = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // SOAP-style
            String reqMsg = "";
            reqMsg += "POST /YouTubeSearch HTTP/1.1\r\n";
            reqMsg += "Host: "+host+":"+port+"\r\n";
            reqMsg += "Content-Type: application/soap+xml; charset=ISO-8859-2\r\n";
            String reqBody = generateSoapMsg(keyword, host);
            reqMsg += "Content-Length: "+reqBody.length()+"\r\n";
            reqMsg += "\r\n";
            reqMsg += reqBody;
            outToRing.println(reqMsg);
            
            // receive a response from the Pastry ring
            while (inFromRing.readLine() == null)
                Thread.sleep(1000);
            while (!(inFromRing.readLine()).trim().isEmpty());
            String line = inFromRing.readLine();
            
            // process the result to front end
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(line.getBytes()));
            printQueryResults(doc, keyword, request, out);
            
            outToRing.close();
            inFromRing.close();
            socket.close();
        }
        catch (Exception e) {
            printRspMsg(
                    "Internal Server Error",
                    "Exception occurred during communication between servlet front end and Pastry ring!",
                    request,
                    out);
            e.printStackTrace();
        }
    }
    
    //---------------------------
    //     private helpers
    //---------------------------
    
    /** prints query results */
    private void printQueryResults(Document doc, String keyword, HttpServletRequest request, PrintWriter out) {
        // banner
        out.println("<html><head><title>YouTube Search Results</title></head>");
        out.println("<body><h1>Jason Wen's YouTube Video Finder</h1><hr align=\"left\" width=\"650\"/>");
        out.println("<h3>Results for <em>'"+keyword+"'</em> (most relevant top):</h3>");
        
        // table content
        NodeList nList = doc.getDocumentElement().getElementsByTagName("m:Entry");
        for (int i = 0; i < nList.getLength(); ++i) {
            try {
                // retrieve video data
                Element entry = (Element) nList.item(i);
                String title = entry.getElementsByTagName("m:Title").item(0).getFirstChild().getNodeValue();
                String uploader = entry.getElementsByTagName("m:Uploader").item(0).getFirstChild().getNodeValue();
                String id = entry.getElementsByTagName("m:Id").item(0).getFirstChild().getNodeValue();
                String count = entry.getElementsByTagName("m:Count").item(0).getFirstChild().getNodeValue();
                String rating = entry.getElementsByTagName("m:Rating").item(0).getFirstChild().getNodeValue();
                String url = entry.getElementsByTagName("m:Url").item(0).getFirstChild().getNodeValue();
                String description = entry.getElementsByTagName("m:Description").item(0).getFirstChild().getNodeValue();
                // display content
                out.println("<table width=\"900\" border=\"10\">");
                out.println("<tr bgcolor=\"#FF99CC\"><td width=\"100\">Title</td><td width=\"800\">" +
                            "<a href=\""+url+"\"><em>" + title + "</em></a></td></tr>");
                out.println("<tr bgcolor=\"#FFFF66\"><td colspan=\"2\">" +
                            "<strong>Uploader</strong>: "+uploader+
                            " | <strong>Video ID</strong>: "+id+
                            " | <strong>View Count</strong>: "+count+
                            " | <strong>Rating</strong>: "+rating+"</td></tr>");
                out.println("<tr><td>Description</td><td>"+description+"</td></tr>");
                out.println("<tr><td>Thumbnails</td><td>");
                NodeList nailList = entry.getElementsByTagName("m:Thumbnail");
                if (nailList.getLength() == 0) {
                    out.println("No Thumbnails :/");
                }
                else {
                    for (int j = 0; j < nailList.getLength(); ++j)
                        out.println("<img src=\""+nailList.item(j).getTextContent()+"\" />");
                }
                out.println("</td></tr></table>");
                out.println("<p>&nbsp;</p>");
            }
            catch (NullPointerException e) {
                continue;
            }
        }
        out.println("<h4><a href=\""+(junitFlag?"":request.getContextPath())+
                "/youtube\">Click Here</a> to go back to front page.</h4>");
        
        // copyright
        copyright(0, out);
        out.println("</stable></body></html>");
        out.close();
    }
    
    /** generates a SOAP message */
    private String generateSoapMsg(String kw, String host) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"ISO-8859-2\" ?>" +
        		"<soap:Envelope " +
        		"xmlns:soap=\"http://www.w3.org/2001/12/soap-envelope\" " +
        		"soap:encodingStyle=\"http://www.w3.org/2001/12/soap-encoding\">");
        buffer.append("<soap:Body><m:YouTubeSearch xmlns:m=\"http://"+host+"/youtube\">");
        buffer.append("<m:Keyword>");
        buffer.append(kw);
        buffer.append("</m:Keyword></m:YouTubeSearch>");
        buffer.append("</soap:Body></soap:Envelope>");
        return buffer.toString();
    }
    
    /** prints response messages */
    private void printRspMsg(String title, String content, 
            HttpServletRequest request, PrintWriter out) {
        out.println("<html><script type=\"text/JavaScript\">setTimeout(\"location.href = '"+
                (junitFlag?"":request.getContextPath())+"/youtube';\",4000);</script>");
        out.println("<head><title>"+title+"</title></head>");
        out.println("<body onload=\"setTimeout\"><h1>"+title+"</h1><hr align=\"left\" width=\"650\"/>");
        out.println("<h3>"+content+"</h3>");
        out.println("<h4>Now redirecting to the front page...</h4>");
        out.println("<p>If not directed automatically, please ");
        out.println("<a href=\""+(junitFlag?"":request.getContextPath())+"/youtube\">Click Here</a> to go back to front page.</p>");
        copyright(4, out);
        out.println("</body></html>");
        out.close();
    }
    
    /** display copyright */
    private void copyright(int blankLn, PrintWriter out) {
        for(int i = 0; i < blankLn; ++i) out.println("<p>&nbsp;</p>");
        out.println("<hr/><h5><em>Copyright (C) 2013 Zhishen (Jason) Wen.");
        out.println(" All rights reserved.</em></h5>");
        out.println("<h5><em>Powered by </em><a href=\"http://www.youtube.com/\">");
        out.println("<img src=\"http://www.youtube.com/yt/img/logo_hh.png\" /></a>&nbsp;&nbsp;");
        out.println("<a href=\"https://developers.google.com/\">");
        out.println("<img src=\"https://developers.google.com/_static/images/developers-logo.png\" height=\"30\"/></a></h5>");
    }
    
    //---------------------------
    //  helper for JUnit tests
    //---------------------------
    
    public void setJunitFlag(boolean b) {
        junitFlag = b;
    }
    
}
