/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package REST_Requests;

import CIMI_Main.Constants;
import CIMI_Main.Main;
import CIMI_Main.TestODL;
import static REST_Requests.BaseURLs.urlFlowReplacer;
import static REST_Requests.BaseURLs.urlOvsReplacer;
import static REST_Requests.BaseURLs.urlQReplacer;
import static REST_Requests.BaseURLs.urlQosReplacer;
import TopologyManagerImpl.FlowConfig;
import TopologyManagerImpl.QosConfig;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private static boolean debug = true;

    public static void setCredentials(String username, String pass) {
        user = username;
        password = pass;
    }

    public static void sendGet(String link, int type, QosConfig qc) {

        URL url;
        HttpURLConnection connection = null;
        String restURL = new String();
        try {

            switch (type) {
                case 1:
                    // Get queue info
                    restURL = urlOvsReplacer(BaseURLs.getQueue, qc.getOvsid());
                    break;
                case 2:
                    // Get qos info
                    //urlFlowReplacer(BaseURLs.getTable, nodeid, table id);
                    restURL = urlOvsReplacer(BaseURLs.getQos, qc.getOvsid());
                    break;
                case 3:
                    // Get port info
                    restURL = urlOvsReplacer(BaseURLs.getPorts, qc.getOvsid());
                    break;
                case 4:
                    // Get bridge info
                    restURL = urlOvsReplacer(BaseURLs.getBridges, qc.getOvsid());
                    break;
                case 5:
                    // Get node info
                    restURL = BaseURLs.getNodes;
                    break;
                default:
                    System.out.println("Undefined type, nothing will be done with request");
            }
            
            url = new URL(link);
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
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            //connection.setUseCaches(false);
            connection.setDoInput(true);

            // Get the response from connection's inputStream
            InputStream json = connection.getInputStream();

            /* READ JSON REQUEST !!!*/
            
            switch (type) {
                case 1:
                    // Get queue info
                    
                    break;
                case 2:
                    // Get qos info
                    

                    break;
                case 3:
                    // Get port info
                    
                    break;
                case 4:
                    // Get bridge info
                    
                    break;
                case 5:
                    // Get node info
                    
                    break;
                default:
                    System.out.println("Undefined type, nothing will be done with request");
            }
            

        } catch (MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    public static boolean sendPost(boolean queue, int type, QosConfig qc) {

        JSONObject data;
        
        // Queue or Qos row creation
        if(queue)
            data = createQueue(qc);
        else
            data = createQos(qc);

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

            if (true) {
                System.out.println(data.toString());
            }

            // Set data to send and close channel
            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(data.toString());
            os.flush();
            os.close();

            if (debug) {
                System.out.println("Request Send. \nWaiting for response...");
            }

            // Get the response code
            InputStream json = connection.getInputStream();

            int status = connection.getResponseCode();
            if (debug) {
                System.out.println("Status: " + status);
            }

            if (status != Constants.CREATED) {
                return false;
            }

            //READ UUID
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

    public static boolean sendDelete(int type) {

        URL url;
        HttpURLConnection connection = null;
        String restURL;
        try {

            switch (type) {
                case 1:
                    restURL = urlQReplacer(BaseURLs.delQos, "<queueuuid>"); // Delete queue 
                    break;
                case 2:
                    restURL = urlQosReplacer(BaseURLs.delQos, "<qosuuid>"); // Delete qos 
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
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            //connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Get the response from connection's inputStream
            InputStream xml = connection.getInputStream();

            int status = connection.getResponseCode();
            if (debug) {
                System.out.println("Status: " + status);
            }

            if (status != Constants.NO_CONTENT) {
                return false;
            }
            if(debug){
                if(type == 1)
                    System.out.println("Queue row deleted with success");
                else
                    System.out.println("QoS row deleted with success");
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
            return data;
        } catch (JSONException ex) {
            Logger.getLogger(TestODL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static JSONObject createQueue(QosConfig qc) {

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
            return data;
        } catch (JSONException ex) {
            Logger.getLogger(TestODL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
