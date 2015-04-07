/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TopologyManagerImpl;

import ITopologyManager.IPort;
import OVS.CircularArray;
import OVS.IfaceStatistics;
import OVS.Queue;
import static REST_Requests.Constants.UP;
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
    private String ifaceUUID;
    private final List<Queue> queues;
    private IfaceStatistics iFaceStats;
    private final CircularArray currBWHistory;
    private int bwCap;
    private int linkSpeed; //kbps
    private int currBwLoad; //kbps
    private int numberCounter = 0;
    private boolean state;  //true means UP
    private boolean hostConnection;

    public Port() {
        hostConnection = false;
        queues = new ArrayList<>();
        iFaceStats = new IfaceStatistics();
        state = UP;
        currBWHistory = new CircularArray(60); //10min = 60*10
        linkSpeed = 100000;
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
    public void setPort(String portid, int bwCap) {
        this.portID = portid;
        this.bwCap = bwCap;
    }

    @Override
    public void setPort(String portid, int bwCap, int currLoad) {
        this.portID = portid;
        this.bwCap = bwCap;
        this.currBwLoad = currLoad;
    }

    @Override
    public void updateCurrLoad(int currLoad) {
        currBWHistory.add(currLoad);    //save value to array
        currBwLoad = currLoad;
    }

    @Override
    public int getCurrLoad() {
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

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getIfaceUUID() {
        return ifaceUUID;
    }

    public void setIfaceUUID(String ifaceUUID) {
        this.ifaceUUID = ifaceUUID;
    }

    public IfaceStatistics getiFaceStats() {
        return iFaceStats;
    }

    public void setiFaceStats(IfaceStatistics iFaceStats) {
        this.iFaceStats = iFaceStats;
    }

    public CircularArray getCurrBWHistory() {
        return currBWHistory;
    }

    public int getLinkSpeed() {
        return linkSpeed;
    }

    public void setLinkSpeed(int linkSpeed) {
        this.linkSpeed = linkSpeed;
    }

    public boolean isHostConnection() {
        return hostConnection;
    }

    public void setHostConnection(boolean hostConnection) {
        this.hostConnection = hostConnection;
    }

}
