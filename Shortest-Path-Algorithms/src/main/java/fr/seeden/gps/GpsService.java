package fr.seeden.gps;

import fr.seeden.gps.algorithm.Algorithm;
import fr.seeden.gps.graph.GhostNode;
import fr.seeden.gps.graph.GraphUtil;
import fr.seeden.gps.graph.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class GpsService {

    /*TODO: some optimisations I may do one day

        - Precomputed landmarks (really faster)
            Cache distances from key nodes so the heuristic h(node) is max(landmark distances)
            => API allows to add landmarks and precompute some(all?) paths from them (with A*) + ofc saves them (serialization)

        - highways hierarchy (and contraption hierarchy like gmaps?)
            Launch algorithm process on different graph layers
     */

    // The maximum number of threads the CPU has
    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();

    // The maximum number of threads to be used by the process. They are distributed for each ExecutorService below.
    private static int threadCount = (int) (MAX_THREADS*0.75);

    // The ExecutorService for algorithm process supplyAsync
    private static ExecutorService SUPPLY_SERVICE;
    // The ExecutorService to be used for the results like thenAcceptAsync or every other callbacks the caller wants to add to the CompletableFuture
    public static ExecutorService RESULT_SERVICE;

    private static int closestNodesMaxDistance = 70, closestNodesMaxCount = 5;

    private static Algorithm algorithm = null;

    static {
        initExecutorServices();
        Runtime.getRuntime().addShutdownHook(new Thread(GpsService::shutdownServices));
    }

    /**
     *
     * @param from The node to start from
     * @param to The node to go to
     * @param visitDebugCallback A custom callback being called when nodes are visited. For visual debug purpose only (in GpsTestWindow).
     * @param acceptCallback A callback to handle the path processed by the algorithm
     * @return A CompletableFuture to allow developers to add more callbacks like {@link CompletableFuture#thenRunAsync(Runnable)}
     */
    static int i = 0;
    public static CompletableFuture<List<Node>> findPathBetweenNodes(Node from, Node to, Algorithm.VisitDebugCallback visitDebugCallback, Consumer<List<Node>> acceptCallback){
        if(algorithm==null) throw new NullPointerException("Algorithm is not set. Should be set using GpsService#setAlgorithm()");
        if(SUPPLY_SERVICE.isShutdown() || RESULT_SERVICE.isShutdown()) throw new RuntimeException("The services have been shutdown. Please call GpsService#shutdownServices() only at the end of your program.");

        final Algorithm.VisitDebugCallback debugCallback = visitDebugCallback!=null ? visitDebugCallback : (n1,n2) -> {};

        if(from instanceof GhostNode){
            List<Node> fromClosest = GraphUtil.findClosestNodesTo(from.getX(), from.getY(), closestNodesMaxDistance, closestNodesMaxCount, algorithm.getGraph().getNodes());
            if(fromClosest.isEmpty()) return CompletableFuture.completedFuture(new ArrayList<>());
            from.addNeighbours(fromClosest);
        }
        if(to instanceof GhostNode){
            List<Node> toClosest = GraphUtil.findClosestNodesTo(to.getX(), to.getY(), closestNodesMaxDistance, closestNodesMaxCount, algorithm.getGraph().getNodes());
            if(toClosest.isEmpty()) return CompletableFuture.completedFuture(new ArrayList<>());
            to.addNeighbours(toClosest);
        }

        CompletableFuture<List<Node>> completableFuture = CompletableFuture.supplyAsync(() -> {
            List<Node> path = algorithm.process(from, to, debugCallback);
            return path;
        }, SUPPLY_SERVICE);
        completableFuture.thenAcceptAsync(acceptCallback, RESULT_SERVICE);
        return completableFuture;
    }

    /**
     * Set the maximum number of threads to be used by the API program.
     *  {@code threadCount}
     * @param threadCount The maximum number of thread.
     * Could not be less than 2 threads (1 per service), and more than 75% of the maximum number of CPU threads.
     * It leaves a quarter for other process on the computer, meaning for low-end device with only 4 threads
     *                 it then keeps one thread from being used by the processes launched here.
     * After that, 80% of the threads goes to the ExecutorService handling algorithm processes,
     *                 and the other 20% to the other ExecutorService handling the results.
     */
    public static void setThreadCount(int threadCount) {
        GpsService.threadCount = Math.clamp(threadCount, 2, (int) (MAX_THREADS*0.75));
        shutdownServices();
        initExecutorServices();
    }

    /**
     * You should only call this method at the end of your program to free the ExecutorService pools
     * and stop them from waiting new tasks and then blocking your program to stop.
     */
    public static void shutdownServices() {
        SUPPLY_SERVICE.shutdownNow();
        RESULT_SERVICE.shutdownNow();
    }

    private static void initExecutorServices(){
        int resultThreadCount = Math.max((int)(GpsService.threadCount*0.2), 1);
        int supplyThreadCount = Math.max(GpsService.threadCount-resultThreadCount, 1);
        SUPPLY_SERVICE = Executors.newFixedThreadPool(supplyThreadCount);
        RESULT_SERVICE = Executors.newFixedThreadPool(resultThreadCount);
    }

    public static void setClosestNodesMaxCount(int closestNodesMaxCount) {
        GpsService.closestNodesMaxCount = Math.max(closestNodesMaxCount, 1);
    }

    public static void setClosestNodesMaxDistance(int closestNodesMaxDistance) {
        GpsService.closestNodesMaxDistance = Math.max(closestNodesMaxDistance, 1);
    }

    public static int getMaxThreads() {
        return MAX_THREADS;
    }

    public static void setAlgorithm(Algorithm inAlgorithm){
        algorithm = inAlgorithm;
    }
    public static Algorithm getAlgorithm() {
        return algorithm;
    }

    private GpsService(){}
}