/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dijkstra;

import java.util.ArrayList;

/**
 *
 * @author nuno
 */
public class DC_Paths {
    
    private String sourceNode;
    private String destNode;
    private ArrayList<ParsedPath> path;
    private ArrayList<ParsedPath> revPath;
    
    public DC_Paths(){
        path = new ArrayList();
        revPath = new ArrayList();
    }

    public DC_Paths(String sourceNode, String destNode) {
        this.sourceNode = sourceNode;
        this.destNode = destNode;
    }

    public String getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(String sourceNode) {
        this.sourceNode = sourceNode;
    }

    public String getDestNode() {
        return destNode;
    }

    public void setDestNode(String destNode) {
        this.destNode = destNode;
    }

    public ArrayList<ParsedPath> getPath() {
        return path;
    }

    public void setPath(ArrayList<ParsedPath> path) {
        this.path = path;
    }

    public ArrayList<ParsedPath> getRevPath() {
        return revPath;
    }

    public void setRevPath(ArrayList<ParsedPath> revPath) {
        this.revPath = revPath;
    }
    
    
}
