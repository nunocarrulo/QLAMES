/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TopologyManagerImpl;

import ITopologyManager.IPort;
import OVS.Queue;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nuno
 */
public class Port implements IPort {

    private String portID;
    private String portUUID;
    private String qosUUID;
    private List<Queue> queues;
    private long bwCap;
    private long currBwLoad;
    private int numberCounter = 0;

    public Port() {
        queues = new ArrayList<>();
    }

    @Override
    public boolean addQueue(Queue q) {
        if (!queues.contains(q)) {
            queues.add(q);
            return true;
        } else {
            System.out.println("Queue already exists on this port.");
            return false;
        }

    }

    @Override
    public boolean delQueue(String qUUID) {

        for (Queue q : queues) {
            if (q.getUuid().equals(qUUID)) {
                queues.remove(q);
                System.out.println("Queue: " + qUUID + " removed successfully");
                return true;
            }
        }
        System.out.println("Queue: " + qUUID + " not found in Port: " + portID);
        return false;

    }

    public int getNextNumberCounter() {
        numberCounter++;
        return numberCounter;
    }

    @Override
    public String toString() {
        return ("PortID: " + portID + " PortUUID: " + portUUID + " QosUUID: " + qosUUID);
    }

    @Override
    public String getPortUUID() {
        return portUUID;
    }

    @Override
    public void setPortUUID(String portUUID) {
        this.portUUID = portUUID;
    }

    @Override
    public void setPort(String portid) {
        this.portID = portid;
    }

    @Override
    public void setPort(String portid, long bwCap) {
        this.portID = portid;
        this.bwCap = bwCap;
    }

    @Override
    public void setPort(String portid, long bwCap, long currLoad) {
        this.portID = portid;
        this.bwCap = bwCap;
        this.currBwLoad = currLoad;
    }

    @Override
    public void updateCurrLoad(long currLoad) {
        currBwLoad = currLoad;
    }

    @Override
    public long getCurrLoad() {
        return currBwLoad;
    }

    /* Setters and Getters */
    public String getPortID() {
        return portID;
    }

    public long getBwCap() {
        return bwCap;
    }

    public String getQosUUID() {
        return qosUUID;
    }

    public void setQosUUID(String QosUUID) {
        this.qosUUID = QosUUID;
    }

    public long getCurrBwLoad() {
        return currBwLoad;
    }

    public int getNumberCounter() {
        return numberCounter;
    }

    public void setNumberCounter(int numberCounter) {
        this.numberCounter = numberCounter;
    }

    public List<Queue> getQueues() {
        return queues;
    }

}
