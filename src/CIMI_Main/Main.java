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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
        FlowConfig fc;
        QosConfig qc;
        
        MyXML.setCredentials("admin", "admin");     // setting credentials to xml request
        MyJson.setCredentials("admin", "admin");    // setting credentials to json request
        System.out.println("Controller credentials set.");
        
        /*Getting Topology*/
        System.out.println("Obtaining network topology...");
        MyXML.sendGet(Constants.topo, null); // requesting topology to controller
        
        //Getting ovs node id info
        MyJson.sendGet(Constants.node, null);
        //Getting ovs port uuid info
        qc = new QosConfig(Constants.ovsID);
        MyJson.sendGet(Constants.port, qc);
        
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

    public static void checkPortUUID(){
        System.out.println("--------------------Printing Port ID and UUID------------------------");
        
        for(TopoNode tn : Utils.topo.getAllNodes())
            for(int i = 0; i < tn.getAllPorts().size(); i++)
                System.out.println("portID= "+tn.getAllPorts().get(i).getPortID()+
                        "\tportUUID= "+tn.getAllPorts().get(i).getPortUUID());
    }
}
