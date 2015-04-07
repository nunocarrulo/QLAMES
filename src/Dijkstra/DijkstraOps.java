/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dijkstra;

import static Dijkstra.Dijkstra.computePaths;
import static Dijkstra.Dijkstra.getShortestPathTo;
import static REST_Requests.Constants.applyLoadBalance;
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

    private static final List<Vertex> vertices = new ArrayList<>();
    private static List<ParsedPath> parsedPath = new ArrayList<>();
    private static List<ParsedPath> revParsedPath = new ArrayList<>();   //reversed path
    private static ArrayList<DC_Paths> dcPaths = new ArrayList<>();
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
        int weight = 1;

        /* Creating vertex */
        for (TopoNode tn : Utils.topo.getAllNodes()) {
            vertex = new Vertex(tn.getId());    //create vertex 
            //System.out.println("TopoNode ID: "+tn.getId());
            vertices.add(vertex);
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
        boolean foundSrc = false;
        boolean foundDst = false;

        for (Vertex vertice : vertices) {
            System.out.println("DijktraOps: Vertice=" + vertice.name + " Source= " + Utils.topo.getHostByIP(source) + " target " + Utils.topo.getHostByIP(dest));

            if (Utils.topo.getHostByIP(source).equals(vertice.name)) {
                src = vertice;
                foundSrc = true;
            } else if (Utils.topo.getHostByIP(dest).equals(vertice.name)) {
                target = vertice;
                foundDst = true;
            }
            if (foundSrc && foundDst) {
                break;
            }

        }
        if (src == null || target == null) {
            System.out.println("Source or target were not found! Exiting...");
            System.exit(0);
        }
        System.out.println("Computing paths");
        computePaths(src);
        if (true) {
            System.out.println("Distance to " + target + ": " + target.minDistance);
        }

        List<Vertex> path = getShortestPathTo(target);  //find shortest path

        if (debug) {
            System.out.println("Path: " + path);
        }

        cleanVertex();

        // decoding of the path and reverse path has "Switch <swID> to <target switch id> via <portID>" 
        decodeVertexPath(path, source, dest);

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

    public static void decodeVertexPath(List<Vertex> vertexPath, String source, String dest) {
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
        parsedPath.remove(parsedPath.size() - 1);
        revParsedPath.remove(revParsedPath.size() - 1);

        // Add path to dcpaths
        if (!applyLoadBalance) {
            System.out.println("Adding new Path");
            DC_Paths dcpaths = new DC_Paths();
            dcpaths.setSourceNode(source);
            dcpaths.setDestNode(dest);
            ArrayList<ParsedPath> up = new ArrayList<>();
            ArrayList<ParsedPath> down = new ArrayList<>();   //reversed path
            ParsedPath aux;
            for (int i = 0; i < parsedPath.size(); i++) {
                //parsed path copy
                aux = new ParsedPath();
                aux.setSwID(parsedPath.get(i).getSwID());
                aux.setPortNumber(parsedPath.get(i).getPortNumber());
                up.add(aux);
            }
            for (int i = 0; i < revParsedPath.size(); i++) {
                //parsed path copy
                aux = new ParsedPath();
                aux.setSwID(revParsedPath.get(i).getSwID());
                aux.setPortNumber(revParsedPath.get(i).getPortNumber());
                down.add(aux);
            }
            dcpaths.setPath(up);
            dcpaths.setRevPath(down);
            dcPaths.add(dcpaths);
        }
        System.out.println("Parsed Path done!");
    }

    public static void getExistingPath(String source, String dest){
        for(DC_Paths dcp : dcPaths){
            if(dcp.getDestNode().equals(dest) && dcp.getSourceNode().equals(source)){
                parsedPath = dcp.getPath();
                revParsedPath = dcp.getRevPath();
                System.out.println("Found Existing Path");
                return;
            }else if(dcp.getDestNode().equals(source) && dcp.getSourceNode().equals(dest)){
                revParsedPath = dcp.getPath();
                parsedPath = dcp.getRevPath();
                System.out.println("Found Existing Path upside down");
                return;
            }
        }
        System.out.println("There are no existing paths between those nodes. Searching using Dijkstra");
        findPath(source, dest);
    }
    
    public static List<ParsedPath> getParsedPath() {
        return parsedPath;
    }

    public static List<ParsedPath> getRevParsedPath() {
        return revParsedPath;
    }

    public static void updateEdge(String swID, String portID, int weight) {
        for (Vertex v : vertices) {
            if (v.name.equals(swID)) {
                for (Edge e : v.adjacencies) {
                    if (e.link.equals(portID)) {
                        System.out.println("Edge found and updated");
                        e.weight = weight;
                    }
                }
            }
        }
    }

    public synchronized static void cleanVertex() {
        for (Vertex v : vertices) {
            v.minDistance = Integer.MAX_VALUE;
            v.previous = null;
        }
    }

    public static void updateAllEdges() {
        for (Vertex v : vertices) {
            for (Edge e : v.adjacencies) {
                e.weight = Utils.topo.getNode(v.name).getPort(e.link).getCurrLoad();    //average after
                System.out.println("Node " + v.name + " port " + e.link + " current load " + e.weight);
            }
        }
    }

}
