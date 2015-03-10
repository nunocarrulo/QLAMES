/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CIMI_Main;

import REST_Requests.MyXML;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.codec.binary.Base64;

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
    static Document xmlDoc = null;

    public static void main(String[] args) {
        // TODO code application logic here

        //String url = "http://192.168.57.1:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:1/table/0/flow/15";
        //sendRequest("admin", "admin", url);
        
        String link = "http://192.168.57.1:8181/restconf/operational/network-topology:network-topology/topology/flow:1/";
        // setting credentials to request
        MyXML.setCredentials("admin", "admin");
        
        // requesting topology to controller
        MyXML.sendGet(link, Constants.topo);
        
    }

    public static boolean sendRequest(String user, String password, String rcvUrl) {
        
        createXMLFile();

        if (xmlDoc.equals(null)) {
            return false;
        }

        StringBuffer result = new StringBuffer();
        URL url;
        HttpURLConnection connection = null;

        try {

            //if (!baseURL.contains("http")) {
            //    baseURL = "http://" + baseURL;
            //}
            url = new URL(rcvUrl);

            // Create authentication string and encode it to Base64
            String authStr = user + ":" + password;
            String encodedAuthStr = Base64.encodeBase64String(authStr.getBytes());

            // Create Http connection
            System.out.println("Opening connection...");
            connection = (HttpURLConnection) url.openConnection();

            // Set connection properties
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Authorization", "Basic "
                    + encodedAuthStr);
            connection.setRequestProperty("Content-Type", "application/xml");
            connection.setRequestProperty("Accept", "application/xml");
            //connection.setUseCaches(false);
            connection.setDoInput(true);
            //connection.setDoOutput(true);
            System.out.println("Setting connection properties and data...");
            // Set Post Data
            OutputStream os = connection.getOutputStream();
            os.write(xmlDoc.toString().getBytes());
            os.close();
            System.out.println("Request Send. \nWaiting for response...");

            // Get the response
            InputStream xml = connection.getInputStream();

            int status = connection.getResponseCode();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xml);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            //initialize StreamResult with File object to save to file
            StreamResult res = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, res);
            String xmlString = res.getWriter().toString();
            System.out.println(xmlString);

            /*InputStream content = (InputStream) connection.getInputStream();
             BufferedReader in = new BufferedReader(new InputStreamReader(content));
             String line = "";
             while ((line = in.readLine()) != null) {
             result.append(line);
             }
             System.out.println("Line Read = " + line);*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        System.out.println("Done!");
        /*if ("success".equalsIgnoreCase(result.toString())) {
         return true;
         } else {
         return false;
         }*/
        return true;
    }

    public static void createXMLFile() {

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

            /*
             // set attribute to staff element
             Attr attr = doc.createAttribute("id");
             attr.setValue("1");
             flowName.setAttributeNode(attr);
 
             // shorten way
             // staff.setAttribute("id", "1");
 
             // firstname elements
             Element firstname = doc.createElement("firstname");
             firstname.appendChild(doc.createTextNode("yong"));
             staff.appendChild(firstname);
 
             // lastname elements
             Element lastname = doc.createElement("lastname");
             lastname.appendChild(doc.createTextNode("mook kim"));
             staff.appendChild(lastname);
 
             // nickname elements
             Element nickname = doc.createElement("nickname");
             nickname.appendChild(doc.createTextNode("mkyong"));
             staff.appendChild(nickname);
 
             // salary elements
             Element salary = doc.createElement("salary");
             salary.appendChild(doc.createTextNode("100000"));
             staff.appendChild(salary);
             */
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            StreamResult result = new StreamResult(new File("home/nuno/file.xml"));

            // Output to console for testing
            result = new StreamResult(System.out);

            transformer.transform(source, result);
            System.out.println();
            System.out.println("File saved!");
            xmlDoc = doc;

        } catch (TransformerException tfe) {
            tfe.printStackTrace();
            System.out.println("Exception " + tfe);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
