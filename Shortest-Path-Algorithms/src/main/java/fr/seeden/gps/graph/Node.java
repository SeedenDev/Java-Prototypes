package fr.seeden.gps.graph;

import java.util.HashMap;
import java.util.Objects;

public class Node {

    private final float x, y;
    private final HashMap<Node, Double> neighbours = new HashMap<>();

    public Node(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void addNeighbours(Node... nodes){
        for (Node node : nodes) {
            this.neighbours.put(node, getDistanceFrom(node));
        }
    }

    public void removeNeighbours(Node... nodes){
        for (Node node : nodes) {
            this.neighbours.remove(node);
        }
    }

    public double getDistanceFrom(Node node){
        return Math.sqrt(Math.pow(Math.abs(this.x-node.getX()), 2) + Math.pow(Math.abs(this.y-node.getY()), 2));
    }

    public boolean isANeighbour(Node node){
        return neighbours.containsKey(node);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public HashMap<Node, Double> getNeighbours() {
        return neighbours;
    }

    @Override
    public String toString() {
        return "Node= x:"+this.x+" ; y:"+this.y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Float.compare(x, node.x) == 0 && Float.compare(y, node.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}