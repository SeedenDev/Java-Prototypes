package fr.seeden.gps.graph;

import java.util.*;

public class GraphUtil {

    public static Graph generateRandomGraph(int sizeX, int sizeY, int nodeCount, int minNeighbour, int maxNeighbour){
        Node[] nodes = new Node[nodeCount];
        int[] targetNeighbourCount = new int[nodeCount];
        Random r = new Random();
        for (int i = 0; i < nodeCount; i++) {
            int x = r.nextInt(10, sizeX-10);
            int y = r.nextInt(10, sizeY-10);
            nodes[i] = new Node(x, y);
            targetNeighbourCount[i] = r.nextInt(minNeighbour, maxNeighbour);
        }
        for (int i = 0; i < nodeCount; i++) {
            Node node = nodes[i];
            int remainingNeighbourCount = targetNeighbourCount[i] - node.getNeighbours().size();
            int attempt = r.nextInt(100, 1000);
            for (int n = 0; n < remainingNeighbourCount && --attempt > 0; n++) {
                int j = r.nextInt(nodeCount);
                Node neighbour = nodes[j];
                if(neighbour.getNeighbours().size()==targetNeighbourCount[j] || node.isANeighbour(neighbour) || node.equals(neighbour)){
                    n--;
                    continue;
                }
                node.addNeighbours(neighbour);
                neighbour.addNeighbours(node);
            }
        }
        return new Graph(sizeX, sizeY, nodes);
    }

    public static Node[][] getRandomNodesPair(Graph graph, int n){
        Node[][] nodeArray = new Node[n][2];
        List<Node> nodes = graph.getNodes();
        Random r = new Random();
        for (int i = 0; i < n; i++) {
            Node from = nodes.get(r.nextInt(0, nodes.size()));
            Node to;
            do {
                to = nodes.get(r.nextInt(0, nodes.size()));
            }
            while(to==from);
            nodeArray[i][0] = from;
            nodeArray[i][1] = to;
        }
        return nodeArray;
    }


    //TODO: optimize?
    public static List<Node> findClosestNodesTo(float x, float y, float maxDistance, float maxCount, List<Node> nodes){
        List<ClosestNodeEntry> closestNodes = new ArrayList<>();
        for (Node node : nodes) {
            if(node instanceof GhostNode) continue;
            float nX = node.getX();
            float nY = node.getY();
            double dist = Math.sqrt(Math.pow(Math.abs(nX-x), 2) + Math.pow(Math.abs(nY-y), 2));
            if(dist>maxDistance) continue;

            boolean add = true;
            if(closestNodes.size()>=maxCount){
                int furthestIndex = -1;
                double furthestDistance = dist;
                for (int i = 0; i < closestNodes.size(); i++) {
                    ClosestNodeEntry nodeEntry = closestNodes.get(i);
                    if(nodeEntry.distance>furthestDistance){
                        furthestIndex = i;
                        furthestDistance = nodeEntry.distance;
                    }
                }
                if(furthestIndex!=-1) closestNodes.remove(furthestIndex);
                else add = false;
            }
            if(add) closestNodes.add(new ClosestNodeEntry(node, dist));
        }
        return closestNodes.stream().map(entry -> entry.linkedNode).toList();
    }

    private static class ClosestNodeEntry {
        private final Node linkedNode;
        private final double distance;

        private ClosestNodeEntry(Node linkedNode, double distance) {
            this.linkedNode = linkedNode;
            this.distance = distance;
        }
    }
}