package fr.seeden.gps.graph;

import java.util.Objects;

public class Edge {

    private final Node node1, node2;
    private final double distance;

    public Edge(Node node1, Node node2, double distance) {
        this.node1 = node1.hashCode() < node2.hashCode() ? node1 : node2;
        this.node2 = node1.hashCode() < node2.hashCode() ? node2 : node1;
        this.distance = distance;
    }

    public Node getNode1() {
        return node1;
    }

    public Node getNode2() {
        return node2;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return (Objects.equals(node1, edge.node1) && Objects.equals(node2, edge.node2)) ||
                (Objects.equals(node1, edge.node2) && Objects.equals(node2, edge.node1));
    }

    @Override
    public int hashCode() {
        return Objects.hash(node1.hashCode(), node2.hashCode());
    }
}