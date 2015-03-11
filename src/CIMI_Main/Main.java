/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CIMI_Main;

import REST_Requests.BaseURLs;
import REST_Requests.MyXML;
import TopologyManagerImpl.FlowConfig;
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

        MyXML.setCredentials("admin", "admin"); // setting credentials to request
        System.out.println("Controller credentials set.");
        /*Getting Topology*/
        System.out.println("Obtaining network topology...");
        MyXML.sendGet(BaseURLs.getTopo, Constants.topo); // requesting topology to controller
        
        FlowConfig fc = new FlowConfig();
        
        String url = "http://192.168.57.1:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:1/table/0/flow/15";
        MyXML.sendPut(url, fc);
        MyXML.sendDelete(Constants.flow, fc);
        //String link = "http://192.168.57.1:8181/restconf/operational/network-topology:network-topology/topology/flow:1/";


    }

    public static Document createXMLFile(boolean queue) {

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements - flow
            Document doc = docBuilder.newDocument();
            Element flow = doc.createElement("flow");
            flow.setAttribute("xmlns", "urn:opendaylight:flow:inventory");
            doc.appendChild(flow);

            // flowname
            Element flowName = doc.createElement("flow-name");
            flowName.appendChild(doc.createTextNode("NBTest"));
            flow.appendChild(flowName);

            // priority
            Element priority = doc.createElement("priority");
            priority.appendChild(doc.createTextNode("35"));
            flow.appendChild(priority);

            //id and table id
            Element flowID = doc.createElement("id");
            flowID.appendChild(doc.createTextNode("15"));
            flow.appendChild(flowID);
            Element tableID = doc.createElement("table_id");
            tableID.appendChild(doc.createTextNode("0"));
            flow.appendChild(tableID);

            //timeouts
            Element hardTimeout = doc.createElement("hard-timeout");
            hardTimeout.appendChild(doc.createTextNode("2500"));
            flow.appendChild(hardTimeout);
            Element softTimeout = doc.createElement("idle-timeout");
            softTimeout.appendChild(doc.createTextNode("2250"));
            flow.appendChild(softTimeout);

            /* MATCH */
            Element match = doc.createElement("match");
            flow.appendChild(match);
            Element ethernetMatch = doc.createElement("ethernet-match");
            match.appendChild(ethernetMatch);
            Element ethernetType = doc.createElement("ethernet-type");
            ethernetMatch.appendChild(ethernetType);
            Element type = doc.createElement("type");
            type.appendChild(doc.createTextNode("2048"));
            ethernetType.appendChild(type);

            /* INSTRUCTIONS */
            Element instructs = doc.createElement("instructions");
            flow.appendChild(instructs);
            Element instruct = doc.createElement("instruction");
            instructs.appendChild(instruct);
            Element order = doc.createElement("order");
            order.appendChild(doc.createTextNode("0"));
            instruct.appendChild(order);
            Element applyActions = doc.createElement("apply-actions");
            instruct.appendChild(applyActions);
            Element action = doc.createElement("action");
            applyActions.appendChild(action);
            Element actOrder = doc.createElement("order");
            actOrder.appendChild(doc.createTextNode("0"));
            action.appendChild(actOrder);
            Element outputAction = doc.createElement("output-action");
            action.appendChild(outputAction);
            Element outputNodeCon = doc.createElement("output-node-connector");
            outputNodeCon.appendChild(doc.createTextNode("2"));
            outputAction.appendChild(outputNodeCon);

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            return doc;

        } catch (TransformerException tfe) {
            tfe.printStackTrace();
            System.out.println("Exception " + tfe);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
