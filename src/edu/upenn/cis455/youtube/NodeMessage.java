package edu.upenn.cis455.youtube;

import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.NodeHandle;

/**
 * Class for messages communicated between nodes.
 * @author Zhishen Wen
 * @version Apr 1, 2013
 */
@SuppressWarnings("serial")
public class NodeMessage implements Message {
    /* members */
    NodeHandle from;
    String content;
    boolean wantResponse = true;
    boolean pingPongSrv = false;
        
        /** constructor for NodeMessage */
        public NodeMessage(NodeHandle from, String content) {
            this.from = from;
            this.content = content;
        }
        
        //---------------------------------
        //      getters and setters
        //---------------------------------
        
        public void setWantResponse(boolean b) {
            wantResponse = b;
        }
        
        public void setPingPongSrv(boolean b) {
            pingPongSrv = b;
        }
        
        public boolean isPingPong() {
            return pingPongSrv;
        }
        
        public boolean isWantResponse() {
            return wantResponse;
        }

        public NodeHandle getFrom() {
            return from;
        }

        public void setFrom(NodeHandle from) {
            this.from = from;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
        
        //-------------------
        // required method
        //-------------------
        
        @Override
        public int getPriority() { 
            return Message.LOW_PRIORITY; 
        }
        
        //--------------------------
        // helper for JUnit tests
        //--------------------------
        
        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (!(o instanceof NodeMessage)) return false;
            NodeMessage that = (NodeMessage) o;
            return that.from == this.from &&
                    that.content.equals(this.content) &&
                    that.wantResponse == this.wantResponse &&
                    that.pingPongSrv == this.pingPongSrv;
        }

}
