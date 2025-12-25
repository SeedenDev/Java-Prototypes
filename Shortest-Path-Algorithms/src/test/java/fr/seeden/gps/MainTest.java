package fr.seeden.gps;

import fr.seeden.gps.graph.Graph;
import fr.seeden.gps.graph.GraphUtil;

public class MainTest {

    public static void main(String[] args) {
        // Generate random graph
        final Graph graph = GraphUtil.generateRandomGraph(700, 700, 50, 5);
        // Test algorithms
        fr.seeden.gps.GpsDebug.setDebug(true);
        fr.seeden.gps.GpsDebug.initFrameAndPanel(graph);
        fr.seeden.gps.GpsDebug.debugRenderPanel();

        //List<Node> path = GpsApi.findPathBetweenNodes(graph, null, null);

        /*
        final Node[][] nodeArray = GraphUtil.getRandomNodesPair(graph, 50);
        try {
            Thread aStarThread = new Thread(new TestAlgorithmThread(graph, nodeArray, AStar.class));
            aStarThread.start();
            Thread dijkstraThread = new Thread(new TestAlgorithmThread(graph, nodeArray, Dijkstra.class));
            dijkstraThread.start();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }*/
    }
}