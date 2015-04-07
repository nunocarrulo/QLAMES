/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TopologyManagerUtils;

import Dijkstra.ReservPath;
import static REST_Requests.Constants.lol;
import TopologyManagerImpl.NodeCon;
import TopologyManagerImpl.Port;
import TopologyManagerImpl.TopoNode;
import TopologyManagerImpl.Topology;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.jettison.json.JSONArray;
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

    public enum StatFields {

        tx_errors(0), collisions(1), tx_bytes(2), rx_dropped(4), tx_packets(5),
        rx_packets(6), tx_dropped(7), rx_errors(10), rx_bytes(11);
        public int val;

        StatFields(int val) {
            this.val = val;
        }
    }

    private static String[] swPorts;
    public static Topology topo = new Topology();
    public static List<String> ovledLinks = new ArrayList<>(); // <switch,port> values
    public static boolean LINK_OVL = false; //some link overloaded
    public static List<ReservPath> ReservPathList = new ArrayList<>();
    public static final boolean debug = true;
    public static String qosUUID;
    public static String queueUUID;
    public static int counter = 1;
    public static boolean firstTime = true;

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
                    /*
                    if (node_id.equals("openflow:2")) {
                        System.out.println("Utils OPENFLOW:2");
                        for (Port p : lol) {
                            System.out.println("\tPortID= " + p.getPortID());
                        }
                    }
                    */
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
        /* Checking which node ports are connected to hosts */
        setHostConPorts();
        
        if (true) {
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
        String iface = "";
        String[] aux = new String[10];
        if (debug) {
            System.out.println("-------------------------------Decoding Port and Iface UUID -------------------------------");
        }
        /* Get every port uuid and save it on portInfo data structure */
        while (it.hasNext()) {

            puuid = it.next().toString();    // get port uuid
            swPort = portJson.getJSONObject("rows").getJSONObject(puuid).get("name").toString().split("-");

            //if it is a switch pass to next uuid
            if (swPort.length < 2) {
                continue;
            }

            sw = "openflow:".concat((swPort[0].substring(1)));
            port = sw.concat(":").concat(swPort[1].substring(3));

            //set interface uuid
            String pattern = "([A-Za-z0-9-]{36})";
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(portJson.getJSONObject("rows").getJSONObject(puuid).get("interfaces").toString());
            if (m.find()) {
                iface = m.group(0);
            } else {
                System.out.println("Cannot find pattern");
            }

            if (true) {
                System.out.println("| Sw: " + sw + " | Port: " + port + " | PortUUID: " + puuid + " | Iface: " + iface);
            }
            /* Saving portUUIDs into topo data structure*/
            if (topo.nodeExists(sw)) {
                if (topo.getNode(sw).getPort(port) != null) {
                    topo.getNode(sw).getPort(port).setPortUUID(puuid);
                    topo.getNode(sw).getPort(port).setIfaceUUID(iface);
                } else {
                    System.out.println("Port " + port + " does not exist!");
                }
            } else {
                System.out.println("Node " + sw + " does not exist!");
            }

        }
        if (debug) {
            System.out.println("--------------------------------------------------------------------------------");
        }

    }

    public static void setQosUUID(String rcvQosUuid) {
        if (debug) {
            System.out.println(rcvQosUuid);
        }

        qosUUID = rcvQosUuid;
    }

    public static void setQueueUUID(String queueUUID) {
        Utils.queueUUID = queueUUID;
    }

    private static void solveBug() {
        TopoNode tn = topo.getNode("openflow:1");
        NodeCon newNc;
        for (NodeCon nc : tn.getNodeCon()) {
            newNc = new NodeCon(tn.getId(), nc.getTo(), nc.getFrom());
            topo.getNode(nc.getDstNodeId()).addNodeCon(newNc);
            if (debug) {
                System.out.println("Adding NodeCon: " + newNc.toString() + " to Node: " + topo.getNode(nc.getDstNodeId()));
            }
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
                        
                        /* detect if is a port connected to host */
                        if (!tpElem.getElementsByTagName("tp-id").item(0).getTextContent().contains("host:")) {
                            
                        }
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

    public static void decodeIfaceInfo(JSONObject ifaceJson) {
        long tx_errors, collisions, tx_bytes, rx_dropped, tx_packets,
                rx_packets, tx_dropped, rx_errors, rx_bytes;
        JSONArray train;

        synchronized (lol) {
            System.out.println("*******************************************************************");
            for (TopoNode tn : Utils.topo.getAllSwitches()) {
                for (Port p : tn.getAllPorts()) {
                    //ignore statistics from ports connected to hosts
                    try {
                        train = ifaceJson.getJSONObject("rows").getJSONObject(p.getIfaceUUID()).getJSONArray("statistics").getJSONArray(1);

                        //gather the info
                        collisions = train.getJSONArray(StatFields.collisions.val).getLong(1);
                        tx_errors = train.getJSONArray(StatFields.tx_errors.val).getLong(1);
                        tx_bytes = train.getJSONArray(StatFields.tx_bytes.val).getLong(1) * 8 / 1000;   //kbps
                        tx_packets = train.getJSONArray(StatFields.tx_packets.val).getLong(1);
                        tx_dropped = train.getJSONArray(StatFields.tx_dropped.val).getLong(1);
                        rx_errors = train.getJSONArray(StatFields.rx_errors.val).getLong(1);
                        rx_bytes = train.getJSONArray(StatFields.rx_bytes.val).getLong(1) * 8 / 1000;   //kbps
                        rx_packets = train.getJSONArray(StatFields.rx_packets.val).getLong(1);
                        rx_dropped = train.getJSONArray(StatFields.rx_dropped.val).getLong(1);

                        //update interface statistics on port
                        p.getiFaceStats().setIfaceStatistics(collisions, rx_bytes, rx_packets, rx_dropped, rx_errors, tx_bytes, tx_packets, tx_dropped, tx_errors);
                        if (p.getiFaceStats().getTx_bytes() >= p.getiFaceStats().getLastTx_bytes()) {
                            System.out.println("Curr " + p.getiFaceStats().getTx_bytes() + " Last " + p.getiFaceStats().getLastTx_bytes());
                            int bw = (int) (((p.getiFaceStats().getTx_bytes() - p.getiFaceStats().getLastTx_bytes())) / 5); //kbps 5 because every 5sec
                            p.updateCurrLoad(bw);
                        } else {
                            System.out.println("Last tx bytes < current tx bytes");
                        }

                        /* Verify congestioned links every 10 min */
                        //if(counter == p.getCurrBWHistory().getSize()){
                            /* Verify if link is overloaded (avgLinkLoad > 80% of LinkSpeed) */
                        //if( p.getCurrBWHistory().calcAvg() > 0.8 * p.getLinkSpeed() ){
                        if (!firstTime) {
                            if (p.getCurrBWHistory().getPrev() > 0.8 * p.getLinkSpeed()) {
                                System.out.println("Curr(prev) value(kbps): " + p.getCurrBWHistory().getPrev());
                                ovledLinks.add(p.getPortID());        // add port to ovledLinks
                                LINK_OVL = true;            // some link is overloaded
                                counter = 0; //reset counter
                                System.out.println("FOUND A CONGESTIONED LINK: " + p.getPortID() + " !");
                            } else {
                                //remove from ovled links if was put there
                                if (ovledLinks.contains(p.getPortID())) {
                                    String aux = p.getPortID();
                                    ovledLinks.remove(aux);
                                }
                            }
                        }

                        counter++;
                        System.out.println("Switch " + tn.getId() + " Port: " + p.getPortID() + " with iface: " + p.getIfaceUUID()
                                + " Current Link Load (kbps): " + p.getCurrBwLoad() + " Avg Link Load(kbps): " + p.getCurrBWHistory().getAverage());

                        if (debug) {
                            System.out.println(p.getiFaceStats().toString());
                        }
                    } catch (JSONException ex) {
                        Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            if (firstTime) {
                firstTime = false;
            }
            System.out.println("*******************************************************************");
        }

    }

    private static void setHostConPorts(){
        
        for(TopoNode tn : topo.getAllSwitches()){
            for(NodeCon nc : tn.getNodeCon()){
                if(nc.getDstNodeId().contains("host:")){
                    //if switch port connected to host, set hostConnection true on that port
                    tn.getPort(nc.getFrom()).setHostConnection(true);
                }
            }
        }
        
    }
    
}
