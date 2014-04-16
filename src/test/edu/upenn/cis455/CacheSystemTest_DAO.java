package test.edu.upenn.cis455;

import java.util.ArrayList;
import java.util.List;

import edu.upenn.cis455.youtube.Item;
import edu.upenn.cis455.youtube.StoreDAO;
import junit.framework.TestCase;

/**
 * JUnit tests for store DAO
 * @author Zhishen Wen
 * @version Apr 2, 2013
 */
public class CacheSystemTest_DAO extends TestCase {
    // temp path to hold the store
    private final String TEST_STORE_PATH = "/home/cis455/export/tmp";
    Item res1, res2, res3;

    protected void setUp() throws Exception {
        initStore();
        StoreDAO.clearStore();
        initTestData();
    }
    
    protected void tearDown() throws Exception {
        closeStore();
    }

    public final void testAccessItem() {
        StoreDAO.putItem(res1);
        StoreDAO.putItem(res2);
        StoreDAO.putItem(res3);
        assertNotNull(StoreDAO.getItem("keyword1"));
        assertNotNull(StoreDAO.getItem("keyword2"));
        assertNotNull(StoreDAO.getItem("keyword3"));
        assertNotSame(res1, StoreDAO.getItem("keyword1"));
        assertNotSame(res2, StoreDAO.getItem("keyword2"));
        assertNotSame(res3, StoreDAO.getItem("keyword3"));
        assertTrue(StoreDAO.getItem("keyword1") instanceof Item);
        assertTrue(StoreDAO.getItem("keyword2") instanceof Item);
        assertTrue(StoreDAO.getItem("keyword3") instanceof Item);
        assertEquals(res1, StoreDAO.getItem("keyword1"));
        assertEquals(res2, StoreDAO.getItem("keyword2"));
        assertEquals(res3, StoreDAO.getItem("keyword3"));
    }

    public final void testDeleteItem() {
        StoreDAO.putItem(res1);
        StoreDAO.putItem(res2);
        StoreDAO.putItem(res3);
        assertNotSame(res1, StoreDAO.getItem("keyword1"));
        assertNotSame(res2, StoreDAO.getItem("keyword2"));
        assertNotSame(res3, StoreDAO.getItem("keyword3"));
        assertTrue(StoreDAO.getItem("keyword1") instanceof Item);
        assertTrue(StoreDAO.getItem("keyword2") instanceof Item);
        assertTrue(StoreDAO.getItem("keyword3") instanceof Item);
        assertEquals(res1, StoreDAO.getItem("keyword1"));
        assertEquals(res2, StoreDAO.getItem("keyword2"));
        assertEquals(res3, StoreDAO.getItem("keyword3"));
        assertNotNull(StoreDAO.getItem("keyword1"));
        assertNotNull(StoreDAO.getItem("keyword2"));
        assertNotNull(StoreDAO.getItem("keyword3"));
        StoreDAO.deleteItem("keyword1");
        assertNull(StoreDAO.getItem("keyword1"));
        StoreDAO.deleteItem("keyword2");
        assertNull(StoreDAO.getItem("keyword2"));
        StoreDAO.deleteItem("keyword3");
        assertNull(StoreDAO.getItem("keyword3"));
    }

    //--------------------------
    //     private helpers
    //--------------------------
    
    private void initStore() {
        StoreDAO.setStorePath(TEST_STORE_PATH);
        StoreDAO.setup();
    }
    
    private void initTestData() {
        res1 = new Item("keyword1", "content1");
        res2 = new Item("keyword2", "content2");
        res3 = new Item("keyword3", "content3");
    }
    
    private void closeStore() {
        StoreDAO.clearStore();
        StoreDAO.shutdown();
    }

}
