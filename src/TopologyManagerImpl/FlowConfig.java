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
public class FlowConfig {

    private String nodeID;
    private String flowName;
    private int flowPrio;
    private int flowID;
    private int tableID;
    private int hardTimeout;
    private int idleTimeout;
    private String etherType;
    private String outputPort;  //parse it
    private String qID;
    private String srcIP;
    private String dstIP;

    public FlowConfig() {

    }

    public FlowConfig(String nodeid, int tableID, int flowID, int flowPrio, String srcIP, String dstIP, String outputPort) {
        this.nodeID = nodeid;
        this.tableID = tableID;
        this.flowID = flowID;
        this.flowPrio = flowPrio;
        this.srcIP = srcIP + "/32";
        this.dstIP = dstIP + "/32";
        this.outputPort = outputPort;

        hardTimeout = idleTimeout = 0;
    }

    public FlowConfig(String nodeid, int tableID, int flowID, int flowPrio, String srcIP, String dstIP, String outputPort, String qid) {
        this.nodeID = nodeid;
        this.tableID = tableID;
        this.flowID = flowID;
        this.flowPrio = flowPrio;
        this.srcIP = srcIP + "/32";
        this.dstIP = dstIP + "/32";
        this.qID = qid;
        this.outputPort = outputPort;

        hardTimeout = idleTimeout = 0;
    }
    
    
    public void setFlowConfig(String nodeid, int tableID, int flowID, int flowPrio, String srcIP, String dstIP, String outputPort) {
        //clear object before use it
        clearFlowConfig();
        
        this.nodeID = nodeid;
        this.tableID = tableID;
        this.flowID = flowID;
        this.flowPrio = flowPrio;
        this.outputPort = outputPort;

        // Concat mask if not empty
        if(!srcIP.isEmpty())
            this.srcIP = srcIP + "/32";
        else
            this.srcIP="";
        if(!dstIP.isEmpty())
            this.dstIP = dstIP + "/32";
        else
            this.dstIP="";
        
        hardTimeout = idleTimeout = 0;
    }
    
    public void setFlowConfig(String nodeid, int tableID, int flowID, int flowPrio, String srcIP, String dstIP, String outputPort, String queueUUID) {
        //clear object before use it
        clearFlowConfig();
        
        this.nodeID = nodeid;
        this.tableID = tableID;
        this.flowID = flowID;
        this.flowPrio = flowPrio;
        this.outputPort = outputPort;
        this.qID = queueUUID;
        
        // Concat mask if not empty
        if(!srcIP.isEmpty())
            this.srcIP = srcIP + "/32";
        else
            this.srcIP="";
        if(!dstIP.isEmpty())
            this.dstIP = dstIP + "/32";
        else
            this.dstIP="";
        
        hardTimeout = idleTimeout = 0;
    }
    
    public void clearFlowConfig(){
        this.flowName = "";
        this.nodeID = "";
        this.tableID = 0;
        this.flowID = 0;
        this.flowPrio = 0;
        this.outputPort = "";
        this.srcIP = "";
        this.dstIP = "";
        qID = "";
        hardTimeout = idleTimeout = 0;
    }
    
    /* Setter and Getters */

    public String getNodeID() {
        return nodeID;
    }

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public int getFlowPrio() {
        return flowPrio;
    }

    public void setFlowPrio(int flowPrio) {
        this.flowPrio = flowPrio;
    }

    public int getFlowID() {
        return flowID;
    }

    public void setFlowID(int flowID) {
        this.flowID = flowID;
    }

    public int getTableID() {
        return tableID;
    }

    public void setTableID(int tableID) {
        this.tableID = tableID;
    }

    public int getHardTimeout() {
        return hardTimeout;
    }

    public void setHardTimeout(int hardTimeout) {
        this.hardTimeout = hardTimeout;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public String getOutputPort() {
        return outputPort;
    }

    public void setOutputPort(String outputPort) {
        this.outputPort = outputPort;
    }

    public String getqID() {
        return qID;
    }

    public void setqID(String qID) {
        this.qID = qID;
    }

    public String getSrcIP() {
        return srcIP;
    }

    public void setSrcIP(String srcIP) {
        this.srcIP = srcIP + "/32";

    }

    public String getDstIP() {
        return dstIP;
    }

    public void setDstIP(String dstIP) {
        this.dstIP = dstIP + "/32";
    }

}
