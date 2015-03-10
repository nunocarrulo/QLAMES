/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ITopologyManager;

import TopologyManagerImpl.NodeCon;
import TopologyManagerImpl.Port;
import java.util.List;

/**
 *
 * @author nuno
 */
public interface ITopoNode {
    /**
     * 
     * @param id Unique identifier of the node 
     * @param port List of the switch's ports
     */
    public void setNode(String id, List<Port> port);
    /**
     * 
     * @param nodeCons List of nodes which are connected to me 
     */
    public void setNodeCons(List<NodeCon> nodeCons);
    /**
     * 
     * @param nodeCon List of nodes which are connected to me 
     */
    public void addNodeCon(NodeCon nodeCon);
    /**
     * If node is a host save IP and MAC address
     * @param ip IP address of host
     * @param mac MAC address of host
     */
    public void setIPAndMac(String ip, String mac);
    
}