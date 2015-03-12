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
    private String qosuuid;
    private String queueuuid;
    
    public QosConfig(){
        
    }
    
    public QosConfig(String ovsid){
        this.ovsid = ovsid;
    }
    public QosConfig(String ovsid, String qosuuid){
        this.ovsid = ovsid;
        this.qosuuid = qosuuid;
    }
    public QosConfig(String ovsid, String qosuuid, String queueuuid){
        this.ovsid = ovsid;
        this.qosuuid = qosuuid;
        this.queueuuid = queueuuid;
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
}
