/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TopologyManagerImpl;

import ITopologyManager.ITopology;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nuno
 */
public class Topology implements ITopology{

    private List<TopoNode> nodes;
    private final boolean debug = true;
    
    public Topology(){
        nodes = new ArrayList<>();
    }
    
    @Override
    public TopoNode getNode(String nodeID) {

        for(int i = 0; i < nodes.size(); i++)
           if(nodes.get(i).getId().equals(nodeID))
               return nodes.get(i);
        System.out.println("Node with id "+nodeID+" was not found!");
        return null;
    }

    @Override
    public TopoNode getNodeByName(String nodeName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addNode(TopoNode node) {
        nodes.add(node);
        if(debug)
            System.out.println("Node: "+ node.getId()+ " added successfully");
    }

    @Override
    public List<TopoNode> getAllNodes() {
        return nodes;
    }

    @Override
    public boolean nodeExists(String nodeid) {
        for(int i = 0; i < nodes.size(); i++)
            if(nodes.get(i).getId().equals(nodeid))
                return true;
        return false;
    }
    
    @Override
    public void clearTopo() {
        nodes.clear();
    }

    @Override
    public void printNodesIds() {
        String print = "Nodes on topology: \n";
        for(int i = 0; i < nodes.size(); i++)
            print+=nodes.get(i).getId()+" ";
        System.out.println(print);
    }

    @Override
    public void printNodes() {
        String print = "Nodes on topology (Full Info): \n";
        for(int i = 0; i < nodes.size(); i++)
            print+=nodes.get(i).toString()+"\n";
        System.out.println(print);
    }
    
}
