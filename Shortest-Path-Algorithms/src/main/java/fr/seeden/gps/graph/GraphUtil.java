package fr.seeden.gps.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GraphUtil {

    public static Graph generateRandomGraph(int sizeX, int sizeY, int nodeNumber, int minNeighbour, int maxNeighbour){
        List<Node> nodeList = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < nodeNumber; i++) {
            int x = r.nextInt(10, sizeX-10);
            int y = r.nextInt(10, sizeY-10);
            Node node = new Node(x, y);
            nodeList.add(node);
            if(i==0) continue;
            int nbOfNeighbour = r.nextInt(maxNeighbour)+minNeighbour;
            for (int j = 0; j < nbOfNeighbour; j++) {
                Node neighbour = nodeList.get(r.nextInt(0, i));
                if(node.isANeighbour(neighbour) || node==neighbour) continue;
                node.addNeighbours(neighbour);
                neighbour.addNeighbours(node);
            }
        }
        return new Graph(sizeX, sizeY, nodeList);
    }

    public static Node[][] getRandomNodesPair(Graph graph, int n){
        Node[][] nodeArray = new Node[n][2];
        List<Node> nodes = graph.getNodes();
        Random r = new Random();
        for (int i = 0; i < n; i++) {
            Node from = nodes.get(r.nextInt(0, nodes.size()));
            Node to;
            do {
                to = nodes.get(r.nextInt(0, nodes.size()));
            }
            while(to==from);
            nodeArray[i][0] = from;
            nodeArray[i][1] = to;
        }
        return nodeArray;
    }
}