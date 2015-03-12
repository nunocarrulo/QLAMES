/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OVS;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author nuno
 */
public class nwPortInfo {
    private static List<SwPortInfo> portInfo = new ArrayList<>();
    
    public static void decodePortInfo(JSONObject portJson) throws JSONException{
        
        Iterator it = portJson.getJSONObject("rows").keys();
        String sw, port, puuid;
        String[] swPort;
        /* Get every port uuid and save it on portInfo data structure */
        while(it.hasNext()){
            puuid = it.next().toString();    // get port uuid
            swPort = portJson.getJSONObject("rows").getJSONObject(puuid).get("name").toString().split("-");
            
            //if it is a switch pass to next uuid
            if(swPort.length < 2)
                continue;
            
            sw = "openflow:".concat((swPort[0].substring(1)));
            port = sw.concat(":").concat(swPort[1].substring(3));
            portInfo.add( new SwPortInfo(sw, port, puuid));
            System.out.println("Sw: "+sw+" Port: "+port+" PortUUID: "+puuid);
        }
        //System.out.println(portJson.getJSONObject("rows").keys());
        //portJson.getJSONArray("rows").getJSONObject(0).get("id").toString();
    }
}
