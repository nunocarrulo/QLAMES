/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package REST_Requests;

import TopologyManagerUtils.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import CIMI_Main.Main;
import static REST_Requests.BaseURLs.urlFlowReplacer;
import TopologyManagerImpl.FlowConfig;
import java.io.DataOutputStream;
import org.w3c.dom.Element;

/**
 *
 * @author nuno
 */
public class MyXML {
    private static String user = new String();
    private static String password = new String();
    private static boolean debug = false;
    
    public static void setCredentials(String username, String pass){
        user = username;
        password = pass;
    }
    
    public static void sendGet(int type, FlowConfig fc){
        
        URL url;
        HttpURLConnection connection = null;
        String restURL = new String();
        try {
            
            switch(type){
                case 1:
                    // Get topology restUrl
                    restURL = BaseURLs.getTopo;
                    break;
                case 2:
                    // Get table restUrl
                    restURL = urlFlowReplacer(BaseURLs.getTable, fc.getNodeID(), Integer.toString(fc.getTableID()));
                    
                    break;
                case 3:
                    // Get flow restUrl
                    restURL = urlFlowReplacer(BaseURLs.getFlow, fc.getNodeID(), Integer.toString(fc.getTableID()), Integer.toString(fc.getFlowID()));
                    break;
                default:
                    System.out.println("Undefined type, nothing will be done with request");
            }
            
            url = new URL(restURL);
            // Create authentication string and encode it to Base64
            String authStr = user + ":" + password;
            String encodedAuthStr = Base64.encodeBase64String(authStr.getBytes());

            // Create Http connection
            System.out.println("Opening connection...");
            connection = (HttpURLConnection) url.openConnection();

            // Set connection properties
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Basic "
                    + encodedAuthStr);
            connection.setRequestProperty("Content-Type", "application/xml");
            connection.setRequestProperty("Accept", "application/xml");
            //connection.setUseCaches(false);
            connection.setDoInput(true);

            // Get the response from connection's inputStream
            InputStream xml = connection.getInputStream();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xml);
            
            //normalize document
            doc.getDocumentElement().normalize();
            
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            
            switch(type){
                case 1:
                    // Decode and save topology
                    Utils.decodeTopology(doc);
                    break;
                case 2:
                    // Get table info
                    //readTable();
                    
                    break;
                case 3:
                    // Get flow info
                    //readFlow();
                    break;
                default:
                    System.out.println("Undefined type, nothing will be done with request");
            }
         

        } catch (MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException | SAXException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        
    }
    
    public static boolean sendPut(boolean Q, boolean arp, FlowConfig fc) {

        Document xmlDoc = createFlow(Q, arp, fc);

        if (xmlDoc == null)
            return false;

        StringBuilder result = new StringBuilder();
        URL url;
        HttpURLConnection connection = null;
        String restURL = new String();

        try {
            restURL = BaseURLs.urlFlowReplacer(BaseURLs.putFlow, fc.getNodeID(), Integer.toString(fc.getTableID()), Integer.toString(fc.getFlowID()));
            url = new URL(restURL);

            // Create authentication string and encode it to Base64
            String authStr = user + ":" + password;
            String encodedAuthStr = Base64.encodeBase64String(authStr.getBytes());

            // Create Http connection
            System.out.println("Opening connection...");
            connection = (HttpURLConnection) url.openConnection();

            if (debug) {
                System.setProperty("http.proxyHost", "localhost");
                System.setProperty("http.proxyPort", "8888");
            }
            // Set connection properties
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Authentication", "Basic");
            connection.setRequestProperty("Authorization", "Basic "+ encodedAuthStr);
            connection.setRequestProperty("Content-Type", "application/xml");
            connection.setRequestProperty("Accept", "application/xml");
            //connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            System.out.println("Setting connection properties and data...");

            //normalize document
            xmlDoc.getDocumentElement().normalize();

            if (debug) 
                System.out.println(getStringFromDoc(xmlDoc));

            // Set data to send and close channel
            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(getStringFromDoc(xmlDoc));
            os.flush();
            os.close();

            if (debug)
                System.out.println("Request Sent. \nWaiting for response...");
            
            // Get the response code
            InputStream xml = connection.getInputStream();

            int status = connection.getResponseCode();
            if (debug) {
                System.out.println("Status: " + status);
            }

            if (status != Constants.OK) {
                return false;
            }

            /*DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
             DocumentBuilder db = dbf.newDocumentBuilder();
             Document doc = db.parse(xml);
             */
        } catch (Exception e) {
            System.out.println("Exception " + e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        // Success
        return true;
    }
    
    public static boolean sendDelete(int type, FlowConfig fc){
        
        URL url;
        HttpURLConnection connection = null;
        String restURL;
        try {
            
            switch(type){
                case 2:
                    // Delete table 
                    restURL = urlFlowReplacer(BaseURLs.delTable, fc.getNodeID(), Integer.toString(fc.getTableID()));
                    
                    break;
                case 3:
                    // Delete flow 
                    restURL = urlFlowReplacer(BaseURLs.delFlow, fc.getNodeID(), Integer.toString(fc.getTableID()), Integer.toString(fc.getFlowID()));
                    break;
                default:
                    System.out.println("Undefined type to delete, nothing will be done with request");
                    return false;
            }
            
            url = new URL(restURL);
            // Create authentication string and encode it to Base64
            String authStr = user + ":" + password;
            String encodedAuthStr = Base64.encodeBase64String(authStr.getBytes());

            // Create Http connection
            System.out.println("Opening connection...");
            connection = (HttpURLConnection) url.openConnection();

            // Set connection properties
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Authorization", "Basic "
                    + encodedAuthStr);
            connection.setRequestProperty("Content-Type", "application/xml");
            connection.setRequestProperty("Accept", "application/xml");
            //connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Get the response from connection's inputStream
            InputStream xml = connection.getInputStream();

            int status = connection.getResponseCode();
            if (debug) {
                System.out.println("Status: " + status);
            }

            if (status != Constants.OK) {
                return false;
            }
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return true;
    }
    
    public static Document createFlow(boolean useQueue, boolean arp, FlowConfig fc) {
        System.out.println("Creating Flow with id= "+fc.getFlowID());
        
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
            flowName.appendChild(doc.createTextNode(fc.getFlowName()));
            flow.appendChild(flowName);

            // priority
            Element priority = doc.createElement("priority");
            priority.appendChild(doc.createTextNode(Integer.toString(fc.getFlowPrio())));
            flow.appendChild(priority);

            //id and table id
            Element flowID = doc.createElement("id");
            flowID.appendChild(doc.createTextNode(Integer.toString(fc.getFlowID())));
            flow.appendChild(flowID);
            Element tableID = doc.createElement("table_id");
            tableID.appendChild(doc.createTextNode(Integer.toString(fc.getTableID())));
            flow.appendChild(tableID);

            //timeouts
            Element hardTimeout = doc.createElement("hard-timeout");
            hardTimeout.appendChild(doc.createTextNode(Integer.toString(fc.getHardTimeout())));
            flow.appendChild(hardTimeout);
            Element softTimeout = doc.createElement("idle-timeout");
            softTimeout.appendChild(doc.createTextNode(Integer.toString(fc.getIdleTimeout())));
            flow.appendChild(softTimeout);

            /* MATCH */
            Element match = doc.createElement("match");
            flow.appendChild(match);
            Element ethernetMatch = doc.createElement("ethernet-match");
            match.appendChild(ethernetMatch);
            Element ethernetType = doc.createElement("ethernet-type");
            ethernetMatch.appendChild(ethernetType);
            Element type = doc.createElement("type");
            
            if(arp)
                type.appendChild(doc.createTextNode(Constants.arp));   // ARP
            else 
                type.appendChild(doc.createTextNode(Constants.ipv4));   // IPv4
            ethernetType.appendChild(type);
            
            //include src and dst IP if not ARP packet
            if(!arp){
                Element srcIP = doc.createElement("ipv4-source");
                srcIP.appendChild(doc.createTextNode(fc.getSrcIP()));
                match.appendChild(srcIP);
                Element dstIP = doc.createElement("ipv4-destination");
                dstIP.appendChild(doc.createTextNode(fc.getDstIP()));
                match.appendChild(dstIP);
            }
            
            /* INSTRUCTIONS */
            Element instructs = doc.createElement("instructions");
            flow.appendChild(instructs);
            Element instruct = doc.createElement("instruction");    //instruction
            instructs.appendChild(instruct);
            Element order = doc.createElement("order");             //intruction order
            order.appendChild(doc.createTextNode("0"));
            instruct.appendChild(order);
            
            Element applyActions = doc.createElement("apply-actions");  //apply actions
            instruct.appendChild(applyActions);
            
            if(useQueue){
                Element actionQ = doc.createElement("action");               //queue action
                applyActions.appendChild(actionQ);                           // add to apply actions
                Element actOrderQ = doc.createElement("order");              //create order node
                actOrderQ.appendChild(doc.createTextNode("0"));              //assign value to order
                actionQ.appendChild(actOrderQ);                               //append to action node
                Element outputActionQ = doc.createElement("output-action"); //create output action
                actionQ.appendChild(outputActionQ);                         //append to action
                Element outputNodeConQ = doc.createElement("output-node-connector");    //create output con
                outputNodeConQ.appendChild(doc.createTextNode(fc.getOutputPort()));                    //set output con value
                outputActionQ.appendChild(outputNodeConQ);                              //append to output action
            }
            /* Output to a port action */
            Element action = doc.createElement("action");               
            applyActions.appendChild(action);
            Element actOrder = doc.createElement("order");
            if(useQueue)
                actOrder.appendChild(doc.createTextNode("1"));
            else
                actOrder.appendChild(doc.createTextNode("0"));
            
            action.appendChild(actOrder);
            Element outputAction = doc.createElement("output-action");
            action.appendChild(outputAction);
            Element outputNodeCon = doc.createElement("output-node-connector");
            outputNodeCon.appendChild(doc.createTextNode(fc.getOutputPort()));
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
    
    private static String getStringFromDoc(org.w3c.dom.Document xmlDoc) {
        try {
            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            //initialize StreamResult with File object to save to file
            StreamResult resSrc = new StreamResult(new StringWriter());
            DOMSource src = new DOMSource(xmlDoc);
            tf.transform(src, resSrc);
            String xmlStr = resSrc.getWriter().toString();

            return xmlStr;
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
}
