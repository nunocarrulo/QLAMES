/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package REST_Requests;

import CIMI_Main.Main;
import CIMI_Main.TestODL;
import OVS.Queue;
import static REST_Requests.BaseURLs.urlOvsReplacer;
import static REST_Requests.BaseURLs.urlQReplacer;
import static REST_Requests.BaseURLs.urlQosReplacer;
import static REST_Requests.Constants.bridge;
import static REST_Requests.Constants.iface;
import static REST_Requests.Constants.node;
import static REST_Requests.Constants.port;
import static REST_Requests.Constants.qos;
import static REST_Requests.Constants.queue;
import TopologyManagerImpl.Port;
import TopologyManagerImpl.QosConfig;
import TopologyManagerUtils.Utils;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author nuno
 */
public class MyJson {

    private static String user = new String();
    private static String password = new String();
    private static boolean debug = false;

    public static void setCredentials(String username, String pass) {
        user = username;
        password = pass;
    }

    public static void sendGet(int type, QosConfig qc) {

        URL url;
        HttpURLConnection connection = null;
        String restURL = new String();
        String out = new String();

        if (false) {
            System.setProperty("http.proxyHost", "localhost");
            System.setProperty("http.proxyPort", "8888");
        }
        try {

            switch (type) {
                case queue:
                    // Get queue info
                    out = "Queue";
                    restURL = urlOvsReplacer(BaseURLs.getQueue, qc.getOvsid());
                    break;
                case qos:
                    // Get qos info
                    out = "QoS";
                    restURL = urlOvsReplacer(BaseURLs.getQos, qc.getOvsid());
                    break;
                case port:
                    // Get port info
                    out = "Port";
                    restURL = urlOvsReplacer(BaseURLs.getPorts, qc.getOvsid());
                    break;
                case bridge:
                    // Get bridge info
                    out = "Bridge";
                    restURL = urlOvsReplacer(BaseURLs.getBridges, qc.getOvsid());
                    break;
                case node:
                    // Get node info
                    out = "Node";
                    restURL = BaseURLs.getNodes;
                    break;
                case iface:
                    //get interface info
                    out = "Interface";
                    restURL = urlOvsReplacer(BaseURLs.getInterfaces, qc.getOvsid());
                    break;
                default:
                    System.out.println("Undefined type, nothing will be done with request");
            }
            if (debug) {
                System.out.println("Send Json GET type:" + out);
            }

            url = new URL(restURL);
            // Create authentication string and encode it to Base64
            String authStr = user + ":" + password;
            String encodedAuthStr = Base64.encodeBase64String(authStr.getBytes());

            // Create Http connection
            if (debug) {
                System.out.println("Opening connection...");
            }
            connection = (HttpURLConnection) url.openConnection();

            // Set connection properties
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Basic "
                    + encodedAuthStr);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            //connection.setUseCaches(false);
            connection.setDoInput(true);

            // Get the response from connection's inputStream
            InputStream jsonIS = connection.getInputStream();

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(jsonIS, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            JSONObject json = new JSONObject(responseStrBuilder.toString());

            /* READ JSON REQUEST !!!*/
            switch (type) {
                case queue:
                    // Get queue info
                    // not necessary for now
                    break;
                case qos:
                    // Get qos info
                    // not necessary for now
                    break;
                case port:
                    // Get port info
                    Utils.decodePortInfo(json);
                    break;
                case bridge:
                    // Get bridge info
                    // not necessary for now
                    break;
                case node:
                    // Get node info
                    if (debug) {
                        System.out.println(json.getJSONArray("node").getJSONObject(0).get("id").toString());
                    }
                    Constants.ovsID = json.getJSONArray("node").getJSONObject(0).get("id").toString();
                    break;
                    
                case iface:
                    Utils.decodeIfaceInfo(json);
                    break;
                default:
                    System.out.println("Undefined type, nothing will be done with request");
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(MyJson.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                connection.disconnect();
                if (debug) {
                    System.out.println("Connection closed.");
                }
            }
        }

    }

    public static boolean sendPost(boolean queue, int type, QosConfig qc) {

        JSONObject data;

        // Queue or Qos row creation
        if (queue) {
            data = createQueue(qc);
        } else {
            data = createQos(qc);
        }

        if (data == null) {
            return false;
        }

        URL url;
        HttpURLConnection connection = null;
        String restURL = new String();
        try {

            switch (type) {
                case 1:
                    // post queue info
                    restURL = urlOvsReplacer(BaseURLs.postQueue, qc.getOvsid());
                    break;
                case 2:
                    // post qos info
                    restURL = urlOvsReplacer(BaseURLs.postQos, qc.getOvsid());
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

            if (debug) {
                System.setProperty("http.proxyHost", "localhost");
                System.setProperty("http.proxyPort", "8888");
            }
            // Set connection properties
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authentication", "Basic");
            connection.setRequestProperty("Authorization", "Basic " + encodedAuthStr);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            //connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            System.out.println("Setting connection properties and data...");

            if (debug) {
                System.out.println("Request: " + data.toString());
            }

            try ( // Set data to send and close channel
                    DataOutputStream os = new DataOutputStream(connection.getOutputStream())) {
                os.writeBytes(data.toString());
                os.flush();
            }

            if (debug) {
                System.out.println("Request Send. \nWaiting for response...");
            }

            // Get the response code
            InputStream jsonIS = connection.getInputStream();

            int status = connection.getResponseCode();
            if (debug) {
                System.out.println("Status: " + status);
            }

            if (status != Constants.CREATED) {
                if (queue) {
                    System.out.println("Error creating Queue row. Status: " + status);
                } else {
                    System.out.println("Error creating QoS row. Status: " + status);
                }
                return false;
            }

            /* READ UUID */
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(jsonIS, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            if (debug) {
                System.out.println(responseStrBuilder.toString());
            }

            if (queue) {
                //saving in a temp static var to be stored on queue data structure
                Utils.setQueueUUID(responseStrBuilder.toString());
            } else {
                //saving in a temp static var to be stored on topo data structure
                Utils.setQosUUID(responseStrBuilder.toString());
            }
        } catch (Exception e) {
            System.out.println("LOLException " + e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        // Success
        return true;
    }

    public static boolean sendPut(int type, QosConfig qc, Port port) {

        JSONObject data;

        data = updateQos(port);

        if (data == null) {
            return false;
        }

        URL url;
        HttpURLConnection connection = null;
        String restURL = new String();
        try {
            
            switch (type) {
                case 1:
                    // put queue info
                    //restURL = urlOvsReplacer(BaseURLs.postQueue, qc.getOvsid());
                    break;
                case 2:
                    // post qos info
                    restURL = urlQosReplacer(BaseURLs.putQos, qc.getOvsid(), qc.getQosuuid());
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

            if (false) {
                System.setProperty("http.proxyHost", "localhost");
                System.setProperty("http.proxyPort", "8888");
            }
            // Set connection properties
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Authentication", "Basic");
            connection.setRequestProperty("Authorization", "Basic " + encodedAuthStr);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            //connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            System.out.println("Setting connection properties and data...");

            if (debug) {
                System.out.println("Request: " + data.toString());
            }
            
            try ( // Set data to send and close channel
                    DataOutputStream os = new DataOutputStream(connection.getOutputStream())) {
                os.writeBytes(data.toString());
                os.flush();
            }

            if (debug) {
                System.out.println("Request Send. \nWaiting for response...");
            }
            
            // Get the response code
            InputStream jsonIS = connection.getInputStream();

            int status = connection.getResponseCode();
            if (debug) {
                System.out.println("Status: " + status);
            }

            if (status != Constants.OK) {
                System.out.println("MyJson: Error updating QoS row. Status: " + status);
                return false;
            }

            /* Read response */
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(jsonIS, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            if (debug) {
                System.out.println(responseStrBuilder.toString());
            }
            if(!responseStrBuilder.toString().toLowerCase().equals("success"))
                return false;
            
        } catch (Exception e) {
            System.out.println("MyJson: Exception " + e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        // Success
        return true;
    }

    public static boolean sendDelete(int type, QosConfig qc) {

        URL url;
        HttpURLConnection connection = null;
        String restURL;
        try {

            switch (type) {
                case 1:
                    restURL = urlQReplacer(BaseURLs.delQueue, qc.getOvsid(), qc.getQueueuuid()); // Delete queue 
                    break;
                case 2:
                    restURL = urlQosReplacer(BaseURLs.delQos, qc.getOvsid(), qc.getQosuuid()); // Delete qos 
                    break;
                default:
                    System.out.println("Undefined type to delete, nothing will be done with request");
                    return false;
            }
            if (debug) {
                System.out.println("RestURL: " + restURL);
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
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            //connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Get the response from connection's inputStream
            //InputStream xml = connection.getInputStream();
            int status = connection.getResponseCode();
            if (true) {
                System.out.println("Status: " + status);
            }

            if (status != Constants.NO_CONTENT) {
                return false;
            }
            if (true) {
                if (type == 1) {
                    System.out.println("Queue row deleted with success");
                } else {
                    System.out.println("QoS row deleted with success");
                }
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

    private static JSONObject createQos(QosConfig qc) {
        JSONObject data = null;
        try {

            data = new JSONObject();
            data.put("parent_uuid", qc.getPortuuid().trim());
            //data.put("parent_uuid", "c16e54b4-5e46-429b-8861-c01b1ac98669");
            JSONObject row = new JSONObject();
            JSONObject qos = new JSONObject();
            //define QoS
            qos.put("type", "linux-htb");       //qos type
            JSONArray maxrateArray = new JSONArray();      //maxrate array
            maxrateArray.put("max-rate");
            maxrateArray.put(Integer.toString(qc.getMaxRateQos()).concat("000000").trim());    //define max rate value
            //maxrateArray.put("250000000");
            JSONArray optsArray = new JSONArray();
            optsArray.put(maxrateArray);
            JSONArray mapArray = new JSONArray();
            mapArray.put("map");

            mapArray.put(optsArray);            //incorporate other-config values array
            qos.put("other_config", mapArray);  //incorporate other config on qos row
            row.put("QoS", qos);    //incorporate qos on row
            data.put("row", row);   //incorporate row on root

            //System.out.println("Request print\n" + data.toString());
            return data;
        } catch (JSONException ex) {
            System.out.println("Exception " + ex);
            Logger.getLogger(TestODL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }

    private static JSONObject createQueue(QosConfig qc) {

        try {
            JSONObject data = new JSONObject();
            data.put("parent_uuid", qc.getPortuuid().trim());
            JSONObject row = new JSONObject();
            JSONObject queue = new JSONObject();
            //define Queue
            JSONArray priority = new JSONArray();      //maxrate array
            priority.put("priority");
            priority.put(Integer.toString(qc.getPriorityQ()));
            JSONArray minrateArray = new JSONArray();      //maxrate array
            minrateArray.put("min-rate");
            minrateArray.put(Integer.toString(qc.getMinRateQ()).concat("000").trim());                   //define max rate value
            JSONArray maxrateArray = new JSONArray();      //maxrate array
            maxrateArray.put("max-rate");
            maxrateArray.put(Integer.toString(qc.getMaxRateQ()).concat("000").trim());                   //define max rate value
            JSONArray optsArray = new JSONArray();
            optsArray.put(priority);
            optsArray.put(minrateArray);
            optsArray.put(maxrateArray);
            JSONArray mapArray = new JSONArray();
            mapArray.put("map");

            mapArray.put(optsArray);            //incorporate other-config values array
            queue.put("other_config", mapArray);  //incorporate other config on qos row
            row.put("Queue", queue);    //incorporate qos on row
            data.put("row", row);   //incorporate row on root

            //System.out.println("Request print\n" + data.toString());
            return data;
        } catch (JSONException ex) {
            Logger.getLogger(TestODL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static JSONObject updateQos(Port port) {
        JSONObject data = null;
        try {
            data = new JSONObject();

            JSONObject row = new JSONObject();
            JSONObject qos = new JSONObject();
            JSONArray mapIntUUID;       // <int,uuid>
            JSONArray others = new JSONArray();
            JSONArray uuid;

            for (Queue q : port.getQueues()) {
                // UUID definition
                uuid = new JSONArray();           // uuid array
                uuid.put("uuid");
                uuid.put(q.getUuid());

                // Map <int,UUID>
                mapIntUUID = new JSONArray();       // <int,uuid>
                mapIntUUID.put(q.getNumber());
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

            System.out.println("Request print\n" + data.toString());
            return data;
        } catch (JSONException ex) {
            System.out.println("Exception " + ex);
            Logger.getLogger(TestODL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }

}
