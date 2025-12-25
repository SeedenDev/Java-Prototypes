package fr.seeden.gps;

import fr.seeden.gps.algorithm.IAlgorithm;
import fr.seeden.gps.graph.Graph;
import fr.seeden.gps.graph.Node;

import java.util.List;

public class GpsApi {

    private static IAlgorithm algorithm;

    public static void setAlgorithm(IAlgorithm inAlgorithm){
        algorithm = inAlgorithm;
    }
    public static IAlgorithm getAlgorithm() {
        return algorithm;
    }

    public static List<Node> findPathBetweenNodes(Graph graph, Node from, Node to){
        GpsThread gpsThread = new GpsThread(graph, from, to);
        gpsThread.run();
        return gpsThread.getResult();
    }
}