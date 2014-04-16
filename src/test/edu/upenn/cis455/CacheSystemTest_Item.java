package test.edu.upenn.cis455;

import edu.upenn.cis455.youtube.Item;
import junit.framework.TestCase;

/**
 * JUnit tests for Item
 * @author Zhishen Wen
 * @version Apr 2, 2013
 */
public class CacheSystemTest_Item extends TestCase {
    Item res;
    Item exp;

    protected void setUp() throws Exception {
        res = new Item("keyword", "content");
        exp = new Item("keyword", "content");
    }

    public final void testItemConstructor() {
        assertNotNull(res);
        assertNotNull(exp);
        assertTrue(res instanceof Item);
        assertTrue(exp instanceof Item);
        assertNotSame(exp, res);
        assertEquals(exp, res);
    }

    public final void testItemDefaultConstructor() {
        res = new Item();
        exp = new Item();
        assertNotNull(res);
        assertNotNull(exp);
        assertTrue(res instanceof Item);
        assertTrue(exp instanceof Item);
        assertNotSame(exp, res);
        assertEquals(exp, res);
    }

    public final void testGetKeyword() {
        assertNotNull(exp.getKeyword());
        assertNotNull(res.getKeyword());
        assertTrue(exp.getKeyword() instanceof String);
        assertTrue(res.getKeyword() instanceof String);
        assertEquals(exp.getKeyword(), "keyword");
        assertEquals(res.getKeyword(), "keyword");
        assertEquals(exp.getKeyword(), res.getKeyword());
    }

    public final void testSetKeyword() {
        assertNotNull(exp.getKeyword());
        assertNotNull(res.getKeyword());
        assertTrue(exp.getKeyword() instanceof String);
        assertTrue(res.getKeyword() instanceof String);
        assertEquals(exp.getKeyword(), "keyword");
        assertEquals(res.getKeyword(), "keyword");
        assertEquals(exp.getKeyword(), res.getKeyword());
        res.setKeyword("kw");
        assertNotNull(res.getKeyword());
        assertTrue(res.getKeyword() instanceof String);
        assertEquals(res.getKeyword(), "kw");
        assertFalse(exp.getKeyword().equals(res.getKeyword()));
        exp.setKeyword("kw");
        assertNotNull(exp.getKeyword());
        assertTrue(exp.getKeyword() instanceof String);
        assertEquals(exp.getKeyword(), "kw");
        assertEquals(exp.getKeyword(), res.getKeyword());
    }

    public final void testGetContent() {
        assertNotNull(exp.getContent());
        assertNotNull(res.getContent());
        assertTrue(exp.getContent() instanceof String);
        assertTrue(res.getContent() instanceof String);
        assertEquals(exp.getContent(), "content");
        assertEquals(res.getContent(), "content");
        assertEquals(exp.getContent(), res.getContent());
    }

    public final void testSetContent() {
        assertNotNull(exp.getContent());
        assertNotNull(res.getContent());
        assertTrue(exp.getContent() instanceof String);
        assertTrue(res.getContent() instanceof String);
        assertEquals(exp.getContent(), "content");
        assertEquals(res.getContent(), "content");
        assertEquals(exp.getContent(), res.getContent());
        res.setContent("new");
        assertNotNull(res.getContent());
        assertTrue(res.getContent() instanceof String);
        assertEquals(res.getContent(), "new");
        assertFalse(exp.getContent().equals(res.getContent()));
        exp.setContent("new");
        assertNotNull(exp.getContent());
        assertTrue(exp.getContent() instanceof String);
        assertEquals(exp.getContent(), "new");
        assertEquals(exp.getContent(), res.getContent());
    }

}
