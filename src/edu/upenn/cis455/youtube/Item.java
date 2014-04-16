package edu.upenn.cis455.youtube;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

/**
 * Class for cache items.
 * @author Zhishen Wen
 * @version Mar 29, 2013
 */
@Entity
public class Item {
    @PrimaryKey
    private String keyword = "";
    private String content = "";
    
    //-----------------------
    // constructors
    //-----------------------
    
    public Item(String keyword, String content) {
        this.keyword = keyword;
        this.content = content;
    }

    public Item() { 
        /* BerkeleyDB needs a default constructor*/ 
    }
    
    //-----------------------
    // getters and setters
    //-----------------------

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    //--------------------------
    //  helper for JUnit tests
    //--------------------------
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Item)) return false;
        Item that = (Item) o;
        return this.keyword.equals(that.keyword) &&
                this.content.equals(that.content);
    }

}
