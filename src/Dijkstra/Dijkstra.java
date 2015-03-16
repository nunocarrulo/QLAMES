/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dijkstra;

import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author nuno
 */
class Vertex implements Comparable<Vertex> {

    public final String name;
    public List<Edge> adjacencies;
    public int minDistance = Integer.MAX_VALUE;// .POSITIVE_INFINITY;
    public Vertex previous;
    private int index = 0;

    public Vertex(String argName) {
        name = argName;
        adjacencies = new ArrayList<>();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Vertex other) {
        return Double.compare(minDistance, other.minDistance);
    }
}

class Edge {

    public final Vertex target;
    public final String link;
    public final int weight;

    public Edge(Vertex argTarget, String argLink, int argWeight) {
        target = argTarget;
        link = argLink;
        weight = argWeight;
    }
    @Override
    public String toString(){
        return ("target: "+target+" through "+link);
    }
}

public class Dijkstra {

    public static void computePaths(Vertex source) {
        source.minDistance = 0;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
        vertexQueue.add(source);

        while (!vertexQueue.isEmpty()) {
            Vertex u = vertexQueue.poll();

            // Visit each edge exiting u
            for (Edge e : u.adjacencies) {
                Vertex v = e.target;
                int weight = e.weight;
                int distanceThroughU = u.minDistance + weight;
                System.out.println("From "+u.name+" to "+e.toString()+"\t"+distanceThroughU);
                if (distanceThroughU < v.minDistance) {
                    vertexQueue.remove(v);
                    v.minDistance = distanceThroughU;
                    v.previous = u;
                    vertexQueue.add(v);
                }
            }
        }
    }

    public static List<Vertex> getShortestPathTo(Vertex target) {
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous) {
            path.add(vertex);
        }
        Collections.reverse(path);
        return path;
    }
}