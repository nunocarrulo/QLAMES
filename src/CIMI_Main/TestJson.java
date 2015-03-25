/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CIMI_Main;

import REST_Requests.Constants;
import REST_Requests.MyJson;
import REST_Requests.MyXML;
import TopologyManagerImpl.QosConfig;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author nuno
 */
public class TestJson {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        QosConfig qc = new QosConfig();
        /* Credentials set to Rest requests */
        MyXML.setCredentials("admin", "admin");     // xml
        MyJson.setCredentials("admin", "admin");    // json
        System.out.println("Controller credentials set.");

        /* Getting Topology */
        System.out.println("Obtaining network topology...");
        MyXML.sendGet(Constants.topo, null); // requesting topology to controller
        
        /* Getting Ovs Node Id info */
        MyJson.sendGet(Constants.node, null);
        qc.setOvsid(Constants.ovsID);
        MyJson.sendGet(Constants.port, qc); //get interfaces uuids
        
        //get interface statistics
        MyJson.sendGet(Constants.iface, qc);

    }

    public static JSONObject putJson() {
        JSONObject data = null;
        try {

            data = new JSONObject();

            String[] uuids = new String[]{"c16e54b4-5e46-429b-8861-c01b1ac98669", "asda-asd-a-d-ad-a-w--qw-d"};
            JSONObject row = new JSONObject();
            JSONObject qos = new JSONObject();
            JSONArray mapIntUUID;       // <int,uuid>
            JSONArray others = new JSONArray();
            JSONArray uuid;

            for (int i = 1; i < 3; i++) {
                // UUID definition
                uuid = new JSONArray();           // uuid array
                uuid.put("uuid");
                uuid.put(uuids[i - 1]);
                // Map <int,UUID>
                mapIntUUID = new JSONArray();       // <int,uuid>
                mapIntUUID.put(i);
                mapIntUUID.put(uuid);
                // Several maps (one per queue)
                others.put(mapIntUUID);
            }
            // Map Encapsulation
            JSONArray mapArray = new JSONArray();
            mapArray.put("map");

            mapArray.put(others);            //incorporate other queue map<int,uuid> values array
            qos.put("queues", mapArray);    //incorporate queues on qos row
            row.put("QoS", qos);            //incorporate qos on row
            data.put("row", row);           //incorporate row on root

            //System.out.println("Request print\n" + data.toString());
            return data;
        } catch (JSONException ex) {
            System.out.println("Exception " + ex);
            Logger.getLogger(TestODL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }
}
