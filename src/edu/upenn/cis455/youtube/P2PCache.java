package edu.upenn.cis455.youtube;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import rice.p2p.commonapi.Id;

/**
 * Main class for the P2P caching system.
 * @author Zhishen Wen
 * @version Apr 1, 2013
 */
public class P2PCache {
    /** static members */
    private static NodeFactory factory;
    private static boolean isTerminated = false;
    
    /** main method to run a Pastry node */
    public static void main(String[] args) throws Exception  {
        // wrong arg number
        if (args.length != 5 && args.length != 6) {
            msg("Wrong number of command line args.");
            usage();
            return;
        }
        
        // read command line args
        int localPort = 9001;
        String bootIp = "192.168.164.133";
        int bootPort = 9001;
        int daemonPort = 9000;
        String dbPath = "/home/cis455/export/berkeleydb";
        boolean pingPong = true;
        try {
            localPort = Integer.parseInt(args[0]);
            bootIp = args[1];
            bootPort = Integer.parseInt(args[2]);
            daemonPort = Integer.parseInt(args[3]);
            dbPath = args[4];
            if (args.length == 6)
                pingPong = args[5].equals("0") ? false : true;
        }
        catch (NumberFormatException e) {
            msg("Command line arg(s) format error. Port number expected an integer.");
            return;
        }
        
        // print author message
        msg("Jason Wen's FreePastry P2P Cache (ver 1.0.0)");
        msg("====== Author: Zhishen Wen (wzhishen) ======");
        P2PCache p2pc = new P2PCache();
        CacheServer cs = null;
        try {
            cs = join(localPort, bootIp, bootPort, dbPath);
            cs.setHost(bootIp);
            
            // create or join a ring successfully
            msg("Join to Pastry ring successfully (Bootstrap "+bootIp+":"+bootPort+").");
            
            // start a daemon thread listening on the daemon port
            Thread t = new Thread(p2pc.new DaemonThread(cs, daemonPort));
            t.setDaemon(true); 
            t.start();
            
            // ping-pong service
            if (pingPong) ping(cs);
        }
        catch (IllegalStateException e) {
            msg("Cannot bind to port "+localPort+". Already in use. Please retry.");
            return;
        }
        catch (RuntimeException e) {
            msg("Cannot join to ring (Bootstrap "+bootIp+":"+bootPort+"). Please retry.");
            return;
        }
        catch (Exception e) {
            msg("Exception occurred during ring formation. Please retry.");
            return;
        }
        
    }
    
    /** inner class for the daemon thread */
    class DaemonThread implements Runnable {
        /* members */
        private CacheServer cs;
        private int daemonPort;
        
        /** constructor for DaemonThread */
        public DaemonThread(CacheServer cs, int daemonPort) {
            this.cs = cs;
            this.daemonPort = daemonPort;
        }

        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(daemonPort);
                Socket socket = null;
                while (!isTerminated) {
                    socket = serverSocket.accept();
                    PrintWriter outToServlet = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader inFromServlet = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while (!(inFromServlet.readLine()).trim().isEmpty());
                    String inMsg = getKeyword(inFromServlet.readLine());
                    Id id = factory.getIdFromString(inMsg);
                    cs.sendMessage(id, inMsg);
                    while (cs.getResponse() == null)
                        Thread.sleep(1000);
                    outToServlet.println(cs.getResponse());
                    cs.setResponse(null);
                    outToServlet.close();
                    inFromServlet.close();
                }
                socket.close();
                serverSocket.close();
            }
            catch (BindException e) {
                msg("Daemon port is already listened on (port "+daemonPort+")");
            }
            catch (Exception e) {
                msg("Exception occurred during communication between servlet front end and Pastry ring.");
            }
        }
    }
    
    /** terminates the socket on which the daemon listens */
    public static void setTerminated(boolean b) {
        isTerminated = b;
    }
    
    //---------------------------
    //     private helpers
    //---------------------------
    
    /** sends a PING message to a randomly chosen node */
    private static void ping(CacheServer cs) throws InterruptedException {
        while (true) {
            Thread.sleep(3000);
            Id id = factory.nidFactory.generateNodeId();
            msg("Sending PING to "+id);
            cs.sendMessage(id, "PING", true);
        }
    }
    
    /** joins this node to the Pastry ring; otherwise creates a new ring if necessary */
    private static CacheServer join(int localPort, String bootIp, 
        int bootPort, String dbPath) throws UnknownHostException {
        InetAddress hostIp = InetAddress.getByName(bootIp);
        InetSocketAddress host = new InetSocketAddress(hostIp, bootPort);
        factory = new NodeFactory(localPort, host);
        return new CacheServer(factory, dbPath);
    }
    
    /** gets the keyword from the request sent by the servlet */
    private static String getKeyword(String msg) {
        int begInd = msg.indexOf("<m:Keyword>") + "<m:Keyword>".length();
        int endInd = msg.indexOf("</m:Keyword>");
        return begInd == -1 || endInd == -1 ?
                "" : msg.substring(begInd, endInd);
    }
    
    /** prints the usage */
    private static void usage() {
        msg("        Usage : P2PCache <localPort> <bootAddress> <bootPort>");
        msg("                         <daemonPort> <DBPath> [<pingPong>]");
        msg("  <localPort> : the port number on local machine to which the");
        msg("                Pastry node should bind.");
        msg("<bootAddress> : the IP address of the Pastry bootstrap node.");
        msg("   <bootPort> : the port number of the Pastry bootstrap node.");
        msg(" <daemonPort> : the port number to which the daemon should bind.");
        msg("     <DBPath> : the path to the BerkeleyDB database.");
        msg("   <pingPong> : (optional) enter 0 to disable ping-pong service;");
        msg("                any other character(s) to enable it. Ping-pong");
        msg("                service is enabled by default.");
    }
    
    /** prints a message */
    private static void msg(String s) {
        System.out.println(s);
    }

}
