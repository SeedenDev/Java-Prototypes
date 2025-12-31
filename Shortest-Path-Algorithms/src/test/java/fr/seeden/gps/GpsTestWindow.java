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
import fr.seeden.gps.graph.Edge;
import fr.seeden.gps.graph.Graph;
import fr.seeden.gps.graph.GraphUtil;
import fr.seeden.gps.graph.Node;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

public class GpsTestWindow extends AppWindow implements EventListener {

    //TODO: optimize the "find neighbours" code + add it to the API ("connectToNearbyNodes()" but not altering the true nodes list)

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
    private Node startNode, goalNode;
    private String resultText;

    private final boolean enableNodeDraw, enableEdgeDraw, enableNodeLabelDraw, enableEdgeWeightDraw, enableDebugEdgeDraw;
    private final int nodeNb, minNeighbour, maxNeighbour;

    public GpsTestWindow(Application mainApp, int nodeNb, int minNeighbour, int maxNeighbour, boolean enableNodeDraw, boolean enableEdgeDraw, boolean enableNodeLabelDraw, boolean enableEdgeWeightDraw, boolean enableDebugEdgeDraw) {
        super("Proto-GPS", 600, 600, true, mainApp);
        this.nodeNb = nodeNb;
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
                startNode = new Node(event.getMouseX(), event.getMouseY());
                goalNode = null;
            }
            else goalNode = new Node(event.getMouseX(), event.getMouseY());
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
            // Find neighbours to link with
            final double MAX_NEIGHBOUR_DISTANCE = 70;
            List<Node> distanceFrom = new ArrayList<>();
            List<Node> distanceTo = new ArrayList<>();

            List<Node> nodes = graph.getNodes();
            for (Node node : nodes) {
                double distFrom = node.getDistanceFrom(startNode);
                double distTo = node.getDistanceFrom(goalNode);
                if(distFrom<=MAX_NEIGHBOUR_DISTANCE) {
                    if (distanceFrom.size() >= 5) {
                        int furthestIndex = 0;
                        double furthestDistance = 0;
                        for (int i = 0; i < distanceFrom.size(); i++) {
                            double d = distanceFrom.get(i).getDistanceFrom(startNode);
                            if (d > furthestDistance) {
                                furthestIndex = i;
                                furthestDistance = d;
                            }
                        }
                        if (distFrom < furthestDistance) {
                            distanceFrom.remove(furthestIndex);
                            distanceFrom.add(furthestIndex, node);
                        }
                    } else distanceFrom.add(node);
                }
                if(distTo<=MAX_NEIGHBOUR_DISTANCE) {
                    if (distanceTo.size() >= 5) {
                        int furthestIndex = 0;
                        double furthestDistance = 0;
                        for (int i = 0; i < distanceTo.size(); i++) {
                            double d = distanceTo.get(i).getDistanceFrom(goalNode);
                            if (d > furthestDistance) {
                                furthestIndex = i;
                                furthestDistance = d;
                            }
                        }
                        if (distTo < furthestDistance) {
                            distanceTo.remove(furthestIndex);
                            distanceTo.add(furthestIndex, node);
                        }
                    } else distanceTo.add(node);
                }
            }
            distanceFrom.forEach(node -> {
                startNode.addNeighbours(node);
                node.addNeighbours(startNode);
            });
            distanceTo.forEach(node -> {
                goalNode.addNeighbours(node);
                node.addNeighbours(goalNode);
            });
            if(startNode.getNeighbours().isEmpty() || goalNode.getNeighbours().isEmpty()){
                resultText = "Cannot proceed to path finding: start node or goal node is too far away from existing nodes.";
            }else {
                // Launch path finding process
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

                            distanceFrom.forEach(node -> node.removeNeighbours(startNode));
                            distanceTo.forEach(node -> node.removeNeighbours(goalNode));
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
        graph = GraphUtil.generateRandomGraph(graphSizeX, graphSizeY, nodeNb, minNeighbour, maxNeighbour);
        renameWindow(String.format("Proto-GPS - Size: %dx%d / Nodes: %d (%d-%d min-max neighbours/node)", graphSizeX, graphSizeY, nodeNb, minNeighbour, maxNeighbour));
        refreshWindow();
    }
}