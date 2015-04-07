/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dijkstra;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nuno
 */
public class ReservPath {

    private int resID;
    private String srcIP, dstIP;
    private List<ParsedPath> path;
    private List<ParsedPath> revPath;

    public ReservPath() {
    }

    public ReservPath(int resID) {
        this.resID = resID;
        path = new ArrayList<>();
        revPath = new ArrayList<>();
    }

    public void setPaths(List<ParsedPath> path, List<ParsedPath> revPath) {
        ParsedPath newpp;
        for(ParsedPath p: path){
            newpp = new ParsedPath();
            newpp.setPortNumber(p.getPortNumber());
            newpp.setSwID(p.getSwID());
            this.path.add(newpp);
        }
        
        for(ParsedPath revp: revPath){
            newpp = new ParsedPath();
            newpp.setPortNumber(revp.getPortNumber());
            newpp.setSwID(revp.getSwID());
            this.revPath.add(newpp);
        }
        
    }

    public void setIPs(String srcIP, String dstIP) {
        this.srcIP = srcIP;
        this.dstIP = dstIP;
    }
    
    public void setPath(List<ParsedPath> path) {
        this.path = path;
    }

    public void setRevPath(List<ParsedPath> revPath) {
        this.revPath = revPath;
    }

    public int getResID() {
        return resID;
    }

    public List<ParsedPath> getPath() {
        return path;
    }

    public List<ParsedPath> getRevPath() {
        return revPath;
    }

    public String getSrcIP() {
        return srcIP;
    }

    public String getDstIP() {
        return dstIP;
    }
    
    @Override
    public String toString() {
        String ret = "ReservPath{ resID=" + resID;
        ret += "\nPath: ";
        for (int i = 0; i < path.size() - 1; i++) {
            ret += (" SwID " + path.get(i).getSwID() + " to target " + path.get(i + 1).getSwID() + " via " + path.get(i).getPortNumber());
        }
        ret += "\nReverse Path :";
        for (int i = 0; i < revPath.size() - 1; i++) {
            ret += (" SwID " + revPath.get(i).getSwID() + " to target " + revPath.get(i + 1).getSwID() + " via " + revPath.get(i).getPortNumber());
        }

        return ret;
    }

}
