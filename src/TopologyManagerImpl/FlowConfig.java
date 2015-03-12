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
    private String queueUUID;
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

    public FlowConfig(String nodeid, int tableID, int flowID, int flowPrio, String srcIP, String dstIP, String outputPort, String queueUUID) {
        this.nodeID = nodeid;
        this.tableID = tableID;
        this.flowID = flowID;
        this.flowPrio = flowPrio;
        this.srcIP = srcIP + "/32";
        this.dstIP = dstIP + "/32";
        this.queueUUID = queueUUID;
        this.outputPort = outputPort;

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

    public String getQueueUUID() {
        return queueUUID;
    }

    public void setQueueUUID(String queueUUID) {
        this.queueUUID = queueUUID;
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
