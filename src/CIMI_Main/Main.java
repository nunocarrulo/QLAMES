/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CIMI_Main;

import DB.FlowMapJpaController;
import DB.QosMapJpaController;
import DB.ReservationJpaController;
import Dijkstra.Launcher;
import REST_Requests.Constants;
import REST_Requests.BaseURLs;
import REST_Requests.MyJson;
import REST_Requests.MyXML;
import TopologyManagerImpl.FlowConfig;
import TopologyManagerImpl.Port;
import TopologyManagerImpl.QosConfig;
import TopologyManagerImpl.TopoNode;
import TopologyManagerUtils.Utils;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author nuno
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    static boolean debug = false;
    static boolean queue = false;
    static ReservationJpaController resCtl;
    static FlowMapJpaController fmCtl;
    static QosMapJpaController qmCtl;

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

        /* Finding paths using Dijkstra algorithm */
        System.out.println("Running Dijkstra algorithm...");
        Launcher.runDijkstra();
        System.out.println("Done!");
        System.exit(0);
        
        /* Installing ARP flows */
        if (debug) {
            System.out.println("Installing ARP flow in all switches!");
        }
        for (TopoNode tn : Utils.topo.getAllNodes()) {
            if (tn.isIsHost()) {
                continue;
            }

            fc.setFlowConfig(tn.getId(), 0, 4, 4, "", "", Constants.OFLogicalPorts.ALL.name());
            MyXML.sendPut(false, true, fc);
            //removeARPFromAll();
        }

        /* Getting Ovs Node Id info */
        MyJson.sendGet(Constants.node, null);
        qc.setOvsid(Constants.ovsID);
        System.out.println("OvsID: " + Constants.ovsID);
        /* Getting Ovs Port Uuid info */
        qc.setOvsid(Constants.ovsID);
        MyJson.sendGet(Constants.port, qc);
        //checkPortUUID();

        /* Create and store QoS UUID info in every switch and every port*/
        System.out.println("Adding qosUUID to all ports...");
        for (TopoNode tn : Utils.topo.getAllSwitches()) {
            for (Port p : tn.getAllPorts()) {
                qc.setPortuuid(p.getPortUUID());
                MyJson.sendPost(queue, Constants.qos, qc);
                p.setQosUUID(Utils.qosUUID);
                System.out.println("Added qosUUID: " + p.getQosUUID() + " to port: " + p.getPortID());
            }
            break;
        }
        System.out.println("Done!");
        System.out.println("Waiting 10 seconds...");
        try {
            Thread.sleep(10000);                 //1000 milliseconds is one second.
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Deleting Qos entries from all ports.");
        /* Remove all qos uuid */
        qc.clear();
        removeQosRowsFromAll(qc);
        
        /* Apply Dijkstra to find paths between hosts */
        
        /* Prepare database to write and read */
        prepareDB();
        
        /* Poll database entries to check new reservations */
        
        System.exit(0);
        
        /* Standalone tests */
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

    public static void prepareDB(){
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("lol");
        EntityManager em = emf.createEntityManager();
        resCtl = new ReservationJpaController(emf);
        fmCtl = new FlowMapJpaController(emf);
        qmCtl = new QosMapJpaController(emf);
    }
    
    public static void removeARPFromAll() {
        FlowConfig fc = new FlowConfig();

        for (TopoNode tn : Utils.topo.getAllNodes()) {
            if (tn.isIsHost()) {
                continue;
            }
            fc.setFlowConfig(tn.getId(), 0, 4, 4, "", "", Constants.OFLogicalPorts.ALL.name());
            MyXML.sendDelete(Constants.flow, fc);
        }
    }

    public static void removeQosRowsFromAll(QosConfig qc) {

        for (TopoNode tn : Utils.topo.getAllSwitches()) {
            for (Port p : tn.getAllPorts()) {
                qc.setQosuuid(p.getQosUUID());
                System.out.println("Deleting "+qc.getQosuuid());
                MyJson.sendDelete(Constants.qos, qc);
                p.setQosUUID("");
                System.out.println("Removed qosUUID: " + qc.getQosuuid() + " from port: " + p.getPortID());
            }
            break;
        }
        
    }

    public static void checkPortUUID() {
        System.out.println("--------------------Printing Port ID and UUID------------------------");

        for (TopoNode tn : Utils.topo.getAllNodes()) {
            for (int i = 0; i < tn.getAllPorts().size(); i++) {
                System.out.println("portID= " + tn.getAllPorts().get(i).getPortID()
                        + "\tportUUID= " + tn.getAllPorts().get(i).getPortUUID());
            }
        }
    }
}
