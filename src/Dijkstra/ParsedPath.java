/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dijkstra;

/**
 *
 * @author nuno
 */
public class ParsedPath {

    private String swID;
    private String portNumber;

    public ParsedPath() {
    }

    public ParsedPath(String swID, String portNumber) {
        this.swID = swID;
        this.portNumber = portNumber;
    }

    public String getSwID() {
        return swID;
    }

    public void setSwID(String swID) {
        this.swID = swID;
    }

    public String getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
    }

}
