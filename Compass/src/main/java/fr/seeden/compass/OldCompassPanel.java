package fr.seeden.compass;

import fr.seeden.core.math.Point2;
import fr.seeden.core.math.Vector2;
import fr.seeden.core.window.AppPanel;

import java.awt.*;
import java.awt.event.MouseEvent;

public class OldCompassPanel {
//
//    public static final int BAR_WIDTH = 100;
//    public static final int BAR_HEIGHT = 10;
//    public static final int COMPASS_DOT_SIZE = 10;
//    public static final int ANGLE_LIMIT = 70;
//    public static final int MIN_ANGLE = -ANGLE_LIMIT;
//    public static final int MAX_ANGLE = ANGLE_LIMIT;
//    // Should be "final" but I can't initialize them in constructor because it is impossible to get the size of the window in it (without the Windows toolbar)
//    public int panelWidth;
//    public int panelHeight;
//    public int barX;
//    public int barY;
//    // Position of player & goal + where the player is looking at + the position of the compass dot in its bar
//    public Point2 playerPos;
//    public Point2 lookPoint;
//    public Point2 goalPos;
//    public int compassX;
//    // Whether the compass dot position is calculated
//    boolean refreshCompass = true;
//    // Delay between each change of "lookPoint" (cursor)
//    private long delay;
//
//    public CompassHandler compassHandler = new CompassHandler(new CompassPanel(null));
//    private final CompassKeybinds keybindings = new CompassKeybinds();
//
//    public OldCompassPanel(CompassApp compassApp) {
//        super(compassApp, null);
//        //keybindings.registerKeybindings(this);
//    }
//
//    // Init panel size here because I can't do it anywhere else
//    public void initDefaultValues(){
//        panelWidth = (int) this.getSize().getWidth();
//        panelHeight = (int) this.getSize().getHeight();
//        System.out.println(panelWidth+"x"+panelHeight);
//        //TODO: AppMain.LOGGER.log("\nINIT PANEL: %s-%s\n", panelWidth, panelHeight);
//
//        barX = (panelWidth-BAR_WIDTH)/2;
//        barY = panelHeight-100;
//
//        compassX = panelWidth/2;
//    }
//
//    //region Drawing on screen
//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        if(panelWidth==0) initDefaultValues();
//
//        // Compass bar section
//        String str = "Compass calc: "+ (refreshCompass ? "ON": "OFF");
//        g.setColor(Color.BLACK);
//        g.drawString(str, barX, barY+BAR_HEIGHT+15);
//
//        g.setColor(Color.BLACK);
//        g.drawRect(barX, barY, BAR_WIDTH, BAR_HEIGHT);
//
//        g.setColor(Color.GREEN);
//        g.drawOval(compassX-COMPASS_DOT_SIZE/2, barY, COMPASS_DOT_SIZE, COMPASS_DOT_SIZE);
//        // Positions and vectors section
//        if(playerPos!=null){
//            drawPoint(g, Color.YELLOW, playerPos.x, playerPos.y, 20, "Player");
//            if(lookPoint!=null){
//                drawPoint(g, Color.ORANGE, lookPoint.x, lookPoint.y, 20);
//                g.setColor(Color.BLACK);
//                g.drawLine(playerPos.x, playerPos.y, lookPoint.x, lookPoint.y);
//
//                Vector2 playerLookVec = new Vector2(playerPos, lookPoint);
//
//                // Center gizmo
//                Vector2 lookDirVec = playerLookVec.normalize().mult(50);
//                g.setColor(Color.PINK);
//                g.drawLine(panelWidth/2, panelHeight/2, (int) ((double) panelWidth /2+lookDirVec.x), (int) ((double) panelHeight /2+lookDirVec.y));
//
//                // Rendering the rotated vec used to check if the angle is + or -
//                Vector2 rotatedVec = playerLookVec.rot(ANGLE_LIMIT).normalize().mult(50);
//                g.setColor(Color.CYAN);
//                g.drawLine(playerPos.x, playerPos.y, (int) (playerPos.x+rotatedVec.x), (int) (playerPos.y+rotatedVec.y));
//
//                rotatedVec = playerLookVec.rot((double) -ANGLE_LIMIT/2).normalize().mult(50);
//                g.setColor(Color.GREEN);
//                g.drawLine(playerPos.x, playerPos.y, (int) (playerPos.x+rotatedVec.x), (int) (playerPos.y+rotatedVec.y));
//            }
//            if(goalPos!=null){
//                drawPoint(g, Color.RED, goalPos.x, goalPos.y, 20, "Goal");
//
//                // Center gizmo
//                Vector2 goalVec = new Vector2(playerPos, goalPos).normalize().mult(100);
//                g.setColor(Color.BLUE);
//                g.drawLine(panelWidth/2, panelHeight/2, (int) ((double) panelWidth /2+goalVec.x), (int) ((double) panelHeight /2+goalVec.y));
//
//                // Draw the angle limits
//                Vector2 minLimitVec = goalVec.rot(-ANGLE_LIMIT).normalize().mult(30);
//                Vector2 maxLimitVec = goalVec.rot(ANGLE_LIMIT).normalize().mult(30);
//                g.setColor(Color.RED);
//                g.drawLine(playerPos.x, playerPos.y, (int) (playerPos.x+minLimitVec.x), (int) (playerPos.y+minLimitVec.y));
//                g.drawLine(playerPos.x, playerPos.y, (int) (playerPos.x+maxLimitVec.x), (int) (playerPos.y+maxLimitVec.y));
//            }
//        }
//    }
//
//    private void drawPoint(Graphics g, Color color, int x, int y, int size, String txt){
//        g.setColor(color);
//        g.drawOval(x-size/2, y-size/2, size, size);
//        g.setColor(Color.BLACK);
//        g.drawString(txt+"="+x+";"+y, x, y);
//    }
//    private void drawPoint(Graphics g, Color color, int x, int y, int size){
//        drawPoint(g, color, x, y, size, "");
//    }
//    //endregion
//
//    public void reset(){
//        playerPos = lookPoint = goalPos = null;//compassHandler.resetPoints();
//        refreshCompass = false;
//        compassX = panelWidth/2;
//    }
//
//    @Override
//    protected void processMouseEvent(MouseEvent e) {
//        super.processMouseEvent(e);
//        if(e.getID()==MouseEvent.MOUSE_RELEASED){
//            int x = e.getX();
//            int y = e.getY();
//            Point2 v = new Point2(x, y);
//            System.out.println("OVERRIDE /// CLICKED "+ x +"/"+ y);
//            switch (e.getButton()){
//                case MouseEvent.BUTTON1: // Left
//                    playerPos = v;
//                    break;
//                case MouseEvent.BUTTON2: // Middle
//                    refreshCompass = !refreshCompass;
//                    if(lookPoint!=null) compassHandler.calc();
//                    break;
//                case MouseEvent.BUTTON3: // Right
//                    goalPos = v;
//                    break;
//                default: break;
//            }
//            clearWindow();
//        }
//    }
//
//    @Override
//    public void mouseMoved(MouseEvent e) {
//        if(e.isShiftDown() && playerPos!=null && delay<System.currentTimeMillis()){
//            delay = System.currentTimeMillis()+50;
//            lookPoint = new Point2(e.getX(), e.getY());
//            if(refreshCompass) compassHandler.calc();
//            clearWindow();
//        }
//    }
}