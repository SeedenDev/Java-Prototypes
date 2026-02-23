package fr.seeden.gps.graph;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Graph {

    //TODO: Graph optimization(the list mhm..) & serialisation in the API

    private final int sizeX, sizeY;
    private final CopyOnWriteArrayList<Node> nodes;
    private double averageDegree, density;
    // Used for drawing graph only. Precomputed here to avoid losing time each time window refreshes.
    private final HashSet<Edge> edges;
    private int nodeCount, edgeCount;

    public Graph(int sizeX, int sizeY, Node... nodes){
        this(sizeX, sizeY, new CopyOnWriteArrayList<>(nodes));
    }
    public Graph(int sizeX, int sizeY, List<Node> nodes){
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.nodes = new CopyOnWriteArrayList<>(nodes);
        this.edges = new HashSet<>();

        int totalNeighbours = 0;
        for (Node node : nodes) {
            totalNeighbours += node.getNeighbours().size();
            for (Map.Entry<Node, Double> entry : node.getNeighbours().entrySet()) {
                edges.add(new Edge(node, entry.getKey(), entry.getValue()));
            }
        }
        this.averageDegree = (double) totalNeighbours / nodes.size();
        this.nodeCount = nodes.size();
        this.edgeCount = edges.size();
        this.density = edgeCount * 2.0 / (nodeCount * (nodeCount-1));
    }

    public void recomputeGraphInfo(){
        this.edges.clear();
        int totalNeighbours = 0;
        for (Node node : nodes) {
            totalNeighbours += node.getNeighbours().size();
            for (Map.Entry<Node, Double> entry : node.getNeighbours().entrySet()) {
                edges.add(new Edge(node, entry.getKey(), entry.getValue()));
            }
        }
        this.averageDegree = (double) totalNeighbours / nodes.size();
        this.nodeCount = nodes.size();
        this.edgeCount = edges.size();
        this.density = edgeCount * 2.0 / (nodeCount * (nodeCount-1));
    }

    public double getAverageDegree() {
        return averageDegree;
    }

    public int getEdgeCount() {
        return edgeCount;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public double getDensity() {
        return density;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public HashSet<Edge> getEdges() {
        return edges;
    }
}