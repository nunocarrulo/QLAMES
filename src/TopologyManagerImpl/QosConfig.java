/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TopologyManagerImpl;

/**
 *
 * @author nuno
 */
public class QosConfig {

    private String ovsid;
    private String portuuid;
    private String qosuuid;
    private String queueuuid;
    private int maxRateQos;//IN MBPS;
    private int minRateQ;//IN MBPS
    private int maxRateQ;//IN MBPS
    private int priorityQ;

    public QosConfig() {
        minRateQ = 0;
        maxRateQos = maxRateQ = 10000;
    }

    public QosConfig(String ovsid) {
        this.ovsid = ovsid;
        minRateQ = 0;
        maxRateQos = maxRateQ = 10000;
    }

    public QosConfig(String ovsid, String qosuuid) {
        this.ovsid = ovsid;
        this.qosuuid = qosuuid;
        minRateQ = 0;
        maxRateQos = maxRateQ = 10000;
    }

    public QosConfig(String ovsid, String qosuuid, String queueuuid) {
        this.ovsid = ovsid;
        this.qosuuid = qosuuid;
        this.queueuuid = queueuuid;
        minRateQ = 0;
        maxRateQos = maxRateQ = 10000;
    }

    public void clear(){
        priorityQ = minRateQ = 0;
        maxRateQos = maxRateQ = 10000;
        portuuid = qosuuid = queueuuid = "";
    }
    
    public void setQosConfig(String portUUID, int maxR){
        this.portuuid = portUUID;
        this.maxRateQ = maxR;
    }
    
    public void setQosConfig(String qosUUID, int priority, int minR, int maxR){
        this.qosuuid = qosUUID;
        this.priorityQ = priority;
        this.minRateQ = minR;
        this.maxRateQ = maxR;
    }
    public void setQQosConfig(String qUUID, int priority, int minR, int maxR){
        this.queueuuid = qUUID;
        this.priorityQ = priority;
        this.minRateQ = minR;
        this.maxRateQ = maxR;
    }
    
    public String getOvsid() {
        return ovsid;
    }

    public void setOvsid(String ovsid) {
        this.ovsid = ovsid;
    }

    public String getQosuuid() {
        return qosuuid;
    }

    public void setQosuuid(String qosuuid) {
        this.qosuuid = qosuuid;
    }

    public String getQueueuuid() {
        return queueuuid;
    }

    public void setQueueuuid(String queueuuid) {
        this.queueuuid = queueuuid;
    }

    public String getPortuuid() {
        return portuuid;
    }

    public void setPortuuid(String portuuid) {
        this.portuuid = portuuid;
    }

    public int getMaxRateQos() {
        return maxRateQos;
    }

    public void setMaxRateQos(int maxRateQos) {
        this.maxRateQos = maxRateQos;
    }

    public int getMinRateQ() {
        return minRateQ;
    }

    public void setMinRateQ(int minRateQ) {
        this.minRateQ = minRateQ;
    }

    public int getMaxRateQ() {
        return maxRateQ;
    }

    public void setMaxRateQ(int maxRateQ) {
        this.maxRateQ = maxRateQ;
    }

    public int getPriorityQ() {
        return priorityQ;
    }

    public void setPriorityQ(int priorityQ) {
        this.priorityQ = priorityQ;
    }

}
