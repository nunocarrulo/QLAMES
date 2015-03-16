/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CIMI_Main;

import REST_Requests.Constants;
import REST_Requests.BaseURLs;
import REST_Requests.MyJson;
import REST_Requests.MyXML;
import TopologyManagerImpl.FlowConfig;
import TopologyManagerImpl.QosConfig;
import TopologyManagerImpl.TopoNode;
import TopologyManagerUtils.Utils;

/**
 *
 * @author nuno
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    //static Document xmlDoc = null;
    static boolean debug = false;

    public static void main(String[] args) {
        /* Variables and Data structures initialization */
        FlowConfig fc = new FlowConfig();
        QosConfig qc = new QosConfig();
        
        /* Credentials set to Rest requests */
        MyXML.setCredentials("admin", "admin");     // xml
        MyJson.setCredentials("admin", "admin");    // json
        System.out.println("Controller credentials set.");
        
        /* Getting Topology */
        System.out.println("Obtaining network topology...");
        MyXML.sendGet(Constants.topo, null); // requesting topology to controller
        
        /* Installing ARP flows */
        if(debug)
            System.out.println("Installing ARP flow in all switches!");
        for(TopoNode tn : Utils.topo.getAllNodes()){
            if(tn.isIsHost())
                continue;
            
            fc.setFlowConfig(tn.getId(), 0, 4, 4, "", "", Constants.OFLogicalPorts.ALL.name());
            MyXML.sendPut(false, true, fc);
            //removeARPFromAll();
        }
        
        /* Getting ovs node id info */
        MyJson.sendGet(Constants.node, null);
        /* Getting ovs port uuid info */
        qc.setOvsid(Constants.ovsID);
        MyJson.sendGet(Constants.port, qc);
        checkPortUUID();
        System.exit(0);
        
        fc = new FlowConfig("openflow:1", 0, 125, 125, "10.0.0.1", "10.0.0.2", "2");
        fc.setFlowName("LOL");  //optional
        String url;
        //String url = "http://192.168.57.1:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:1/table/0/flow/15";
        //url = BaseURLs.urlFlowReplacer(BaseURLs.putFlow, fc.getNodeID(), Integer.toString(fc.getTableID()), Integer.toString(fc.getFlowID()));
        //MyXML.sendPut(url, fc);
        url = BaseURLs.urlFlowReplacer(BaseURLs.delFlow, fc.getNodeID(), Integer.toString(fc.getTableID()), Integer.toString(fc.getFlowID()));
        MyXML.sendDelete(Constants.flow, fc);
        //String link = "http://192.168.57.1:8181/restconf/operational/network-topology:network-topology/topology/flow:1/";


    }

    public static void removeARPFromAll(){
        FlowConfig fc = new FlowConfig();
        
        for(TopoNode tn : Utils.topo.getAllNodes()){
            if(tn.isIsHost())
                continue;
            fc.setFlowConfig(tn.getId(), 0, 4, 4, "", "", Constants.OFLogicalPorts.ALL.name());
            MyXML.sendDelete(Constants.flow, fc);
        }
    }
    
    public static void checkPortUUID(){
        System.out.println("--------------------Printing Port ID and UUID------------------------");
        
        for(TopoNode tn : Utils.topo.getAllNodes())
            for(int i = 0; i < tn.getAllPorts().size(); i++)
                System.out.println("portID= "+tn.getAllPorts().get(i).getPortID()+
                        "\tportUUID= "+tn.getAllPorts().get(i).getPortUUID());
    }
}
