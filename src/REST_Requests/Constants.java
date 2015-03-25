/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package REST_Requests;

/**
 *
 * @author nuno
 */
public class Constants {
    
    public static final Object lol = new Object();
    public static final double LINK_THRESHOLD = 0.8;    //80%
    public static final boolean applyLoadBalance = false;
    
    /* HTTP ERROR CODES */
    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int NO_CONTENT = 204;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int BAD_GATEWAY = 502;
    public static final int SERVICE_UNAVAILABLE = 503;
    
    public static String ovsID;
    
    /* Port State */
    public static final boolean UP = true;
    public static final boolean DOWN = false;
    
    /* Profiles */
    public static final int Silver = 1;
    public static final int Gold = 2;
    public static final int Platinum = 3;
    
    /* GET TYPES XML */
    public static final int topo = 1;
    public static final int table = 2;
    public static final int flow = 3;

    /* GET TYPES JSON */
    public static final int queue = 1;
    public static final int qos = 2;
    public static final int port = 3;
    public static final int bridge = 4;
    public static final int node = 5;
    public static final int iface = 6;
    
    /* Ethernet types */
    public static final String ipv4 = "2048";
    public static final String arp = "2054";
    public static final String ipv6 = "34525";
    public static final String lldp = "35020";

    /* OF13 Logical Ports */
    public static enum OFLogicalPorts {
        ALL, CONTROLLER, TABLE, IN_PORT, ANY, LOCAL, NORMAL, FLOOD
    }

}
