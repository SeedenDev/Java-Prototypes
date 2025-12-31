package fr.seeden.gps.algorithm;

import fr.seeden.gps.graph.Graph;
import fr.seeden.gps.graph.Node;

import java.util.*;

public class AStar extends Algorithm {

   /*TODO A* optimisations:

        - Add timeout (a bit faster)
            if (openSet.size() > 5000) return null;
            -
        - Bidirectional A* (quite faster)
            Run A* from start AND goal simultaneously, exploring WAY less nodes
            -
        - Early termination (a bit faster)
            if (closedSet.size() > 1000) return approximatePath(); (not the best path but isok)
            -
        - Refactor from maps to raw arrays (& BitSet instead of HashSet)
     */

    /*TODO: refactor ProcessMethod

            Just a private launch method bc there are only a few lines different for each method.
            Remove ProcessMethod interface and add OpenSet interface => isEmpty() add() init()
                => it stores the data structure depending on the processMethodSelection
     */

    // Use PriorityQueue process if graph is sparse, and ArrayList if it is dense
    private boolean useHeap;

    /**
     * How the ProcessMethod is chosen. Either AUTO (depending on the graph) or forced ARRAYLIST/PRIORITY_QUEUE process
     */
    private ProcessMethodSelection processMethodSelection = ProcessMethodSelection.AUTO;

    /** What heuristic method to use in AStar#h(node)
        - Euclidean: slower but better for more realistic graphs, e.g. real roads (shortest and most optimal path)
        - Octile: better for grids (i.e. for some games) and faster but maybe not optimal & only "Good enough" path (not always shortest)
     */
    private HeuristicMethod heuristicMethod = HeuristicMethod.EUCLIDEAN;

    private Node goal;

    public AStar(Graph graph) {
        super(graph);
        selectOptimalProcessMethod();
    }

    private void selectOptimalProcessMethod(){
        double averageDegree = graph.getAverageDegree();
        int nodeCount = graph.getNodes().size();
        useHeap = (nodeCount >= 7000 || averageDegree > 15) && nodeCount >= 3000;
    }

    @Override
    public List<Node> process(Node start, Node goal, VisitDebugCallback visitDebugCallback){
        this.goal = goal;

        ProcessMethod processMethod;
        switch (processMethodSelection) {
            case PRIORITY_QUEUE -> processMethod = new PriorityQueueProcess();
            case ARRAYLIST -> processMethod = new ArrayListProcess();
            default -> processMethod = useHeap ? new PriorityQueueProcess() : new ArrayListProcess();
        }

        List<Node> result = processMethod.launch(start, goal, visitDebugCallback);
        if(result!=null) return result;
        //TODO: timed-out, change the process method or some parameters
        //  try to recompute the path with a simpler heuristic or even change the algorithm?

        return null;
    }

    private List<Node> reconstructPath(HashMap<Node, Node> cameFrom, Node current){
        ArrayList<Node> totalPath = new ArrayList<>(Collections.singletonList(current));
        while(cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            totalPath.addFirst(current);
        }
        return totalPath;
    }

    private int h(Node node){
        if(heuristicMethod.equals(HeuristicMethod.EUCLIDEAN)) return (int) node.getDistanceFrom(this.goal);
        int dx = Math.abs((int)(node.getX() - goal.getX()));
        int dy = Math.abs((int)(node.getY() - goal.getY()));
        return (int) (Math.max(dx, dy) * 1.414);
    }

    //region Custom process section
    private class ArrayListProcess implements ProcessMethod {
        @Override
        public List<Node> launch(Node start, Node goal, VisitDebugCallback visitDebugCallback) {
            HashMap<Node, Node> cameFrom = new HashMap<>();
            HashMap<Node, Integer> gScore = new HashMap<>();
            HashMap<Node, Integer> fScore = new HashMap<>();
            ArrayList<Node> openSet = new ArrayList<>();
            HashSet<Node> closedSet = new HashSet<>();

            gScore.put(start, 0);
            fScore.put(start, h(start));
            openSet.add(start);

            while (!openSet.isEmpty()) {
                Node current = getLowestFScoreNode(openSet, fScore);
                openSet.remove(current);

                if(closedSet.contains(current)) continue;
                closedSet.add(current);

                if (current==goal) return reconstructPath(cameFrom, current);

                //TODO: add a "timeout" option returns null.

                for (Map.Entry<Node, Double> entry : current.getNeighbours().entrySet()) {
                    Node neighbour = entry.getKey();
                    if(closedSet.contains(neighbour)) continue;

                    visitDebugCallback.send(current, neighbour);

                    int tentative_gScore = (int) (gScore.get(current) + entry.getValue());
                    if(tentative_gScore < gScore.getOrDefault(neighbour, Integer.MAX_VALUE)){
                        cameFrom.put(neighbour, current);
                        gScore.put(neighbour, tentative_gScore);
                        fScore.put(neighbour, tentative_gScore+h(neighbour));

                        //if (!openSet.contains(neighbour))
                            openSet.add(neighbour);
                    }
                }
            }
            return new ArrayList<>();
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
    }
    private class PriorityQueueProcess implements ProcessMethod {
        @Override
        public List<Node> launch(Node start, Node goal, VisitDebugCallback visitDebugCallback) {
            HashMap<Node, Node> cameFrom = new HashMap<>();
            HashMap<Node, Integer> gScore = new HashMap<>();
            HashMap<Node, Integer> fScore = new HashMap<>();
            PriorityQueue<PQNodeEntry> openSet = new PriorityQueue<>();
            HashSet<Node> closedSet = new HashSet<>();

            gScore.put(start, 0);
            fScore.put(start, h(start));
            openSet.add(new PQNodeEntry(start, fScore.get(start)));

            while (!openSet.isEmpty()) {
                PQNodeEntry polled = openSet.poll();
                Node current = polled.node;

                if(closedSet.contains(current)) continue;
                closedSet.add(current);

                if (current==goal) return reconstructPath(cameFrom, current);

                //TODO: add a "timeout" option returns null.

                for (Map.Entry<Node, Double> entry : current.getNeighbours().entrySet()) {
                    Node neighbour = entry.getKey();
                    if(closedSet.contains(neighbour)) continue;

                    visitDebugCallback.send(current, neighbour);

                    int tentative_gScore = (int) (gScore.get(current) + entry.getValue());
                    if(tentative_gScore < gScore.getOrDefault(neighbour, Integer.MAX_VALUE)){
                        cameFrom.put(neighbour, current);
                        gScore.put(neighbour, tentative_gScore);
                        fScore.put(neighbour, tentative_gScore+h(neighbour));

                        openSet.add(new PQNodeEntry(neighbour, fScore.get(neighbour)));
                    }
                }
            }
            return new ArrayList<>();
        }
        private static class PQNodeEntry implements Comparable<PQNodeEntry> {
            private final Node node;
            private final int fScore;

            PQNodeEntry(Node node, int fScore) {
                this.node = node;
                this.fScore = fScore;
            }

            @Override
            public int compareTo(PQNodeEntry other) {
                int cmp = Integer.compare(this.fScore, other.fScore);
                return cmp == 0 ? Integer.compare(System.identityHashCode(this.node), System.identityHashCode(other.node)) : cmp;
            }
        }
    }
    /**
     * TreeSet has been proven too slow in my benchmarks. I keep the code here, but it should not be used. (will be removed in the next commit, just a backup)
     */
    @Deprecated
    private class TreeProcess implements ProcessMethod {
        @Override
        public List<Node> launch(Node start, Node goal, VisitDebugCallback visitDebugCallback) {
            HashMap<Node, Node> cameFrom = new HashMap<>();
            HashMap<Node, Integer> gScore = new HashMap<>();
            HashMap<Node, Integer> fScore = new HashMap<>();
            TreeSet<TreeNodeEntry> openSet = new TreeSet<>();

            gScore.put(start, 0);
            fScore.put(start, h(start));
            openSet.add(new TreeNodeEntry(start, fScore.get(start)));

            while (!openSet.isEmpty()) {
                TreeNodeEntry polled = openSet.pollFirst();
                assert polled != null;
                Node current = polled.node;

                // openTracker#remove current

                if (current==goal) return reconstructPath(cameFrom, current);

                //WASTODO: add a "timeout" option returns null.

                for (Map.Entry<Node, Double> entry : current.getNeighbours().entrySet()) {
                    Node neighbour = entry.getKey();

                    //WASTODO: callback edge current;neighbour

                    int tentative_gScore = (int) (gScore.getOrDefault(current, 0) + entry.getValue());
                    if(tentative_gScore < gScore.getOrDefault(neighbour, Integer.MAX_VALUE)){
                        cameFrom.put(neighbour, current);
                        gScore.put(neighbour, tentative_gScore);
                        fScore.put(neighbour, tentative_gScore+h(neighbour));

                        //WASTODO: openSetTracker to remove the node more efficiently
                        openSet.removeIf(e -> e.node == neighbour);
                        openSet.add(new TreeNodeEntry(neighbour, fScore.get(neighbour)));
                    }
                }
            }
            return new ArrayList<>();
        }

        private static class TreeNodeEntry implements Comparable<TreeNodeEntry> {
            final Node node;
            final int fScore;

            TreeNodeEntry(Node node, int fScore) {
                this.node = node;
                this.fScore = fScore;
            }

            @Override
            public int compareTo(TreeNodeEntry other) {
                int cmp = Integer.compare(this.fScore, other.fScore);
                return cmp == 0 ? Integer.compare(System.identityHashCode(this.node), System.identityHashCode(other.node)) : cmp;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof TreeNodeEntry && ((TreeNodeEntry) obj).node == this.node;
            }

            @Override
            public int hashCode() {
                return node.hashCode();
            }
        }
    }

    private interface ProcessMethod {
        List<Node> launch(Node start, Node goal, VisitDebugCallback visitDebugCallback);
    }
    //endregion

    public enum ProcessMethodSelection {
        AUTO, ARRAYLIST, PRIORITY_QUEUE;
    }
    public enum HeuristicMethod {
        EUCLIDEAN, OCTILE;
    }

    public void setProcessMethodSelection(ProcessMethodSelection processMethodSelection) {
        this.processMethodSelection = processMethodSelection;
    }

    public void setHeuristicMethod(HeuristicMethod heuristicMethod) {
        this.heuristicMethod = heuristicMethod;
    }

    @Override
    public void setGraph(Graph graph) {
        super.setGraph(graph);
        selectOptimalProcessMethod();
    }
}