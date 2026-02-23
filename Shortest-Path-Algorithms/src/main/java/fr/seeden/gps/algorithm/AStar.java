package fr.seeden.gps.algorithm;

import fr.seeden.gps.graph.GhostNode;
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

        List<Node> result = launch(start, visitDebugCallback);
        if(result!=null) return result;
        //TODO: timed-out, change the process method or some parameters
        //  try to recompute the path with a simpler heuristic or even change the algorithm?

        return new ArrayList<>();
    }

    private List<Node> reconstructPath(HashMap<Node, Node> cameFrom, Node current){
        Node start = current;
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

    private List<Node> launch(Node start, VisitDebugCallback visitDebugCallback){
        OpenSet openSet;
        switch (processMethodSelection) {
            case PRIORITY_QUEUE -> openSet = new PriorityQueueOpenSet();
            case ARRAYLIST -> openSet = new ArrayListOpenSet();
            default -> openSet = useHeap ? new PriorityQueueOpenSet() : new ArrayListOpenSet();
        }

        HashMap<Node, Node> cameFrom = new HashMap<>();
        HashMap<Node, Integer> gScore = new HashMap<>();
        HashMap<Node, Integer> fScore = new HashMap<>();
        HashSet<Node> closedSet = new HashSet<>();

        gScore.put(start, 0);
        fScore.put(start, h(start));
        openSet.add(start, fScore.get(start));

        HashSet<Node> goalSet = new HashSet<>();
        goalSet.add(goal);
        if(goal instanceof GhostNode){
            for (Map.Entry<Node, Double> entry : goal.getNeighbours().entrySet()) {
                goalSet.add(entry.getKey());
            }
        }

        while (!openSet.isEmpty()) {
            Node current = openSet.next(fScore);

            if(closedSet.contains(current)) continue;
            closedSet.add(current);

            if (goalSet.contains(current)) return reconstructPath(cameFrom, goal);

            //TODO: add a "timeout" option returns null.

            for (Map.Entry<Node, Double> entry : current.getNeighbours().entrySet()) {
                Node neighbour = entry.getKey();
                if(closedSet.contains(neighbour) || neighbour instanceof GhostNode) continue;

                visitDebugCallback.send(current, neighbour);

                int tentative_gScore = (int) (gScore.get(current) + entry.getValue());
                if(tentative_gScore < gScore.getOrDefault(neighbour, Integer.MAX_VALUE)){
                    cameFrom.put(neighbour, current);
                    gScore.put(neighbour, tentative_gScore);
                    fScore.put(neighbour, tentative_gScore+h(neighbour));

                    //if (!openSet.contains(neighbour))
                    openSet.add(neighbour, fScore.get(neighbour));
                }
            }
        }
        return new ArrayList<>();
    }

    //region Custom OpenSet
    private interface OpenSet {
        void add(Node node, int node_fScore);
        Node next(HashMap<Node, Integer> fScore);
        boolean isEmpty();
    }
    private class ArrayListOpenSet implements OpenSet {

        private ArrayList<Node> openSet = new ArrayList<>();

        @Override
        public void add(Node node, int node_fScore) {
            openSet.add(node);
        }

        @Override
        public Node next(HashMap<Node, Integer> fScore) {
            Node node = getLowestFScoreNode(openSet, fScore);
            openSet.remove(node);
            return node;
        }

        @Override
        public boolean isEmpty() {
            return openSet.isEmpty();
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

    private class PriorityQueueOpenSet implements OpenSet {

        PriorityQueue<PQNodeEntry> openSet = new PriorityQueue<>();

        @Override
        public void add(Node node, int node_fScore) {
            openSet.add(new PQNodeEntry(node, node_fScore));
        }

        @Override
        public Node next(HashMap<Node, Integer> fScore) {
            return openSet.poll().node;
        }

        @Override
        public boolean isEmpty() {
            return openSet.isEmpty();
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