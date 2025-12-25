package fr.seeden.gps.algorithm;

import fr.seeden.gps.graph.Graph;
import fr.seeden.gps.graph.Node;

import java.util.List;

public abstract class IAlgorithm {

    protected final Graph graph;

    public IAlgorithm(Graph graph) {
        this.graph = graph;
    }

    public abstract List<Node> process(Node start, Node goal);
}