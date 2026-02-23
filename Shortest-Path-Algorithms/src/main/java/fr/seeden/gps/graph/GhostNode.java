package fr.seeden.gps.graph;

/**
 * A utility class for node to avoid computing in algorithms
 */
public class GhostNode extends Node {

    public GhostNode(float x, float y) {
        super(x, y);
    }
}
