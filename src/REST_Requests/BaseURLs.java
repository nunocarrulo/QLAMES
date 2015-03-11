/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package REST_Requests;

/**
 *
 * @author nuno
 */
public class BaseURLs {
    /* ODL REST API */
    /* GETS */
    public static final String getTopo = "http://192.168.57.1:8181/restconf/operational/network-topology:network-topology/";
    public static final String getTable = "http://192.168.57.1:8181/restconf/config/opendaylight-inventory:nodes/node/<nodeid>/table/<tableid>/";
    public static final String getFlow = "http://192.168.57.1:8181/restconf/config/opendaylight-inventory:nodes/node/<nodeid>/table/<tableid>/flow/<flowid>";;
    /* PUTS */
    public static final String putFlow = "http://192.168.57.1:8181/restconf/config/opendaylight-inventory:nodes/node/<nodeid>/table/<tableid>/flow/<flowid>";
    /* DELETES */
    public static final String delTable = "http://192.168.57.1:8181/restconf/config/opendaylight-inventory:nodes/node/<nodeid>/table/<tableid>";
    public static final String delFlow = "http://192.168.57.1:8181/restconf/config/opendaylight-inventory:nodes/node/<nodeid>/table/<tableid>/flow/<flowid>";
    
    
    /* OVSDB REST API*/
    /* GETS */
    public static final String getNodes = "http://192.168.57.1:8080/controller/nb/v2/connectionmanager/nodes";
    public static final String getBridges = "http://192.168.57.1:8080/ovsdb/nb/v2/node/OVS/<ovsid>/tables/bridge/rows";
    public static final String getPorts = "http://192.168.57.1:8080/ovsdb/nb/v2/node/OVS/<ovsid>/tables/port/rows";
    public static final String getQos = "http://192.168.57.1:8080/ovsdb/nb/v2/node/<ovsid>/tables/qos/rows";
    public static final String getQueue = "http://192.168.57.1:8080/ovsdb/nb/v2/node/OVS/<ovsid>/tables/queue/rows";
    /* POSTS */
    public static final String postQos = "http://localhost:8080/ovsdb/nb/v2/node/OVS/<ovsid>/tables/QoS/rows";
    public static final String postQueue = "http://192.168.57.1:8080/ovsdb/nb/v2/node/OVS/<ovsid>/tables/queue/rows";
    /* DELETES */
    public static final String delQos = "http://localhost:8080/ovsdb/nb/v2/node/OVS/192.168.57.101:39994/tables/qos/rows/<qosuuid>";
    public static final String delQueue = "http://localhost:8080/ovsdb/nb/v2/node/OVS/192.168.57.101:39994/tables/queue/rows/<queueuuid>";
    
    /* Flows replacers */
    public static String urlFlowReplacer(String baseUrl, String nodeid, String tableid){
       String ret = baseUrl;
       /* Replacing fields */
       ret = ret.replace("<nodeid>", nodeid);
       ret = ret.replace("<tableid>", tableid);
       return ret;
    }
    public static String urlFlowReplacer(String baseUrl, String nodeid, String tableid, String flowid){
       String ret = baseUrl;
       /* Replacing fields */
       ret = ret.replace("<nodeid>", nodeid);
       ret = ret.replace("<tableid>", tableid);
       ret = ret.replace("<flowid>", flowid);
       return ret;
    }
    
    /* Ovs replacers */
    public static String urlOvsReplacer(String baseUrl, String ovsid){
       String ret = baseUrl;
       ret = ret.replace("<ovsid>", ovsid);   // Replacing fields 
       return ret;
    }
    public static String urlQosReplacer(String baseUrl, String qosuuid){
       String ret = baseUrl;
       ret = ret.replace("<qosuuid>", qosuuid);   // Replacing fields 
       return ret;
    }
    public static String urlQReplacer(String baseUrl, String queueuuid){
       String ret = baseUrl;
       ret = ret.replace("<queueuuid>", queueuuid);   // Replacing fields 
       return ret;
    }
    
}
