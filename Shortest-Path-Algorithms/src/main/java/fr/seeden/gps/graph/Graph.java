package fr.seeden.gps.graph;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Graph {

    //TODO: Graph optimization(the list mhm..) & serialisation in the API

    private final int sizeX, sizeY;
    private final CopyOnWriteArrayList<Node> nodes;
    private final double averageDegree;
    // Used for drawing graph only. Precomputed here to avoid losing time each time window refreshes.
    private final HashSet<Edge> edges;

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
    }

    public double getAverageDegree() {
        return averageDegree;
    }

    public int getTotalEdges() {
        return edges.size();
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public HashSet<Edge> getEdges() {
        return edges;
    }
}