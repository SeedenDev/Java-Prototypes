package fr.seeden.gps.graph;

import java.util.Arrays;
import java.util.List;

public class Graph {

    private final int sizeX, sizeY;
    private final List<Node> nodes;

    public Graph(int sizeX, int sizeY, Node... nodes){
        this(sizeX, sizeY, Arrays.asList(nodes));
    }
    public Graph(int sizeX, int sizeY, List<Node> nodes){
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.nodes = nodes;
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
}