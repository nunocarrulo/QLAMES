/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ITopologyManager;

import TopologyManagerImpl.Port;

/**
 *
 * @author nuno
 */
public interface IPort {
    /**
     * 
     * @param portId Unique port identifier
     */
    public void setPort(String portId);
    /**
     * 
     * @param portId Unique port identifier
     * @param bwCap Maximum capacity of link (kbps) 
     */
    public void setPort(String portId, long bwCap);
    
    /**
     * 
     * @param portId Unique port identifier
     * @param bwCap Maximum capacity of link (kbps) 
     * @param currLoad Current bandwidth reserved on port
     */
    public void setPort(String portId, long bwCap, long currLoad);
    /**
     * 
     * @param currLoad Current bandwidth reserved on port
     */
    public void updateCurrLoad(long currLoad);
    /**
     * 
     * @return Returns current bandwidth reserved on port
     */
    public long getCurrLoad();

}
