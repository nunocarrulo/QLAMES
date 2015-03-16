/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dijkstra;

import static Dijkstra.Dijkstra.computePaths;
import static Dijkstra.Dijkstra.getShortestPathTo;
import TopologyManagerImpl.NodeCon;
import TopologyManagerImpl.TopoNode;
import TopologyManagerUtils.Utils;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author nuno
 */
public class Launcher {
    
    public static List<Vertex> vertices = new ArrayList<>();
    public static final boolean debug = false;
    public static void runDijkstra(){
        Vertex vertex;
        Edge edge;
        int index = 0;
        boolean spf = true;
        int weight;
        
        if(spf)
            weight = 1;
        else
            weight = 25;
        
        /* Creating vertex */
        for(TopoNode tn : Utils.topo.getAllNodes()){
            vertex = new Vertex(tn.getId());    //create vertex 
            //System.out.println("TopoNode ID: "+tn.getId());
            vertices.add(vertex);
            //vertices[index++] = vertex;         //add vertex to list
        }
        
        /* Creating edges for each vertex*/
        for(TopoNode tn : Utils.topo.getAllNodes()){
            for(NodeCon nc : tn.getNodeCon()){
                System.out.println("TopoNode ID: "+tn.getId()+" Target: "+nc.getDstNodeId()+" from "+
                        nc.getFrom()+" to "+nc.getTo());
                Vertex lol = findVertex(tn.getId());
                if(lol == null){
                    System.out.println("Error, null Vertex.");
                    System.exit(0);
                }
                edge = new Edge(findVertex(nc.getDstNodeId()), nc.getFrom(), weight); //create edge
                lol.adjacencies.add(edge);  //adding edge to list
            }
        }
        if(debug){
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("Debugging vertex and edges.");
            for(Vertex v : vertices){
                System.out.println("Vertex "+v.name);
                for(Edge e : v.adjacencies)
                    System.out.println("\tTarget "+e.target+" via "+e.link);
            } 
        }
        
        //for(Vertex lol : vertices){
        System.out.println("-------------------------------------------------------------------------------------");
        Vertex src = null;
        Vertex target = null;
        for (Vertex vertice : vertices) {
            switch (vertice.name) {
                case "host:82:49:3f:36:87:a2":
                    src = vertice;
                    break;
                case "host:ba:f7:af:81:fe:f5":
                    target = vertice;
                    break;
            }
        }
        if(src == null || target == null)
        {
            System.out.println("Source or target were not found!Exiting...");
            System.exit(0);
        }
        computePaths(src);    
        System.out.println("Distance to " + target + ": " + target.minDistance);
        List<Vertex> path = getShortestPathTo(target);
        System.out.println("Path: " + path);
            /*computePaths(lol);
            System.out.println("Distance to " + v + ": " + v.minDistance);
                List<Vertex> path = getShortestPathTo(v);
                System.out.println("Path: " + path);
            
            //System.out.println("Node "+lol.name+" paths:");
            
            for (Vertex v : vertices) {
                System.out.println("Distance to " + v + ": " + v.minDistance);
                List<Vertex> path = getShortestPathTo(v);
                System.out.println("Path: " + path);
            }
        //}*/
    }
    
    /**
     * 
     * @param name Topology Node id
     * @return returns the vertex associated to the specified id (name)
     */
    public static Vertex findVertex(String name){
        for(Vertex v : vertices){
            if(v.name.equals(name))
                return v;
        }
        
        return null;
    }
   
}
