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
public class DijkstraOps {

    private static List<Vertex> vertices = new ArrayList<>();
    private static final List<ParsedPath> parsedPath = new ArrayList<>();
    private static final List<ParsedPath> revParsedPath = new ArrayList<>();   //reversed path
    private static final boolean debug = false;
    private static ParsedPath pp;

    public static void runDijkstra() {
        Vertex vertex;
        Edge edge;
        int index = 0;
        boolean spf = true;
        int weight;

        if (spf) {
            weight = 1;
        } else {
            weight = 25;
        }

        /* Creating vertex */
        for (TopoNode tn : Utils.topo.getAllNodes()) {
            vertex = new Vertex(tn.getId());    //create vertex 
            //System.out.println("TopoNode ID: "+tn.getId());
            vertices.add(vertex);
            //vertices[index++] = vertex;         //add vertex to list
        }

        /* Creating edges for each vertex*/
        for (TopoNode tn : Utils.topo.getAllNodes()) {
            for (NodeCon nc : tn.getNodeCon()) {
                System.out.println("TopoNode ID: " + tn.getId() + " Target: " + nc.getDstNodeId() + " from "
                        + nc.getFrom() + " to " + nc.getTo());
                Vertex lol = findVertex(tn.getId());
                if (lol == null) {
                    System.out.println("Error, null Vertex.");
                    System.exit(0);
                }
                edge = new Edge(findVertex(nc.getDstNodeId()), nc.getFrom(), weight); //create edge
                lol.adjacencies.add(edge);  //adding edge to list
            }
        }
        if (debug) {
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("Debugging vertex and edges.");
            for (Vertex v : vertices) {
                System.out.println("Vertex " + v.name);
                for (Edge e : v.adjacencies) {
                    System.out.println("\tTarget " + e.target + " via " + e.link);
                }
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
        if (src == null || target == null) {
            System.out.println("Source or target were not found!Exiting...");
            System.exit(0);
        }
        computePaths(src);
        System.out.println("Distance to " + target + ": " + target.minDistance);
        List<Vertex> path = getShortestPathTo(target);
        System.out.println("Path: " + path);

    }

    public static void createGraph() {
        Vertex vertex;
        Edge edge;
        int index = 0;
        boolean spf = true;
        int weight;

        if (spf) {
            weight = 1;
        } else {
            weight = 25;
        }

        /* Creating vertex */
        for (TopoNode tn : Utils.topo.getAllNodes()) {
            vertex = new Vertex(tn.getId());    //create vertex 
            //System.out.println("TopoNode ID: "+tn.getId());
            vertices.add(vertex);
            //vertices[index++] = vertex;         //add vertex to list
        }

        /* Creating edges for each vertex*/
        for (TopoNode tn : Utils.topo.getAllNodes()) {
            for (NodeCon nc : tn.getNodeCon()) {
                System.out.println("TopoNode ID: " + tn.getId() + " Target: " + nc.getDstNodeId() + " from "
                        + nc.getFrom() + " to " + nc.getTo());
                Vertex lol = findVertex(tn.getId());
                if (lol == null) {
                    System.out.println("Error, null Vertex.");
                    System.exit(0);
                }
                edge = new Edge(findVertex(nc.getDstNodeId()), nc.getFrom(), weight); //create edge
                lol.adjacencies.add(edge);  //adding edge to list
            }
        }
        if (debug) {
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("Debugging vertex and edges.");
            for (Vertex v : vertices) {
                System.out.println("Vertex " + v.name);
                for (Edge e : v.adjacencies) {
                    System.out.println("\tTarget " + e.target + " via " + e.link);
                }
            }
        }
    }

    public static List<Vertex> findPath(String source, String dest) {
        if (debug) {
            System.out.println("-------------------------------------------------------------------------------------");
        }
        Vertex src = null;
        Vertex target = null;
        for (Vertex vertice : vertices) {
            if (Utils.topo.getHostByIP(source).equals(vertice.name)) {
                src = vertice;
            } else if (Utils.topo.getHostByIP(dest).equals(vertice.name)) {
                target = vertice;
            }

        }
        if (src == null || target == null) {
            System.out.println("Source or target were not found! Exiting...");
            System.exit(0);
        }
        computePaths(src);
        if (debug) {
            System.out.println("Distance to " + target + ": " + target.minDistance);
        }

        List<Vertex> path = getShortestPathTo(target);  //find shortest path

        if (true) {
            System.out.println("Path: " + path);
        }
        // decoding of the path and reverse path has "Switch <swID> to <target switch id> via <portID>" 
        decodeVertexPath(path);

        return path;
    }

    /**
     *
     * @param name Topology Node id
     * @return returns the vertex associated to the specified id (name)
     */
    public static Vertex findVertex(String name) {
        for (Vertex v : vertices) {
            if (v.name.equals(name)) {
                return v;
            }
        }

        return null;
    }

    public static void decodeVertexPath(List<Vertex> vertexPath) {
        //clean up list and parsedpath object
        parsedPath.clear();
        revParsedPath.clear();
        
        String target, rawFrom, from;

        // get sw path
        for (Vertex v : vertexPath) {
            pp = new ParsedPath();
            pp.setSwID(v.name);
            parsedPath.add(pp);
        }
        // get sw rev path
        for (int i = parsedPath.size() - 1; i >= 0; i--) {
            pp = new ParsedPath();
            pp.setSwID(parsedPath.get(i).getSwID());
            revParsedPath.add(pp);
        }
        
        // Find ports on up path
        for (int i = 1; i < parsedPath.size() - 1; i++) {
            rawFrom = Utils.topo.getNode(parsedPath.get(i).getSwID()).getNodeConFromPortByTarget(parsedPath.get(i + 1).getSwID());
            from = (rawFrom.split(":"))[2];
            parsedPath.get(i).setPortNumber(from);
        }
        
        // Find ports on down path
        for (int i = 1; i < revParsedPath.size() - 1; i++) {
            rawFrom = Utils.topo.getNode(revParsedPath.get(i).getSwID()).getNodeConFromPortByTarget(revParsedPath.get(i + 1).getSwID());
            from = (rawFrom.split(":"))[2];
            revParsedPath.get(i).setPortNumber(from);
        }
        //Delete first and last entries because nothing is installed on hosts (neither queues or flows)
        parsedPath.remove(0); //parsedPath.remove(parsedPath.size()-1);
        revParsedPath.remove(0); //revParsedPath.remove(revParsedPath.size()-1);

        if (true) {
            System.out.println("Up Path:");
            for (int i = 0; i < parsedPath.size() - 1; i++) {
                System.out.println("SwID " + parsedPath.get(i).getSwID() + " to target " + parsedPath.get(i + 1).getSwID() + " via " + parsedPath.get(i).getPortNumber());
            }
            System.out.println("\nDown Path (Reverse):");
            for (int i = 0; i < revParsedPath.size() - 1; i++) {
                System.out.println("SwID " + revParsedPath.get(i).getSwID() + " to target " + revParsedPath.get(i + 1).getSwID() + " via " + revParsedPath.get(i).getPortNumber());
            }
        }

        System.out.println("Parsed Path done!");
    }

    public static List<ParsedPath> getParsedPath() {
        return parsedPath;
    }

    public static List<ParsedPath> getRevParsedPath() {
        return revParsedPath;
    }

}
