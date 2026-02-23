package fr.seeden.gps;

import fr.seeden.core.Application;
import fr.seeden.core.event.EMouseButton;
import fr.seeden.core.event.EventHandler;
import fr.seeden.core.event.EventListener;
import fr.seeden.core.event.MouseEvent;
import fr.seeden.core.window.AppKeybinding;
import fr.seeden.core.window.AppWindow;
import fr.seeden.core.window.WindowUtil;
import fr.seeden.gps.algorithm.AStar;
import fr.seeden.gps.algorithm.Algorithm;
import fr.seeden.gps.algorithm.Dijkstra;
import fr.seeden.gps.graph.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

public class GpsTestWindow extends AppWindow implements EventListener {

    enum DebugAlgorithm {
        ASTAR, DIJKSTRA;
    }
    private DebugAlgorithm debugAlgorithm = DebugAlgorithm.ASTAR;

    private final AppKeybinding.OnPressKeybinding switchAlgorithmKey = new AppKeybinding.OnPressKeybinding("switchAlgorithm", KeyEvent.VK_SPACE, () -> {
        int i = debugAlgorithm.ordinal()+1;
        if(i==DebugAlgorithm.values().length) i = 0;
        debugAlgorithm = DebugAlgorithm.values()[i];
        refreshWindow();
    });
    private final AppKeybinding.OnPressKeybinding resetKey = new AppKeybinding.OnPressKeybinding("reset", KeyEvent.VK_ENTER, () -> {
        generateGraph();
        reset();
    });

    private Graph graph;

    private boolean processStarted = false;
    // Precomputed edges when added to list for faster render (thread-safe because callback & render are concurrent)
    private final CopyOnWriteArraySet<Edge> visitedEdgeDebugSet = new CopyOnWriteArraySet<>();
    // Precomputed edges when the path is found
    private final HashSet<Edge> foundPathSet = new HashSet<>();

    private boolean placeStart = true;
    private GhostNode startNode, goalNode;
    private String resultText;

    private final boolean enableNodeDraw, enableEdgeDraw, enableNodeLabelDraw, enableEdgeWeightDraw, enableDebugEdgeDraw;
    private final int nodeCount, minNeighbour, maxNeighbour;

    public GpsTestWindow(Application mainApp, int windowWidth, int windowHeight, int nodeCount, int minNeighbour, int maxNeighbour, boolean enableNodeDraw, boolean enableEdgeDraw, boolean enableNodeLabelDraw, boolean enableEdgeWeightDraw, boolean enableDebugEdgeDraw) {
        super("Proto-GPS", windowWidth, windowHeight, true, mainApp);
        this.nodeCount = nodeCount;
        this.minNeighbour = minNeighbour;
        this.maxNeighbour = maxNeighbour;
        this.enableNodeDraw = enableNodeDraw;
        this.enableEdgeDraw = enableEdgeDraw;
        this.enableNodeLabelDraw = enableNodeLabelDraw;
        this.enableEdgeWeightDraw = enableEdgeWeightDraw;
        this.enableDebugEdgeDraw = enableDebugEdgeDraw;

        registerKeybindings(resetKey, switchAlgorithmKey);

        generateGraph();
        refreshWindow();
    }

    @Override
    public void render(Graphics g, double deltaTime) {
        super.render(g, deltaTime);

        if(enableNodeDraw){
            List<Node> nodes = this.graph.getNodes();
            for (Node node : nodes) {
                if(node instanceof GhostNode) continue;
                int x = (int) node.getX();
                int y = (int) node.getY();
                if(enableNodeLabelDraw) WindowUtil.drawCircleWithLabel(g, Color.GREEN, x, y, 6, "");
                else WindowUtil.drawCircle(g, Color.GREEN, x, y, 6);
            }
        }
        if(enableEdgeDraw){
            HashSet<Edge> edges = graph.getEdges();
            for (Edge edge : edges) {
                Node node1 = edge.getNode1();
                Node node2 = edge.getNode2();
                g.setColor(Color.BLUE);
                g.drawLine((int) node1.getX(), (int) node1.getY(), (int) node2.getX(), (int) node2.getY());
                int midX = (int) (node1.getX()+node2.getX())/2;
                int midY = (int) (node1.getY()+node2.getY())/2;
                if(!enableEdgeWeightDraw) continue;
                g.setColor(Color.RED);
                g.drawString(Double.toString(edge.getDistance()), midX, midY);
            }
        }
        // Redraw these edges above the other to be sure they are visible
        if(enableDebugEdgeDraw){
            for (Edge edge : visitedEdgeDebugSet) {
                g.setColor(Color.YELLOW);
                g.drawLine((int) edge.getNode1().getX(), (int) edge.getNode1().getY(), (int) edge.getNode2().getX(), (int) edge.getNode2().getY());
            }
        }
        for (Edge edge : foundPathSet) {
            g.setColor(Color.ORANGE);
            g.drawLine((int) edge.getNode1().getX(), (int) edge.getNode1().getY(), (int) edge.getNode2().getX(), (int) edge.getNode2().getY());
        }

        g.setColor(Color.BLACK);
        g.drawString("Algorithm: "+debugAlgorithm.name(), 5, getPanelHeight()-20);
        if(resultText!=null) g.drawString(resultText, 5, getPanelHeight()-5);

        if(startNode!=null) WindowUtil.drawCircle(g, Color.MAGENTA, (int) startNode.getX(), (int) startNode.getY(), 20);
        if(goalNode!=null) WindowUtil.drawCircle(g, Color.PINK, (int) goalNode.getX(), (int) goalNode.getY(), 20);
    }

    @EventHandler
    public void onMouseReleased(MouseEvent.MouseReleasedEvent event){
        if(processStarted) return;
        if (event.getClickedButton().equals(EMouseButton.LEFT)) {
            if(resultText!=null) reset();
            if(placeStart) {
                startNode = new GhostNode(event.getMouseX(), event.getMouseY());
                goalNode = null;
            }
            else goalNode = new GhostNode(event.getMouseX(), event.getMouseY());
            placeStart = !placeStart;
            refreshWindow();
        }
        else if(event.getClickedButton().equals(EMouseButton.MIDDLE)){
            reset();
        }
        else if(event.getClickedButton().equals(EMouseButton.RIGHT)){
            if(startNode==null || goalNode == null) {
                resultText = "Cannot proceed to path finding: you should select a start point and a goal.";
                return;
            }
            if(resultText!=null) {
                visitedEdgeDebugSet.clear();
                foundPathSet.clear();
            }
            // Launch path finding process
            List<Node> nodes = graph.getNodes();
            nodes.add(startNode);
            nodes.add(goalNode);

            refreshWindow();

            Algorithm algorithm = null;
            switch (debugAlgorithm) {
                case ASTAR -> algorithm = new AStar(graph);
                case DIJKSTRA -> algorithm = new Dijkstra(graph);
            }
            GpsService.setAlgorithm(algorithm);

            resultText = "From "+startNode+" to "+goalNode;
            processStarted = true;
            long startTime = System.currentTimeMillis();

            CompletableFuture<List<Node>> processFuture = GpsService.findPathBetweenNodes(startNode, goalNode,
                    (node1, node2) -> {
                        visitedEdgeDebugSet.add(new Edge(node1, node2, 0));
                        refreshWindow();
                    },
                    (path) -> {
                        long duration = System.currentTimeMillis() - startTime;
                        resultText = "Process took " + duration + "ms";
                        getLogger().debug("Path is: " + (path != null && !path.isEmpty() ? path : "not found."));

                        nodes.remove(startNode);
                        nodes.remove(goalNode);

                        if (path == null) return;

                        for (int i = 0; i < path.size() - 1; i++)
                            foundPathSet.add(new Edge(path.get(i), path.get(i + 1), 0));

                        refreshWindow();
                        processStarted = false;
                    }
            );
        }
    }

    @Override
    protected void onClose() {
        GpsService.shutdownServices();
    }

    private void reset(){
        this.processStarted = false;
        this.visitedEdgeDebugSet.clear();
        this.foundPathSet.clear();
        this.placeStart = true;
        this.startNode = this.goalNode = null;
        this.resultText = null;
        refreshWindow();
    }

    private void generateGraph(){
        int graphSizeX = getPanelWidth()-10, graphSizeY = getPanelHeight()-30;
        graph = GraphUtil.generateRandomGraph(graphSizeX, graphSizeY, nodeCount, minNeighbour, maxNeighbour);
        renameWindow(String.format("Proto-GPS - Size: %dx%d / Nodes: %d (%d-%d min-max neighbours/node) / %d edges (Average degree: %f - Density: %f)", graphSizeX, graphSizeY, nodeCount, minNeighbour, maxNeighbour, graph.getEdgeCount(), graph.getAverageDegree(), graph.getDensity()));
        refreshWindow();
    }
}