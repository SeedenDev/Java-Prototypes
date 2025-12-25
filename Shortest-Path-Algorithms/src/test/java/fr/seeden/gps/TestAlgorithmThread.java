package fr.seeden.gps;

import fr.seeden.gps.algorithm.IAlgorithm;
import fr.seeden.gps.graph.Graph;
import fr.seeden.gps.graph.Node;
import fr.seeden.gps.window.DebugFrame;
import fr.seeden.gps.window.DebugPanel;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class TestAlgorithmThread implements Runnable {

    private final Node[][] nodeArray;
    private final IAlgorithm algorithm;
    private final DebugPanel panel;

    public TestAlgorithmThread(Graph graph, Node[][] nodeArray, Class<? extends IAlgorithm> algorithmClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DebugFrame frame = new DebugFrame(algorithmClass.getName());
        panel = new DebugPanel();
        frame.setContentPane(panel);
        panel.setRenderedGraph(graph);
        this.nodeArray = nodeArray;
        this.algorithm = algorithmClass.getConstructor(Graph.class).newInstance(graph);
    }

    @Override
    public void run() {
        panel.renderGraph();
        int n = nodeArray.length-1;
        final List<Long> testsDuration = new ArrayList<>();
        while(!Thread.currentThread().isInterrupted()){
            Node from = nodeArray[n][0];
            Node to = nodeArray[n][1];
            System.out.println("From"+ from+" to "+to);
            long start = System.currentTimeMillis();
            List<Node> path = algorithm.process(from, to);
            long duration = System.currentTimeMillis()-start;
            System.out.println(algorithm.getClass().getName()+"/Path found in "+duration+"ms : "+(path!=null ? path : "NOT FOUND"));
            testsDuration.add(duration);
            if(n--==0){
                Thread.currentThread().interrupt();
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        long s = 0;
        for (long l : testsDuration) {
            s += l;
        }
        System.out.println(algorithm.getClass().getName()+"/Average time to resolve: "+(s/testsDuration.size())+"ms");
    }
}