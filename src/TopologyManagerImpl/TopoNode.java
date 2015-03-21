/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TopologyManagerImpl;

import ITopologyManager.ITopoNode;
import TopologyManagerUtils.Utils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nuno
 */
public class TopoNode implements ITopoNode {

    private String id, ip, mac;
    private List<NodeCon> nodeCons;
    private List<Port> ports;
    private boolean isHost;
    private int flowID = 10;
    private int flowPrio = 10;  //increment for each reservation
    
    public TopoNode() {
        nodeCons = new ArrayList<>();
        ports = new ArrayList<>();
        id = new String();
        ip = new String();
        mac = new String();
        isHost = false;
    }

    @Override
    public void setNode(String id, List<Port> port) {
        this.id = id;
        this.ports = port;

    }

    @Override
    public void setNodeCons(List<NodeCon> nodeConList) {
        this.nodeCons = nodeConList;
    }

    @Override
    public void addNodeCon(NodeCon nodeCon) {
        if (!nodeCons.contains(nodeCon)) {
            nodeCons.add(nodeCon);
        } else {
            System.out.println("NodeCon already exists and thus was not added");
        }
    }

    @Override
    public void setIPAndMac(String ip, String mac) {
        this.ip = ip;
        this.mac = mac;
        isHost = true;
    }

    @Override
    public String toString() {
        String ret = new String();
        ret += "NodeID: " + id + "\n\tNodeConnections: \n";
        for (int i = 0; i < nodeCons.size(); i++) {
            ret += "\t\tConnected to: " + nodeCons.get(i).getDstNodeId() + " From: " + nodeCons.get(i).getFrom() + " To: " + nodeCons.get(i).getTo() + "\n";
        }
        ret += "\tNodePorts: \n";
        for (int i = 0; i < ports.size(); i++) {
            ret += "\t\t" + ports.get(i).getPortID() + "\n";
        }

        /* Print addresses if node is a host */
        if (isHost) {
            ret += "\tIP = " + ip + "\tMAC = " + mac + "\n";
        }

        return ret;
    }
    
    @Override
    public Port getPort(String portID) {
        for(Port port : ports){
            if(port.getPortID().equals(portID))
                return port;
        }
        if(Utils.debug)
            System.out.println("Port not found!");
        return null;
        
    }
    
    @Override
    public Port getPortByNumber(int portNumber) {
        
        for(Port port : ports){
            if(Integer.parseInt(port.getPortID().split(":")[2]) == portNumber)
                return port;
        }
        if(Utils.debug)
            System.out.println("Port not found!");
        return null;
        
    }
    
    @Override
    public String getNodeConFromPortByTarget(String target){
        for(NodeCon nc : nodeCons){
            if(nc.getDstNodeId().equals(target))
                return nc.getFrom();
        }
        return "";
    }
    
    @Override
    public int getAndIncFlowID() {
        int ret = flowID;
        flowID++;
        return ret;
    }
    
    /* Setter and Getters */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<NodeCon> getNodeCon() {
        return nodeCons;
    }

    public void setNodeCon(List<NodeCon> nodeCon) {
        this.nodeCons = nodeCon;
    }

    public List<Port> getAllPorts() {
        return ports;
    }
    
    public boolean isIsHost() {
        return isHost;
    }

    public String getIp() {
        return ip;
    }

    public String getMac() {
        return mac;
    }
    
    public int getFlowID() {
        return flowID;
    }

    public void setFlowID(int flowID) {
        this.flowID = flowID;
    }

    public int getFlowPrio() {
        return flowPrio;
    }

    public void setFlowPrio(int flowPrio) {
        this.flowPrio = flowPrio;
    }

}
