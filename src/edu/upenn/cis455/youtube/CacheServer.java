package edu.upenn.cis455.youtube;

import rice.p2p.commonapi.*;

/**
 * Class for the cache Pastry application.
 * @author Zhishen Wen
 * @version Apr 1, 2013
 */
public class CacheServer implements Application {
    /* members */
    NodeFactory nodeFactory;
    Node node;
    Endpoint endpoint;
    YouTubeClient clnt;
    
    String response = null;
    
    /** Constructor for CacheServer */
    public CacheServer(NodeFactory nodeFactory, String dbPath) {
        this.nodeFactory = nodeFactory;
        this.node = nodeFactory.getNode();
        this.endpoint = node.buildEndpoint(this, "Cache Server");
        this.endpoint.register();
        this.clnt = new YouTubeClient(dbPath);
    }
    
    /** sends a message via node ID */
    public void sendMessage(Id idToSendTo, String msgToSend) {
        sendMessageFull(idToSendTo, null, msgToSend, false);
    }
    
    /** sends a message via node ID with a flag for ping-pong service */
    public void sendMessage(Id idToSendTo, String msgToSend, boolean pingPong) {
        sendMessageFull(idToSendTo, null, msgToSend, pingPong);
    }
    
    /** sends a message via node handle */
    public void sendMessageDirect(NodeHandle handle, String msgToSend) {
        sendMessageFull(null, handle, msgToSend, false);
    }
    
    /** gets the response */
    public String getResponse() {
        return response;
    }
    
    /** sets the response*/
    public void setResponse(String rsp) {
        response = rsp;
    }
    
    /** delivers a message to other nodes in the DHT */
    public void deliver(Id id, Message message) {
        NodeMessage msg = (NodeMessage) message;
        // ping-pong service
        if (msg.isPingPong() && msg.getContent().equals("PING")) {
            System.out.println("Received PING to ID "+id+" from node "+msg.getFrom().getId()+"; returning PONG");
            NodeMessage reply = new NodeMessage(node.getLocalNodeHandle(), "PONG");
            reply.setPingPongSrv(true);
            endpoint.route(null, reply, msg.getFrom());
            return;
        }
        else if (msg.isPingPong() && msg.getContent().equals("PONG")) {
            System.out.println("Received PONG from node "+msg.getFrom().getId());
            return;
        }
        
        if (!msg.isPingPong() && !msg.isWantResponse()) {
            // get a RESULT message
            response = msg.getContent();
        }
            
        if (!msg.isPingPong() && msg.isWantResponse()) {
            // generate a RESULT message
            String result = clnt.searchVideos(msg.getContent());
            NodeMessage reply = new NodeMessage(node.getLocalNodeHandle(), result);
            reply.setWantResponse(false);
            endpoint.route(null, reply, msg.getFrom());
        }
    }
    
    /** sets host address */
    public void setHost(String host) {
        clnt.setHost(host);
    }
    
    //---------------------------
    //     private helper
    //---------------------------
    
    /** sends a message via node ID or handle with a ping-pong flag */
    private void sendMessageFull(Id idToSendTo, NodeHandle handle, 
            String msgToSend, boolean pingPong) {
        NodeMessage msg = new NodeMessage(node.getLocalNodeHandle(), msgToSend);
        msg.setPingPongSrv(pingPong);
        endpoint.route(idToSendTo, msg, handle);
    }
    
    //----------------------------
    //  methods not implemented
    //----------------------------
    
    public void update(NodeHandle handle, boolean joined) { }
    
    public boolean forward(RouteMessage routeMessage) { return true; }

}
