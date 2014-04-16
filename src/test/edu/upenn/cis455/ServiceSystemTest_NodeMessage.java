package test.edu.upenn.cis455;

import edu.upenn.cis455.youtube.NodeMessage;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.NodeHandle;
import junit.framework.TestCase;

/**
 * JUnit tests for NodeMessage
 * @author Zhishen Wen
 * @version Apr 2, 2013
 */
public class ServiceSystemTest_NodeMessage extends TestCase {
    NodeMessage res;
    NodeMessage exp;
    NodeHandle from = null;

    protected void setUp() throws Exception {
        res = new NodeMessage(from, "content");
        exp = new NodeMessage(from, "content");
    }

    public final void testNodeMessageConstructor() {
        assertNotNull(res);
        assertNotNull(exp);
        assertTrue(res instanceof NodeMessage);
        assertTrue(exp instanceof NodeMessage);
        assertNotSame(exp, res);
        assertEquals(exp, res);
    }

    public final void testSetWantResponse() {
        assertNotNull(exp.isWantResponse());
        assertNotNull(res.isWantResponse());
        assertTrue(exp.isWantResponse());
        assertTrue(res.isWantResponse());
        assertEquals(exp.isWantResponse(), res.isWantResponse());
        res.setWantResponse(false);
        assertNotNull(res.isWantResponse());
        assertFalse(res.isWantResponse());
        assertFalse(exp.isWantResponse() == res.isWantResponse());
        exp.setWantResponse(false);
        assertNotNull(exp.isWantResponse());
        assertFalse(exp.isWantResponse());
        assertTrue(exp.isWantResponse() == res.isWantResponse());
    }

    public final void testSetPingPongSrv() {
        assertNotNull(exp.isPingPong());
        assertNotNull(res.isPingPong());
        assertFalse(exp.isPingPong());
        assertFalse(res.isPingPong());
        assertEquals(exp.isPingPong(), res.isPingPong());
        res.setPingPongSrv(true);
        assertNotNull(res.isPingPong());
        assertTrue(res.isPingPong());
        assertFalse(exp.isPingPong() == res.isPingPong());
        exp.setPingPongSrv(true);
        assertNotNull(exp.isPingPong());
        assertTrue(exp.isPingPong());
        assertTrue(exp.isPingPong() == res.isPingPong());
    }

    public final void testIsPingPong() {
        assertNotNull(exp.isPingPong());
        assertNotNull(res.isPingPong());
        assertFalse(exp.isPingPong());
        assertFalse(res.isPingPong());
        assertEquals(exp.isPingPong(), res.isPingPong());
    }

    public final void testIsWantResponse() {
        assertNotNull(exp.isWantResponse());
        assertNotNull(res.isWantResponse());
        assertTrue(exp.isWantResponse());
        assertTrue(res.isWantResponse());
        assertEquals(exp.isWantResponse(), res.isWantResponse());
    }

    public final void testGetFrom() {
        assertNull(exp.getFrom());
        assertNull(res.getFrom());
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

    public final void testGetPriority() {
        assertNotNull(res.getPriority());
        assertNotNull(exp.getPriority());
        assertEquals(exp.getPriority(), Message.LOW_PRIORITY);
        assertEquals(exp.getPriority(), Message.LOW_PRIORITY);
        assertEquals(exp.getPriority(), res.getPriority());
    }

}
