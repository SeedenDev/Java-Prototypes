package fr.seeden.gps;

import fr.seeden.core.Application;
import fr.seeden.core.LaunchArgs;
import fr.seeden.core.event.EventBus;
import fr.seeden.gps.algorithm.AStar;
import fr.seeden.gps.algorithm.Dijkstra;
import fr.seeden.gps.graph.Graph;
import fr.seeden.gps.graph.GraphUtil;
import fr.seeden.gps.graph.Node;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;

public class GpsTestApp extends Application {

    private static final LaunchArgs.Arg<Boolean> USE_DEBUG_WINDOW =
            new LaunchArgs.Arg<>("useDebugWindow", false, true);
    private static final LaunchArgs.Arg<Boolean> ENABLE_NODE_DRAW =
            new LaunchArgs.Arg<>("enableNodeDraw", true, true);
    private static final LaunchArgs.Arg<Boolean> ENABLE_EDGE_DRAW =
            new LaunchArgs.Arg<>("enableEdgeDraw", false, true);
    private static final LaunchArgs.Arg<Boolean> ENABLE_NODE_LABEL_DRAW =
            new LaunchArgs.Arg<>("enableNodeLabelDraw", false, true);
    private static final LaunchArgs.Arg<Boolean> ENABLE_EDGE_WEIGHT_DRAW =
            new LaunchArgs.Arg<>("enableEdgeWeightDraw", false, true);
    private static final LaunchArgs.Arg<Boolean> ENABLE_DEBUG_EDGE_DRAW =
            new LaunchArgs.Arg<>("enableDebugEdgeDraw", true, true);
    private static final LaunchArgs.ArgMandatoryValue<Integer> DEBUG_WINDOW_NODE_NB =
            new LaunchArgs.ArgMandatoryValue<>("debugWindowNodeNb", 1000);
    private static final LaunchArgs.ArgMandatoryValue<Integer> DEBUG_WINDOW_MIN_NEIGHBOUR =
            new LaunchArgs.ArgMandatoryValue<>("debugWindowMinNeighbour", 3);
    private static final LaunchArgs.ArgMandatoryValue<Integer> DEBUG_WINDOW_MAX_NEIGHBOUR =
            new LaunchArgs.ArgMandatoryValue<>("debugWindowMaxNeighbour", 15);

    protected GpsTestApp(LaunchArgs.ComputedArgs args) {
        super("GPS Algorithms");

        boolean enableNodeDraw = args.get(ENABLE_NODE_DRAW);
        boolean enableEdgeDraw = args.get(ENABLE_EDGE_DRAW);
        boolean enableNodeLabelDraw = args.get(ENABLE_NODE_LABEL_DRAW);
        boolean enableEdgeWeightDraw = args.get(ENABLE_EDGE_WEIGHT_DRAW);
        boolean enableDebugEdgeDraw = args.get(ENABLE_DEBUG_EDGE_DRAW);
        int nodeNb = args.get(DEBUG_WINDOW_NODE_NB);
        int minNeighbour = args.get(DEBUG_WINDOW_MIN_NEIGHBOUR);
        int maxNeighbour = args.get(DEBUG_WINDOW_MAX_NEIGHBOUR);

        GpsTestWindow gpsWindow = new GpsTestWindow(this, nodeNb, minNeighbour, maxNeighbour, enableNodeDraw, enableEdgeDraw, enableNodeLabelDraw, enableEdgeWeightDraw, enableDebugEdgeDraw);
        addWindow(gpsWindow);

        EventBus.addEventListener(gpsWindow);
    }

    /**
     * Args list (should follow a hyphen "-"). If not set, use the default value (missingDefaultValue) :
     * - useDebugWindow => open the GpsTestWindow with random graphs. Launch pathfinding by choosing the start and goal position (default disabled)
     * Arguments to use if "useDebugWindow" is set:
     *      The followings may take a boolean value after an equal symbol "arg=value". If there is no value, defaulted to "true" (notSetDefaultValue)
     * - enableNodeDraw => toggle the draw call of the nodes (default enabled)
     * - enableEdgeDraw => toggle the draw call of the edges (default disabled)
     * - enableNodeLabelDraw => toggle the draw call of the node labels (default disabled) [not executed if nodeDraw disabled]
     * - enableEdgeWeightDraw => toggle the draw call of the edge weight text (default disabled) [not executed if edgeDraw disabled]
     * - enableDebugEdgeDraw => toggle the draw call of the visual debug visited edges (default enabled)
     *      The followings should take an integer value after an equal symbol "arg=value". If there is no value, arg is ignored and default value is used.
     * - debugWindowNodeNb => set the node number of the randomly generated graph (default 10000)
     * - debugWindowMinNeighbour => set the minimum neighbour number per node of the randomly generated graph (default 3)
     * - debugWindowMaxNeighbour =>set the maximum neighbour number per node of the randomly generated graph (default 15)
     */
    public static void main(String[] args) {
        LaunchArgs.ComputedArgs computedArgs = LaunchArgs.compute(args, USE_DEBUG_WINDOW, ENABLE_NODE_DRAW, ENABLE_EDGE_DRAW, ENABLE_NODE_LABEL_DRAW, ENABLE_EDGE_WEIGHT_DRAW, ENABLE_DEBUG_EDGE_DRAW, DEBUG_WINDOW_NODE_NB, DEBUG_WINDOW_MIN_NEIGHBOUR, DEBUG_WINDOW_MAX_NEIGHBOUR);

        boolean useDebugWindow = computedArgs.get(USE_DEBUG_WINDOW);
        if(useDebugWindow) new GpsTestApp(computedArgs);
        else {
            testAlgorithms();
            GpsService.shutdownServices();
        }
    }

    private static void testAlgorithms(){
        // Test on a small graph
        int graphSize = 500, nodeNb = 50, minNeighbour = 2, maxNeighbour = 5, testsNb = 100;
        Graph graph = GraphUtil.generateRandomGraph(graphSize, graphSize, nodeNb, minNeighbour, maxNeighbour);
        Node[][] nodeArray = GraphUtil.getRandomNodesPair(graph, testsNb);
        //testDijkstra(testsNb, nodeArray, graph, graphSize, minNeighbour, maxNeighbour, "small");
        testAStar(testsNb, nodeArray, graph, graphSize, minNeighbour, maxNeighbour, "small");

        // Test on a middle-sized graph
        graphSize = 5000; nodeNb = 1000; minNeighbour = 50; maxNeighbour = 200; testsNb = 500;
        graph = GraphUtil.generateRandomGraph(graphSize, graphSize, nodeNb, minNeighbour, maxNeighbour);
        nodeArray = GraphUtil.getRandomNodesPair(graph, testsNb);
        //testAStar(testsNb, nodeArray, graph, graphSize, minNeighbour, maxNeighbour, "middle-sized");

        // Test on a dense graph
        graphSize = 50000; nodeNb = 10000; minNeighbour = 100; maxNeighbour = 500; testsNb = 1000;
        graph = GraphUtil.generateRandomGraph(graphSize, graphSize, nodeNb, minNeighbour, maxNeighbour);
        nodeArray = GraphUtil.getRandomNodesPair(graph, testsNb);
        //testAStar(testsNb, nodeArray, graph, graphSize, minNeighbour, maxNeighbour, "dense");

        // Test on a more realistic graph
        graphSize = 100000; nodeNb = 50000; minNeighbour = 2; maxNeighbour = 8; testsNb = 100;
        graph = GraphUtil.generateRandomGraph(graphSize, graphSize, nodeNb, minNeighbour, maxNeighbour);
        nodeArray = GraphUtil.getRandomNodesPair(graph, testsNb);
        //testAStar(testsNb, nodeArray, graph, graphSize, minNeighbour, maxNeighbour, "more realistic");
    }

    private static void testDijkstra(int testsNb, Node[][] nodeArray, Graph graph, int graphSize, int minNeighbour, int maxNeighbour, String info){
        System.out.println("\n\nDijkstra algorithm test on a "+info+" graph");
        printGraphInfo(graph, graphSize, minNeighbour, maxNeighbour);

        Dijkstra algorithm = new Dijkstra(graph);
        GpsService.setAlgorithm(algorithm);

        chainTests(testsNb, nodeArray);
    }

    private static void testAStar(int testsNb, Node[][] nodeArray, Graph graph, int graphSize, int minNeighbour, int maxNeighbour, String info){
        System.out.println("\n\nA* algorithm test on a "+info+" graph");
        printGraphInfo(graph, graphSize, minNeighbour, maxNeighbour);

        AStar algorithm = new AStar(graph);
        GpsService.setAlgorithm(algorithm);

        algorithm.setProcessMethodSelection(AStar.ProcessMethodSelection.ARRAYLIST);
        algorithm.setHeuristicMethod(AStar.HeuristicMethod.EUCLIDEAN);
        System.out.println("\nRun test: ArrayList + Euclidean");
        chainTests(testsNb, nodeArray);

        algorithm.setHeuristicMethod(AStar.HeuristicMethod.OCTILE);
        System.out.println("\nRun test: ArrayList + Octile");
        chainTests(testsNb, nodeArray);

        algorithm.setProcessMethodSelection(AStar.ProcessMethodSelection.PRIORITY_QUEUE);
        algorithm.setHeuristicMethod(AStar.HeuristicMethod.EUCLIDEAN);
        System.out.println("\nRun test: PriorityQueue + Euclidean");
        chainTests(testsNb, nodeArray);

        algorithm.setHeuristicMethod(AStar.HeuristicMethod.OCTILE);
        System.out.println("\nRun test: PriorityQueue + Octile");
        chainTests(testsNb, nodeArray);
    }

    private static void chainTests(int testsNb, Node[][] nodeArray){
        final Stack<Long> testsDuration = new Stack<>();
        long totalDuration = System.currentTimeMillis();
        for (int i = 0; i < nodeArray.length; i++) {
            Node from = nodeArray[i][0];
            Node to = nodeArray[i][1];

            //System.out.println("From" + from + " to " + to);
            long startTime = System.currentTimeMillis();

            CompletableFuture<List<Node>> processFuture = GpsService.findPathBetweenNodes(from, to, null, path -> {
                long duration = System.currentTimeMillis() - startTime;
                testsDuration.push(duration);
                //System.out.println("Path is: " + (!path.isEmpty() ? path : "NOT FOUND"));
                //System.out.println("Process took " + duration + "ms");
            });
        }

        int size = testsDuration.size();
        while(size!=testsNb) { size = testsDuration.size(); }
        totalDuration = System.currentTimeMillis() - totalDuration;

        long allIndividualDuration = 0;
        while (!testsDuration.empty()) {
            allIndividualDuration += testsDuration.pop();
        }
        System.out.println("Took "+totalDuration+"ms for "+size+" tests. Average test: "+(allIndividualDuration/size)+"ms");
    }

    private static void printGraphInfo(Graph graph, int graphSize, int minNeighbour, int maxNeighbour){
        System.out.println("Graph stats:");
        System.out.println("Size: "+graphSize);
        System.out.println("Min-max neighbours/node: "+minNeighbour+"-"+maxNeighbour);
        System.out.println("Nodes: " +graph.getNodes().size());
        System.out.println("Avg degree: " +graph.getAverageDegree());
        System.out.println("Total edges: " +graph.getTotalEdges());
        System.out.println("Density: " + (graph.getTotalEdges() * 2.0 / (graph.getNodes().size() * (graph.getNodes().size() - 1))));
    }
}