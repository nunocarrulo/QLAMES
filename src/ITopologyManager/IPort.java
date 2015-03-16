/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ITopologyManager;

import OVS.Queue;

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
    /**
     * 
     * @param portUUID Unique ovsdb port identifier
     */
    public void setPortUUID(String portUUID);
    /**
     * 
     * @return ovsdb port uuid
     */
    public String getPortUUID();
    
    /**
     * Attaches a queue to the port (and to the QoS row)
     * @param q Queue to be added to the port 
     * @return  <li>true, Queue added successfully</li>
     *          <li>false, Queue already exists and thus was not added</li>
     */
    public boolean addQueue(Queue q);
    
    /**
     * Removes the queue attached to the port (if exists)
     * @param qUUID Unique identifier of the queue to be deleted from that port.
     * @return  <li>true, Queue deleted successfully</li>
     *          <li>false, Queue not found</li>
     */
    public boolean delQueue(String qUUID);
    /**
     * Attaches a QoS row to the port.
     * @param qos Unique identifier of the QoS row to be added
     * @return  <li>true, QoS row added successfully</li>
     *          <li>false, QoS row already exists and thus was not added</li>
     */
    //public boolean addQos(String qos);
    /**
     * Removes the queue attached to the port (if exists)
     */
    //public void delQos();
}
