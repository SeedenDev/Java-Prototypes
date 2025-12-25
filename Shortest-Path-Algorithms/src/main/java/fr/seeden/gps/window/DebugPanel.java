package fr.seeden.gps.window;

import fr.seeden.gps.GpsApi;
import fr.seeden.gps.GpsDebug;
import fr.seeden.gps.algorithm.AStar;
import fr.seeden.gps.algorithm.Dijkstra;
import fr.seeden.gps.graph.Graph;
import fr.seeden.gps.graph.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DebugPanel extends JPanel implements MouseListener {

    private Graph renderedGraph;
    private Graphics g;
    private final List<String> debugTexts = new ArrayList<>();
    private final int debugTextPos_X = 5;
    private final int debugTextPos_Y = 15;

    public DebugPanel() {
        setLayout(new BorderLayout());
        addMouseListener(this);
    }

    public void setRenderedGraph(Graph renderedGraph) {
        this.renderedGraph = renderedGraph;
        this.g = getGraphics();
    }

    public void renderGraph() {
        repaint();
        revalidate();
        System.out.println("REDRAW");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        List<Node> nodes = this.renderedGraph.getNodes();
        List<Node> drawnNodes = new ArrayList<>();
        for (Node node : nodes) {
            g.setColor(Color.GREEN);
            g.drawOval((int) node.getX()-3, (int) node.getY()-3, 6, 6);
            g.setColor(Color.BLACK);
            g.drawString(node.getX()+";"+node.getY(), (int) node.getX(), (int) node.getY());
            for (Map.Entry<Node, Double> entry : node.getNeighbours().entrySet()) {
                Node neighbour = entry.getKey();
                if(!drawnNodes.contains(neighbour)) {
                    g.setColor(Color.BLUE);
                    g.drawLine((int) node.getX(), (int) node.getY(), (int) neighbour.getX(), (int) neighbour.getY());
                    int midX = (int) Math.abs((neighbour.getX()+node.getX())/2);
                    int midY = (int) Math.abs((neighbour.getY()+node.getY())/2);
                    g.setColor(Color.RED);
                    g.drawString(Integer.toString(entry.getValue().intValue()), midX, midY);
                }
            }
            drawnNodes.add(node);
        }
        for(String debugText : debugTexts) {
            g.setColor(Color.MAGENTA);
            g.drawString(debugText, debugTextPos_X, debugTextPos_Y+(20*(debugTexts.size()-1)));
        }
    }

    public void addAlgorithmProcessLine(Node from, Node to){
        g.setColor(Color.YELLOW);
        g.drawLine((int) from.getX(), (int) from.getY(), (int) to.getX(), (int) to.getY());
    }
    public void addFinalPathLine(Node from, Node to){
        g.setColor(Color.ORANGE);
        g.drawLine((int) from.getX(), (int) from.getY(), (int) to.getX(), (int) to.getY());
    }
    public void drawStartAndGoal(Node start, Node goal){
        g.setColor(Color.MAGENTA);
        g.drawOval((int) start.getX()-10, (int) start.getY()-10, 20, 20);
        g.setColor(Color.PINK);
        g.drawOval((int) goal.getX()-10, (int) goal.getY()-10, 20, 20);
    }
    public void addDebugText(String s){
        this.debugTexts.add(s);
        g.setColor(Color.MAGENTA);
        g.drawString(s, debugTextPos_X, debugTextPos_Y+(20*(debugTexts.size()-1)));
    }
    public void clearDebugTexts(){
        this.debugTexts.clear();
        renderGraph();
    }

    private int[][] clicksPosition = new int[2][2];
    private boolean firstClick = true;

    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        if(e.getID()!= MouseEvent.MOUSE_RELEASED) return;
        if (e.getButton()==MouseEvent.BUTTON1) {
            int x = e.getX();
            int y = e.getY();
            System.out.println("CLICKED "+ x +"/"+ y);
            if(firstClick) clicksPosition[0] = new int[]{x, y};
            else clicksPosition[1] = new int[]{x, y};
            firstClick = !firstClick;
        }
        else if(e.getButton()==MouseEvent.BUTTON3){
            System.out.println("LAUNCHING PROCESS");
            Node from = new Node(clicksPosition[0][0], clicksPosition[0][1]);
            Node to = new Node(clicksPosition[1][0], clicksPosition[1][1]);
            List<Node> nodes = this.renderedGraph.getNodes();
            // Find neighbours
            List<Node> distanceFrom = new ArrayList<>();
            List<Node> distanceTo = new ArrayList<>();
            final double MAX_DIST = 70;
            for (Node node : nodes) {
                double distFrom = node.getDistanceFrom(from);
                double distTo = node.getDistanceFrom(to);
                if(distFrom<=MAX_DIST) {
                    if (distanceFrom.size() >= 5) {
                        int furthestIndex = 0;
                        double furthestDistance = 0;
                        for (int i = 0; i < distanceFrom.size(); i++) {
                            double d = distanceFrom.get(i).getDistanceFrom(from);
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
                if(distTo<=MAX_DIST) {
                    if (distanceTo.size() >= 5) {
                        int furthestIndex = 0;
                        double furthestDistance = 0;
                        for (int i = 0; i < distanceTo.size(); i++) {
                            double d = distanceTo.get(i).getDistanceFrom(to);
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
                from.addNeighbours(node);
                node.addNeighbours(from);
            });
            distanceTo.forEach(node -> {
                to.addNeighbours(node);
                node.addNeighbours(to);
            });
            if(from.getNeighbours().isEmpty() ||to.getNeighbours().isEmpty()){
                GpsDebug.debugPanelAddText("Start or end node is too far away from existing node. Cancelling path finding.");
            }else {
                // Find path
                nodes.add(from);
                nodes.add(to);
                renderGraph();
                GpsApi.setAlgorithm(new AStar(this.renderedGraph));
                GpsApi.findPathBetweenNodes(this.renderedGraph, from, to);
                if(GpsDebug.isDebugEnabled()){
                    paint(g);
                    GpsApi.setAlgorithm(new Dijkstra(this.renderedGraph));
                    GpsApi.findPathBetweenNodes(this.renderedGraph, from, to);
                }
            }
            if(GpsDebug.isDebugEnabled()){
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                clearDebugTexts();
                paint(g);
            }
            distanceFrom.forEach(node -> node.removeNeighbours(from));
            distanceTo.forEach(node -> node.removeNeighbours(to));
            nodes.remove(from);
            nodes.remove(to);
        }
        else if(e.getButton()==MouseEvent.BUTTON2) renderGraph();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}