package fr.seeden.gps.algorithm;

import fr.seeden.gps.graph.Graph;
import fr.seeden.gps.graph.Node;

import java.util.List;

public abstract class Algorithm {

    protected Graph graph;

    public Algorithm(Graph graph) {
        this.graph = graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    /**
     * Process the algorithm to found a path from the {@code start} node to the {@code goal} node.
     * @param start The node to start from
     * @param goal The node to go to
     * @param visitDebugCallback A debug callback for my own test as a prototype project (see usage in GpsTestWindow)
     * @return The found path or an empty list if no path found. Or null if the process timed-out.
     */
    public abstract List<Node> process(Node start, Node goal, VisitDebugCallback visitDebugCallback);

    public interface VisitDebugCallback {
        void send(Node node1, Node node2);
    }
}