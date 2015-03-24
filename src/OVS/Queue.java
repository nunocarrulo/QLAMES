/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OVS;

import REST_Requests.Constants;

/**
 *
 * @author nuno
 */
public class Queue {

    private int profile;
    private int number;
    private String uuid;
    private int maxRate;
    private int minRate;
    private int priority;

    public Queue(int profile, String uuid, int linkSpeed) {
        this.profile = profile;
        this.uuid = uuid;
        setProfileParams(linkSpeed);
    }

    public Queue(int profile, int number, String uuid, int linkSpeed) {
        this.profile = profile;
        this.number = number;
        this.uuid = uuid;
        setProfileParams(linkSpeed);
    }

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

    public Queue(int number, String uuid, int minRate, int maxRate, int priority) {
        this.number = number;
        this.uuid = uuid;
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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getProfile() {
        return profile;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    private void setProfileParams(int linkSpeed) {
        switch (profile) {
            case Constants.Silver:
                minRate = (int) 0.1 * linkSpeed;
                maxRate = (int) 0.4 * linkSpeed;
                priority = 5;
                break;
            case Constants.Gold:
                minRate = (int) 0.4 * linkSpeed;
                maxRate = (int) 0.7 * linkSpeed;
                priority = 3;
                break;
            case Constants.Platinum:
                minRate = (int) 0.7 * linkSpeed;
                maxRate = (int) linkSpeed;
                priority = 1;
                break;
            default: 
                System.out.println("Unknown profile type!Nothing will be done.");
                break;
        }
    }

    @Override
    public String toString() {
        return ("QueueUUID: " + uuid + " number: " + number + " minRate: " + minRate + " maxRate: " + maxRate + " priority " + priority);
    }

}
