/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ITopologyManager;

import TopologyManagerImpl.TopoNode;
import java.util.List;

/**
 *
 * @author nuno
 */
public interface ITopology {
    /**
     * 
     * @param nodeID unique identifier of the node
     * @return Returns the node data structure
     */
    public TopoNode getNode(String nodeID);
    
    /**
     * 
     * @param nodeName unique simpler identifier of node
     * @return Returns the node data structure
     */
    public TopoNode getNodeByName(String nodeName);
    
    /**
     * 
     * @param node TopoNode data structure
     */
    public void addNode(TopoNode node);
    
    /**
     * 
     * @return Returns a list of all nodes of the topology
     */
    public List<TopoNode> getAllNodes();
    
    /**
     * 
     * @return Returns a list of all switches of the topology
     */
    public List<TopoNode> getAllSwitches();
    
    /**
     * 
     * @param nodeid unique identifier of the node
     * @return <li>true, node with id "nodeid" exists in topology </li>
     *         <li>false, node with id "nodeid" does exist in topology</li> 
     */
    public boolean nodeExists(String nodeid);
    
    /**
     * Prints the ids of all the nodes on the topology
     */
    public void printNodesIds();
    
    /**
     * Print the all node data
     */
    public void printNodes();
    
    /**
     * Deletes all nodes belonging to the topology
     */
    public void clearTopo();
    
    /**
     * 
     * @param ip IP address of the node to be searched on the topology
     * @return Returns the node ID of the node with the given IP
     */
    public String getHostByIP(String ip);
}
