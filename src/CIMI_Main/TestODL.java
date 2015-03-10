/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CIMI_Main;

import java.util.logging.Level;
import java.util.logging.Logger;
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
    public static void main(String[] args) {

        try {
            //Sample post data.
            JSONObject postData = new JSONObject();

            postData.put("nameaurabh","TestFlow");
            postData.put("nwSrc","192.168.1.10");
            postData.put("nwDst", "192.168.1.11");
            postData.put("installInHw","true");
            postData.put("priority", "00");
            postData.put("etherType", "x800");
            postData.put("actionsw", "normal");//().put("ENQEUE=2"));
            
            //Node on which this flow should be installed
            JSONObject node = new JSONObject();
            node.put("id","0:00:00:76:54:54");
            node.put("type","OF");
            postData.put("node","de");
            
            //Actual flow install
            boolean result = OpenDaylightHelper.installFlow(postData, "admin", "admin", "localhost:8181");
            
            if(result){
                System.out.println("Flow installed Successfully");
            }else{
                System.err.println("Failed install flow!");
            }
        } catch (JSONException ex) {
            Logger.getLogger(TestODL.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Exception"+ex);
        }

        
    }
    
}
