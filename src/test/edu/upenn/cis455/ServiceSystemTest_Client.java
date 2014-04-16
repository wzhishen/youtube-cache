package test.edu.upenn.cis455;

import edu.upenn.cis455.youtube.Item;
import edu.upenn.cis455.youtube.StoreDAO;
import edu.upenn.cis455.youtube.YouTubeClient;
import junit.framework.TestCase;

/**
 * JUnit tests for YouTubeClient
 * @author Zhishen Wen
 * @version Apr 2, 2013
 */
public class ServiceSystemTest_Client extends TestCase {
    // temp path to hold the store
    private final String TEST_STORE_PATH = "/home/cis455/export/tmp";
    YouTubeClient clnt;

    protected void setUp() throws Exception {
        // instantiation
        clnt = new YouTubeClient(TEST_STORE_PATH);
        clnt.setHost("myHost");
        clnt.setJunitFlag(true);
        // prepare store data
        initStore();
        StoreDAO.clearStore();
        StoreDAO.putItem(new Item("news", "newsContent"));
        StoreDAO.putItem(new Item("news2", "newsContent2"));
        StoreDAO.putItem(new Item("news3", "newsContent3"));
        closeStore();
    }
    
    public final void testSearchVideos() {
        String res = "HTTP/1.1 200 OK\r\n" +
        		"Content-Type: application/soap+xml; charset=ISO-8859-2\r\n" +
        		"Content-Length: 306\r\n\r\n" +
        		"<?xml version=\"1.0\" encoding=\"ISO-8859-2\" ?>" +
        		"<soap:Envelope " +
        		"xmlns:soap=\"http://www.w3.org/2001/12/soap-envelope\" " +
        		"soap:encodingStyle=\"http://www.w3.org/2001/12/soap-encoding\">" +
        		"<soap:Body>" +
        		"<m:YouTubeSearchResponse " +
        		"xmlns:m=\"http://myHost/youtube\">" +
        		"newsContent" +
        		"</m:YouTubeSearchResponse>" +
        		"</soap:Body>" +
        		"</soap:Envelope>";
        assertEquals(res, clnt.searchVideos("news"));
        
        res = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/soap+xml; charset=ISO-8859-2\r\n" +
                "Content-Length: 307\r\n\r\n" +
                "<?xml version=\"1.0\" encoding=\"ISO-8859-2\" ?>" +
                "<soap:Envelope " +
                "xmlns:soap=\"http://www.w3.org/2001/12/soap-envelope\" " +
                "soap:encodingStyle=\"http://www.w3.org/2001/12/soap-encoding\">" +
                "<soap:Body>" +
                "<m:YouTubeSearchResponse " +
                "xmlns:m=\"http://myHost/youtube\">" +
                "newsContent2" +
                "</m:YouTubeSearchResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";
        assertEquals(res, clnt.searchVideos("news2"));

        res = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/soap+xml; charset=ISO-8859-2\r\n" +
                "Content-Length: 307\r\n\r\n" +
                "<?xml version=\"1.0\" encoding=\"ISO-8859-2\" ?>" +
                "<soap:Envelope " +
                "xmlns:soap=\"http://www.w3.org/2001/12/soap-envelope\" " +
                "soap:encodingStyle=\"http://www.w3.org/2001/12/soap-encoding\">" +
                "<soap:Body>" +
                "<m:YouTubeSearchResponse " +
                "xmlns:m=\"http://myHost/youtube\">" +
                "newsContent3" +
                "</m:YouTubeSearchResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";
        assertEquals(res, clnt.searchVideos("news3"));
    }
    
    private void initStore() {
        StoreDAO.setStorePath(TEST_STORE_PATH);
        StoreDAO.setup();
    }
    
    private void closeStore() {
        StoreDAO.shutdown();
    }

}
