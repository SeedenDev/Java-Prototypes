package fr.seeden.gps;

import fr.seeden.gps.algorithm.IAlgorithm;
import fr.seeden.gps.graph.Graph;
import fr.seeden.gps.graph.Node;

import java.util.List;

public class GpsThread implements Runnable {

    private final Graph graph;
    private final Node from, to;

    protected GpsThread(Graph graph, Node from, Node to) {
        this.graph = graph;
        this.from = from;
        this.to = to;
    }

    private List<Node> path;

    @Override
    public void run() {
        if(GpsDebug.isDebugEnabled()){
            System.out.println("From" + from + " to " + to);
            GpsDebug.startTimer();
        }
        IAlgorithm algorithm = GpsApi.getAlgorithm();
        this.path = algorithm.process(from, to);
        if(GpsDebug.isDebugEnabled()) {
            long duration = GpsDebug.endTimer();
            String algoName = algorithm.getClass().getName();
            String debugText = algoName.substring(algoName.lastIndexOf(".")) + "/Path found in " + duration + "ms";
            System.out.println(debugText+" : " + (path != null ? path : "NOT FOUND"));
            GpsDebug.debugPanelAddText(debugText);
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            GpsDebug.refreshPanel();
        }
    }

    public List<Node> getResult() {
        return this.path;
    }
}
