/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TopologyManagerImpl;

import ITopologyManager.INodeCon;

/**
 *
 * @author nuno
 */
public class NodeCon implements INodeCon{
    
    /**
     * nodeID of the destination node
     * @serialField dstNodeId
     */
    private String dstNodeId;
    /**
     * PortID of the source node
     * @serialField from
     */
    private String from;
    
    /**
     * PortID of the destination node
     * @serialField to
     */
    private String to;

    public NodeCon() {
    }

    public NodeCon(String dstNodeId, String from, String to) {
        this.dstNodeId = dstNodeId;
        this.from = from;
        this.to = to;
    }
    
    @Override
    public void setConnection(String nodeId, String from, String to) {
        dstNodeId = nodeId;
        this.from = from;
        this.to = to;
    }
    
    @Override
    public String toString(){
        return ("Target "+dstNodeId+" from "+from+" to "+to);
    }
    
    /* Getters */
    public String getDstNodeId() {
        return dstNodeId;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
    
}
