/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ITopologyManager;

import TopologyManagerImpl.NodeCon;

/**
 *
 * @author nuno
 */
public interface INodeCon {
    /**
     * 
     * @param nodeId Unique source node identifier
     * @param from  Unique port id of the source switch
     * @param to    Unique port id of the dest switch which is connected to source switch
     * @return <li>true, NodeCon created successfully</li>
     *         <li>false, NodeCon not created due to error</li>
     */
    public void setConnection(String nodeId, String from, String to);

}
