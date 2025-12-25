package fr.seeden.gps.algorithm;

import fr.seeden.gps.GpsDebug;
import fr.seeden.gps.graph.Graph;
import fr.seeden.gps.graph.Node;

import java.util.*;

public class AStar extends IAlgorithm {

    private Node goal;

    public AStar(Graph graph) {
        super(graph);
    }

    private List<Node> reconstructPath(HashMap<Node, Node> cameFrom, Node current){
        ArrayList<Node> totalPath = new ArrayList<>(Collections.singletonList(current));
        GpsDebug.debugRenderPanel();
        while(cameFrom.containsKey(current)) {
            GpsDebug.debugPanelDrawFinalLine(current, cameFrom.get(current));
            current = cameFrom.get(current);
            totalPath.addFirst(current);
        }
        GpsDebug.debugPanelDrawStartAndEndPoint(current, goal);
        return totalPath;
    }

    @Override
    public List<Node> process(Node start, Node goal){
        this.goal = goal;
        GpsDebug.debugPanelDrawStartAndEndPoint(start, goal);

        List<Node> openSet = new java.util.ArrayList<>(List.of(start));
        HashMap<Node, Node> cameFrom = new HashMap<>();

        HashMap<Node, Integer> gScore = new HashMap<>();
        gScore.put(start, 0);

        HashMap<Node, Integer> fScore = new HashMap<>();
        fScore.put(start, h(start));

        while (!openSet.isEmpty()) {
            Node current = getLowestFScoreNode(openSet, fScore); // the node in openSet having the lowest fScore[] value
            if (current==goal) {
                return reconstructPath(cameFrom, current);
            }
            openSet.remove(current);
            for (Map.Entry<Node, Double> entry : current.getNeighbours().entrySet()) {
                if(GpsDebug.isDebugEnabled()){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                Node neighbour = entry.getKey();
                GpsDebug.debugPanelDrawAlgorithmLine(current, neighbour);
                int tentative_gScore = gScore.get(current) + entry.getValue().intValue();
                if(tentative_gScore < gScore.getOrDefault(neighbour, Integer.MAX_VALUE)){
                    cameFrom.put(neighbour, current);
                    gScore.put(neighbour, tentative_gScore);
                    fScore.put(neighbour, tentative_gScore+h(neighbour));
                    if (!openSet.contains(neighbour)) {
                        openSet.add(neighbour);
                    }
                }
            }
        }
        return null; // goal was neved reached
    }

    private Node getLowestFScoreNode(List<Node> openSet, HashMap<Node, Integer> fScore) {
        int minF = Integer.MAX_VALUE;
        Node fNode = null;
        for (Node node : openSet) {
            if(fScore.getOrDefault(node, Integer.MAX_VALUE)<minF){
                minF = fScore.get(node);
                fNode = node;
            }
        }
        return fNode;
    }

    private int h(Node node){
        return (int) node.getDistanceFrom(this.goal);
    }
}