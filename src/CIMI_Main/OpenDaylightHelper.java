/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CIMI_Main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author nuno
 */
public class OpenDaylightHelper {
    
    public static boolean installFlow(JSONObject postData, String user,
            String password, String baseURL) {

        StringBuffer result = new StringBuffer();
        try {

            if (!baseURL.contains("http")) {
                baseURL = "http://" + baseURL;
            }
            baseURL = baseURL
                    + "/controller/nb/v2/flowprogrammer/default/node/OF/"
                    + postData.getJSONObject("node").get("id") + "/staticFlow/"
                    + postData.get("name");            // Create URL = base URL + container
            URL url = new URL(baseURL);

            // Create authentication string and encode it to Base64
            String authStr = user + ":" + password;
            String encodedAuthStr = Base64.encodeBase64String(authStr
                    .getBytes());

            // Create Http connection
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();

            // Set connection properties
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Authorization", "Basic "
                    + encodedAuthStr);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Set Post Data
            OutputStream os = connection.getOutputStream();
            os.write(postData.toString().getBytes());
            os.close();

            // Get the response from connection's inputStream
            InputStream content = (InputStream) connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    content));
            String line = "";
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if ("success".equalsIgnoreCase(result.toString())) {
            return true;
        } else {
            return false;
        }
    }
    
}
