/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TopologyManagerImpl;

import ITopologyManager.IPort;

/**
 *
 * @author nuno
 */
public class Port implements IPort{

    private String portID;
    private long bwCap;
    private long currBwLoad;
    
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

    /* Getters */
    public String getPortID() {
        return portID;
    }

    public long getBwCap() {
        return bwCap;
    }

    public long getCurrBwLoad() {
        return currBwLoad;
    }
}
