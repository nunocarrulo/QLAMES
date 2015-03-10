/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CIMI_Main;

/**
 *
 * @author nuno
 */
public class Constants {
    /* GET TYPES XML */
    public static final int topo = 1;
    public static final int node = 2;
    public static final int table = 3;
    public static final int flow = 4;
    
    /* Ethernet types */
    public static final int ipv4 = 2048;
    public static final int arp = 2054;
    public static final int ipv6 = 34525;
    public static final int lldp = 35020;

    /* OF13 Logical Ports */
    public static enum OFLogicalPorts{
        ALL, CONTROLLER, TABLE, IN_PORT, ANY, LOCAL, NORMAL, FLOOD
    } 
    
}
