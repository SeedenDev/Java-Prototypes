package fr.seeden.gps.algorithm;

import fr.seeden.gps.graph.GhostNode;
import fr.seeden.gps.graph.Graph;
import fr.seeden.gps.graph.Node;

import java.util.*;

public class Dijkstra extends Algorithm {

    //TODO: maybe try to optimize Dijkstra

    private Node goal;

    public Dijkstra(Graph graph) {
        super(graph);
    }

    private List<Node> reconstructPath(HashMap<Node, Node> prev, Node current){
        ArrayList<Node> totalPath = new ArrayList<>(Collections.singletonList(current));
        while(prev.containsKey(current)) {
            current = prev.get(current);
            totalPath.addFirst(current);
        }
        return totalPath;
    }

    @Override
    public List<Node> process(Node start, Node goal, VisitDebugCallback visitDebugCallback) {
        this.goal = goal;

        List<Node> nodes = graph.getNodes();
        HashMap<Node, Integer> dist = new HashMap<>();
        HashMap<Node, Node> prev = new HashMap<>();
        List<Node> Q = new ArrayList<>(nodes);
        dist.put(start, 0);

        HashSet<Node> goalSet = new HashSet<>();
        goalSet.add(goal);
        if(goal instanceof GhostNode){
            for (Map.Entry<Node, Double> entry : goal.getNeighbours().entrySet()) {
                goalSet.add(entry.getKey());
                //entry.getKey().addNeighbours(goal);
            }
        }

        while (!Q.isEmpty()) {
            Node current = getMinimumDistNode(Q, dist);

            if (goalSet.contains(current)) return reconstructPath(prev, goal);

            Q.remove(current);
            for (Map.Entry<Node, Double> entry : current.getNeighbours().entrySet()) {
                Node neighbour = entry.getKey();
                if(neighbour instanceof GhostNode) continue;

                visitDebugCallback.send(current, neighbour);

                int alt = dist.getOrDefault(current, 0) + entry.getValue().intValue();
                if(alt<dist.getOrDefault(neighbour, Integer.MAX_VALUE)){
                    dist.put(neighbour, alt);
                    prev.put(neighbour, current);
                }
            }
        }
        return new ArrayList<>();
    }

    private Node getMinimumDistNode(List<Node> Q, HashMap<Node, Integer> dist){
        Node minNode = null;
        int minDist = Integer.MAX_VALUE;
        for (Node node : Q) {
            if(dist.getOrDefault(node, Integer.MAX_VALUE)<minDist){
                minDist = dist.get(node);
                minNode = node;
            }
        }
        return minNode;
    }
}