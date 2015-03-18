/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CIMI_Main;

import REST_Requests.Constants;
import REST_Requests.MyJson;
import TopologyManagerImpl.QosConfig;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author nuno
 */
public class TestODL {

    /**
     * @param args the command line arguments
     */
    private static String lol;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        MyJson.setCredentials("admin", "admin");    // json
        String ovsid = "192.168.57.102:44926";
        try (BufferedReader br = new BufferedReader(new FileReader("qosUUID.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                QosConfig qc = new QosConfig(ovsid, line);
                MyJson.sendDelete(Constants.qos, qc);
            }
        }

        System.exit(0);
        jsonReqQos();
        jsonReqQueue();
        System.out.println("DONE!");

    }

    public static void jsonReqQos() {

        try {
            JSONObject data = new JSONObject();
            data.put("parent_uuid", "asdagas-a-das-d-ad-a-");
            JSONObject row = new JSONObject();
            JSONObject qos = new JSONObject();
            //define QoS
            qos.put("type", "linux-htb");       //qos type
            JSONArray maxrateArray = new JSONArray();      //maxrate array
            maxrateArray.put("max-rate");
            maxrateArray.put("1000000000");                   //define max rate value
            JSONArray optsArray = new JSONArray();
            optsArray.put(maxrateArray);
            JSONArray mapArray = new JSONArray();
            mapArray.put("map");

            mapArray.put(optsArray);            //incorporate other-config values array
            qos.put("other_config", mapArray);  //incorporate other config on qos row
            row.put("qos", qos);    //incorporate qos on row
            data.put("row", row);   //incorporate row on root

            System.out.println("Request print\n" + data.toString());

        } catch (JSONException ex) {
            Logger.getLogger(TestODL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void jsonReqQueue() {

        try {
            JSONObject data = new JSONObject();
            data.put("parent_uuid", "asdagas-a-das-d-ad-a-");
            JSONObject row = new JSONObject();
            JSONObject queue = new JSONObject();
            //define Queue
            JSONArray priority = new JSONArray();      //maxrate array
            priority.put("priority");
            priority.put("5");
            JSONArray minrateArray = new JSONArray();      //maxrate array
            minrateArray.put("min-rate");
            minrateArray.put("80000000");                   //define max rate value
            JSONArray maxrateArray = new JSONArray();      //maxrate array
            maxrateArray.put("max-rate");
            maxrateArray.put("100000000");                   //define max rate value
            JSONArray optsArray = new JSONArray();
            optsArray.put(priority);
            optsArray.put(minrateArray);
            optsArray.put(maxrateArray);
            JSONArray mapArray = new JSONArray();
            mapArray.put("map");

            mapArray.put(optsArray);            //incorporate other-config values array
            queue.put("other_config", mapArray);  //incorporate other config on qos row
            row.put("queue", queue);    //incorporate qos on row
            data.put("row", row);   //incorporate row on root

            System.out.println("Request print\n" + data.toString());

        } catch (JSONException ex) {
            Logger.getLogger(TestODL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
