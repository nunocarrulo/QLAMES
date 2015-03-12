/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OVS;

/**
 *
 * @author nuno
 */
public class SwPortInfo {
    
    private String sw;
    private String port;
    private String portUuid;


    public SwPortInfo(String sw, String port, String portUuid) {
        this.sw = sw;
        this.port = port;
        this.portUuid = portUuid;
    }
    
    public String getSw() {
        return sw;
    }

    public String getPort() {
        return port;
    }

    public String getPortUuid() {
        return portUuid;
    }
    
}
