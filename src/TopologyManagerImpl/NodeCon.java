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
    private String dstNodeId;
    private String from, to;
    
    @Override
    public void setConnection(String nodeId, String from, String to) {
        dstNodeId = nodeId;
        this.from = from;
        this.to = to;
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
