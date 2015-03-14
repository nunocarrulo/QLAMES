/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OVS;

/**
 *
 * @author nuno
 */
public class Queue {
    private String uuid;
    private long maxRate;
    private long minRate;
    private int priority;

    public Queue(String uuid) {
        this.uuid = uuid;
    }

    public Queue(long minRate, int priority) {
        this.minRate = minRate;
        this.priority = priority;
    }

    public Queue(long maxRate, long minRate, int priority) {
        this.maxRate = maxRate;
        this.minRate = minRate;
        this.priority = priority;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setMaxRate(long maxRate) {
        this.maxRate = maxRate;
    }

    public void setMinRate(long minRate) {
        this.minRate = minRate;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getUuid() {
        return uuid;
    }

    public long getMaxRate() {
        return maxRate;
    }

    public long getMinRate() {
        return minRate;
    }

    public int getPriority() {
        return priority;
    }
    
}
