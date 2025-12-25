package fr.seeden.gps;

import fr.seeden.gps.graph.Graph;
import fr.seeden.gps.graph.Node;
import fr.seeden.gps.window.DebugFrame;
import fr.seeden.gps.window.DebugPanel;

public class GpsDebug {

    private static boolean debugEnabled = false;

    private static final DebugFrame DEBUG_FRAME = new DebugFrame("Debug");
    private static final DebugPanel DEBUG_PANEL = new DebugPanel();

    private static long time;

    public static void debugRenderPanel(){
        if(debugEnabled) DEBUG_PANEL.renderGraph();
    }

    public static void debugPanelDrawFinalLine(Node start, Node end){
        if(debugEnabled) DEBUG_PANEL.addFinalPathLine(start, end);
    }

    public static void debugPanelDrawStartAndEndPoint(Node start, Node end){
        if(debugEnabled) DEBUG_PANEL.drawStartAndGoal(start, end);
    }

    public static void debugPanelDrawAlgorithmLine(Node start, Node end){
        if(debugEnabled) DEBUG_PANEL.addAlgorithmProcessLine(start, end);
    }

    public static void debugPanelAddText(String text){
        if(debugEnabled) DEBUG_PANEL.addDebugText(text);
    }

    public static void refreshPanel(){
        if(debugEnabled) DEBUG_PANEL.renderGraph();
    }

    public static void startTimer(){
        time = System.currentTimeMillis();
    }

    public static long endTimer(){
        return System.currentTimeMillis()-time;
    }

    public static void initFrameAndPanel(Graph graph){
        DEBUG_FRAME.setContentPane(DEBUG_PANEL);
        DEBUG_PANEL.setRenderedGraph(graph);
    }

    public static void setDebug(boolean enabled) {
        debugEnabled = enabled;
    }

    public static void toggleDebug(){
        debugEnabled = !debugEnabled;
    }

    public static boolean isDebugEnabled() {
        return debugEnabled;
    }
}