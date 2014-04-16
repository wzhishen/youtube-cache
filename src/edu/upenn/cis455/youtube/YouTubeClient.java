package edu.upenn.cis455.youtube;

import java.net.URL;

import com.google.gdata.client.youtube.YouTubeQuery;
import com.google.gdata.client.youtube.YouTubeQuery.OrderBy;
import com.google.gdata.client.youtube.YouTubeQuery.SafeSearch;
import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.extensions.Rating;
import com.google.gdata.data.media.mediarss.MediaPlayer;
import com.google.gdata.data.media.mediarss.MediaThumbnail;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.VideoFeed;
import com.google.gdata.data.youtube.YouTubeMediaGroup;
import com.google.gdata.data.youtube.YtStatistics;

/**
 * Class for the YouTube client.
 * @author Zhishen Wen
 * @version Apr 1, 2013
 */
public class YouTubeClient {
    /* members */
    private String dbPath;
    private String host;
    /* constants */
    private final String APP_NAME = "Jason Wen's YouTube Video Finder";
    private final String FEED_URL = "http://gdata.youtube.com/feeds/api/videos";
    private final int MAX_SEARCH_RESULTS = 20;
    private final SafeSearch SAFE_SEARCH_PARAM = YouTubeQuery.SafeSearch.NONE;
    private final OrderBy ORDER_BY_PARAM = YouTubeQuery.OrderBy.RELEVANCE;
    
    private boolean junitFlag = false;
    
    /** constructor for YouTubeClient */
    public YouTubeClient(String dbPath) {
        this.dbPath = dbPath;
    }
    
    /** searches videos given a keyword and returns a RESULT message */
    public String searchVideos(String kw) {
        // init store
        StoreDAO.setStorePath(dbPath);
        StoreDAO.setup();
        Item item = StoreDAO.getItem(kw);
        
        // print message
        if (!junitFlag)
            System.err.println(
                    "Query for " + kw + " resulted in a cache " + 
                    (item == null ? "MISS." : "HIT."));
        
        // make a query
        String result = "";
        boolean ok = true;
        try {
            if (!junitFlag && item == null) {
                item = youTubeSearch(kw);
                StoreDAO.putItem(item);
            }
            result += generateResultMsg(item);
        }
        catch (Exception e) {
            ok = false;
            System.err.println(
                    "Unable to retrieve query results " +
                    "or make a remote query!");
        }
        
        // disconnect from store
        StoreDAO.shutdown();
        return ok ?
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/soap+xml; charset=ISO-8859-2\r\n" +
                "Content-Length: " + result.length() + "\r\n" +
                "\r\n" +
                result : null;
    }
    
    /** sets host address */
    public void setHost(String host) {
        this.host = host;
    }
    
    //---------------------------
    //     private helpers
    //---------------------------
    
    /** generates the RESULT message */
    private String generateResultMsg(Item item) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"ISO-8859-2\" ?>");
        buffer.append("<soap:Envelope " +
        		"xmlns:soap=\"http://www.w3.org/2001/12/soap-envelope\" " +
        		"soap:encodingStyle=\"http://www.w3.org/2001/12/soap-encoding\">");
        buffer.append("<soap:Body><m:YouTubeSearchResponse xmlns:m=\"http://"+host+"/youtube\">");
        buffer.append(item.getContent());
        buffer.append("</m:YouTubeSearchResponse></soap:Body></soap:Envelope>");
        return buffer.toString();
    }
    
    /** returns an Item object given a keyword */
    private Item youTubeSearch(String kw) throws Exception {
        // init service
        YouTubeService service = new YouTubeService(APP_NAME);
        YouTubeQuery query = new YouTubeQuery(new URL(FEED_URL));
        
        // order results by the relevance
        query.setOrderBy(ORDER_BY_PARAM);

        // search for keyword
        query.setFullTextQuery(kw);
        query.setMaxResults(MAX_SEARCH_RESULTS);
        query.setSafeSearch(SAFE_SEARCH_PARAM);

        // retrieve results
        VideoFeed videoFeed = service.query(query, VideoFeed.class);
        String content = localizeVideoFeed(videoFeed);
        return new Item(kw, content);
    }
    
    /** converts a VideoFeed object to XML-style document */
    private String localizeVideoFeed(VideoFeed videoFeed) {
        String content = "<m:Entries>";
        for(VideoEntry videoEntry : videoFeed.getEntries() ) {
          content += localizeVideoEntry(videoEntry);
        }
        content += "</m:Entries>";
        return content;
      }
    
    /** converts a VideoEntry object to XML-style document */
    private String localizeVideoEntry(VideoEntry videoEntry) {
        String content = "<m:Entry>";
        
        // title
        content += "<m:Title>"+videoEntry.getTitle().getPlainText()+"</m:Title>";
        
        YouTubeMediaGroup mediaGroup = videoEntry.getMediaGroup();
        // uploader
        content += "<m:Uploader>"+mediaGroup.getUploader()+"</m:Uploader>";
        // video ID
        content += "<m:Id>"+mediaGroup.getVideoId()+"</m:Id>";
        // description
        content += "<m:Description>"+mediaGroup.getDescription().getPlainTextContent()+"</m:Description>";
        
        // video URL
        MediaPlayer mediaPlayer = mediaGroup.getPlayer();
        content += "<m:Url>"+mediaPlayer.getUrl()+"</m:Url>";
        
        // video rating
        Rating rating = videoEntry.getRating();
        content += "<m:Rating>"+(rating != null ? rating.getAverage() : "No Rating :(")+"</m:Rating>";
        
        // view count
        YtStatistics stats = videoEntry.getStatistics();
        content += "<m:Count>"+(stats != null ? stats.getViewCount() : "No View Count :/")+"</m:Count>";
        
        // thumbnails
        content += "<m:Thumbnails>";
        for(MediaThumbnail mediaThumbnail : mediaGroup.getThumbnails()) {
            // only retrieve small pics
            if (mediaThumbnail.getWidth() > 150) continue;
            content += "<m:Thumbnail>"+mediaThumbnail.getUrl()+"</m:Thumbnail>";
        }
        content += "</m:Thumbnails>";
        
        content += "</m:Entry>";
        return normalize(content);
    }
    
    /** normalizes a string */
    private String normalize(String s) {
        return s.replace("&", "&amp;");
    }
    
    //--------------------------
    // helper for JUnit tests
    //--------------------------
    public void setJunitFlag(boolean b) {
        junitFlag = b;
    }
    
}
