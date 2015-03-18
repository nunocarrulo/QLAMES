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
    private int maxRate;
    private int minRate;
    private int priority;

    public Queue(String uuid) {
        this.uuid = uuid;
    }

    public Queue(int minRate, int priority) {
        this.minRate = minRate;
        this.priority = priority;
    }

    public Queue(int minRate, int maxRate, int priority) {
        this.maxRate = maxRate;
        this.minRate = minRate;
        this.priority = priority;
    }
    
    public Queue(String qUUID, int minRate, int maxRate, int priority) {
        this.uuid = qUUID;
        this.maxRate = maxRate;
        this.minRate = minRate;
        this.priority = priority;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setMaxRate(int maxRate) {
        this.maxRate = maxRate;
    }

    public void setMinRate(int minRate) {
        this.minRate = minRate;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getUuid() {
        return uuid;
    }

    public int getMaxRate() {
        return maxRate;
    }

    public int getMinRate() {
        return minRate;
    }

    public int getPriority() {
        return priority;
    }
    
}
