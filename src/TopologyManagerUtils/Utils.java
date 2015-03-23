/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TopologyManagerUtils;

import TopologyManagerImpl.NodeCon;
import TopologyManagerImpl.Port;
import TopologyManagerImpl.TopoNode;
import TopologyManagerImpl.Topology;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author nuno
 */
public class Utils {

    private static String[] swPorts;
    public static Topology topo = new Topology();
    public static final boolean debug = false;
    public static String qosUUID;
    public static String queueUUID;

    public static void decodeTopology(Document doc) {

        System.out.println("Decoding Topology...");

        TopoNode topoNode;

        /* Retrieving topology (switches and hosts) */
        NodeList nodes = doc.getElementsByTagName("node");

        if (true) {
            System.out.println("Network Nodes:");
        }

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) node;
                if (e.getNodeName().contains("node")) {
                    // Retrieving switch ID
                    String node_id = e.getElementsByTagName("node-id").item(0).getTextContent();
                    System.out.println("\t" + node_id);

                    /* Reading termination ports of node and Creating node */
                    topoNode = new TopoNode();
                    List<Port> lol = readTermPoints(e);
                    if(node_id.equals("openflow:2")){
                        System.out.println("Utils OPENFLOW:2");
                        for(Port p : lol){
                            System.out.println("\tPortID= "+p.getPortID());
                        }
                    }
                    
                    topoNode.setNode(node_id, lol);
                    
                    // if node is a host insert ip and mac
                    if (node_id.contains("host:")) {
                        //retrieve ip
                        Node addr = e.getElementsByTagName("addresses").item(0);
                        Element addrElem = (Element) addr;
                        String hostIP = addrElem.getElementsByTagName("ip").item(0).getTextContent();

                        //retrieve mac
                        String hostMAC = addrElem.getElementsByTagName("mac").item(0).getTextContent();
                        //set node ip and mac info
                        topoNode.setIPAndMac(hostIP, hostMAC);
                    }

                    // Adding node to topology
                    topo.addNode(topoNode);

                }
            }
        }
        // Reading nodes links
        readNodeLinks(doc);
        //solveBug();
        if(true){
            System.out.println("-------------------------------------------------------------------------------------");
            topo.printNodes();
        }
        System.out.println("Done decoding topology...");
        System.out.println("-------------------------------------------------------------------------------------");
    }

    public static void decodePortInfo(JSONObject portJson) throws JSONException {

        Iterator it = portJson.getJSONObject("rows").keys();
        String sw, port, puuid;
        String[] swPort;
        if(debug)
            System.out.println("-------------------------------Decoding Port UUID-------------------------------");
        /* Get every port uuid and save it on portInfo data structure */
        while (it.hasNext()) {
            
            puuid = it.next().toString();    // get port uuid
            swPort = portJson.getJSONObject("rows").getJSONObject(puuid).get("name").toString().split("-");

            //if it is a switch pass to next uuid
            if (swPort.length < 2)
                continue;
            
            sw = "openflow:".concat((swPort[0].substring(1)));
            port = sw.concat(":").concat(swPort[1].substring(3));
            
            if(debug)
                System.out.println("| Sw: " + sw + " | Port: " + port + " | PortUUID: " + puuid+ " |");
            /* Saving portUUIDs into topo data structure*/
            if(topo.nodeExists(sw))
                if(topo.getNode(sw).getPort(port) != null)
                    topo.getNode(sw).getPort(port).setPortUUID(puuid);
                else
                   System.out.println("Port "+port+" does not exist!"); 
            else
                System.out.println("Node "+sw+" does not exist!");
            
        }
        if(debug)
            System.out.println("--------------------------------------------------------------------------------");

    }

    public static void setQosUUID(String rcvQosUuid){
        if(debug)
            System.out.println(rcvQosUuid);

        qosUUID = rcvQosUuid;
    }
    
    public static void setQueueUUID(String queueUUID) {
        Utils.queueUUID = queueUUID;
    }
    
    private static void solveBug(){
        TopoNode tn = topo.getNode("openflow:1");
        NodeCon newNc; 
        for(NodeCon nc : tn.getNodeCon()){
            newNc = new NodeCon(tn.getId(), nc.getTo(), nc.getFrom());
            topo.getNode(nc.getDstNodeId()).addNodeCon(newNc);
            if(debug)
                System.out.println("Adding NodeCon: "+newNc.toString()+" to Node: "+topo.getNode(nc.getDstNodeId()));
        }
        
    }
    
    private static List<Port> readTermPoints(Element e) {
        // Retrieving switch ports
        NodeList tpList = e.getElementsByTagName("termination-point");
        int portCounter = -1;
        Port port;

        List<Port> portList = new ArrayList<Port>();

        swPorts = new String[tpList.getLength()];
        for (int j = 0; j < tpList.getLength(); j++) {
            Node tp = tpList.item(j);
            if (tp.getNodeType() == Node.ELEMENT_NODE) {
                //System.out.println("\nCurrent Element :" + tp.getNodeName());
                Element tpElem = (Element) tp;
                if (tpElem.getNodeName().contains("termination-point")) {
                    //if is not local port add else ignore
                    if (!tpElem.getElementsByTagName("tp-id").item(0).getTextContent().contains(":LOCAL")) {
                        swPorts[++portCounter] = tpElem.getElementsByTagName("tp-id").item(0).getTextContent();

                        if (debug) {
                            System.out.printf("\t\t%s\n", swPorts[portCounter]);
                        }

                        /* Add ports to the list */
                        port = new Port();
                        port.setPort(swPorts[portCounter]);
                        portList.add(port);
                    }
                }
            }
        }
        return portList;
    }

    private static void readNodeLinks(Document doc) {
        if (debug) {
            System.out.println("Network Links:");
            System.out.println("\t          Node \t\t\t      TP  ");
        }

        NodeList links = doc.getElementsByTagName("link");
        NodeCon nCon;

        for (int i = 0; i < links.getLength(); i++) {
            Node link = links.item(i);
            if (link.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) link;
                if (e.getNodeName().contains("link")) {
                    // Retrieving source information 
                    Node src = e.getElementsByTagName("source").item(0);
                    Element srcElem = (Element) src;
                    String srcTp = srcElem.getElementsByTagName("source-tp").item(0).getTextContent();
                    String srcNode = srcElem.getElementsByTagName("source-node").item(0).getTextContent();
                    if (debug) {
                        System.out.println("\tSource: " + srcNode + "\t\t" + srcTp);
                    }
                    
                    // Retrieving destination information
                    Node dst = e.getElementsByTagName("destination").item(0);
                    Element dstElem = (Element) dst;
                    String dstTp = dstElem.getElementsByTagName("dest-tp").item(0).getTextContent();
                    String dstNode = dstElem.getElementsByTagName("dest-node").item(0).getTextContent();

                    if (debug) {
                        System.out.println("\tDestin: " + dstNode + "\t\t" + dstTp);
                        System.out.println();
                    }
                    /* Adding connection to source node */
                    nCon = new NodeCon();
                    nCon.setConnection(dstNode, srcTp, dstTp);  // create node connection
                    
                    topo.getNode(srcNode).addNodeCon(nCon);     // get source node and add connection to it

                }
            }
        }
    }
    
    
}
