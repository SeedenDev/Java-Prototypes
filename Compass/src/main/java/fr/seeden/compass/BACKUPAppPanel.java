package fr.seeden.compass;

import fr.seeden.core.math.Point2;
import fr.seeden.core.math.Vector2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BACKUPAppPanel extends JPanel implements MouseListener, MouseMotionListener {

    private int panelWidth, panelHeight;
    private int barWidth = 100, barHeight = 10, barX, barY;
    private final int compassSize = 10, angleLimit = 70, min = -angleLimit, max = angleLimit;

    private int compassX;
    private Point2 playerPos, lookDirection, goalPos;

    public BACKUPAppPanel() {
        setLayout(new BorderLayout());
        addMouseListener(this);
        addMouseMotionListener(this);
        setFocusable(true);
        requestFocusInWindow();

    }

    // Because I can't get the real size in the constructor
    public void initDefaultValues(){
        panelWidth = (int) this.getSize().getWidth();
        panelHeight = (int) this.getSize().getHeight();

        barX = (panelWidth-barWidth)/2;
        barY = panelHeight-100;

        compassX = panelWidth/2;
        System.out.printf("\nINIT: %s-%s\n", panelWidth, panelHeight);

        this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "RESET");
        this.getActionMap().put("RESET", new AbstractAction("RESET") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("RESET");
                reset();
                reloadGraphics();
            }
        });
        this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0), "UP");
        this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), "LEFT");
        this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "DOWN");
        this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "RIGHT");
        this.getActionMap().put("UP", new AbstractAction("UP") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int y1 = Math.clamp(playerPos.y-10, 10, panelHeight-10);
                playerPos = new Point2(playerPos.x, y1);
                if(refreshCompass) calcCompass();
                reloadGraphics();
            }
        });
        this.getActionMap().put("LEFT", new AbstractAction("LEFT") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int x1 = Math.clamp(playerPos.x-10, 10, panelWidth-10);
                playerPos = new Point2(x1, playerPos.y);
                if(refreshCompass) calcCompass();
                reloadGraphics();
            }
        });
        this.getActionMap().put("DOWN", new AbstractAction("DOWN") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int y1 = Math.clamp(playerPos.y+10, 10, panelHeight-10);
                playerPos = new Point2(playerPos.x, y1);
                if(refreshCompass) calcCompass();
                reloadGraphics();
            }
        });
        this.getActionMap().put("RIGHT", new AbstractAction("RIGHT") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int x1 = Math.clamp(playerPos.x+10, 10, panelWidth-10);
                playerPos = new Point2(x1, playerPos.y);
                if(refreshCompass) calcCompass();
                reloadGraphics();
            }
        });
    }

    public void reloadGraphics() {
        repaint();
        revalidate();
        System.out.println("REDRAW");
    }

    public void reset(){
        playerPos = lookDirection = goalPos = null;
        refreshCompass = false;
        compassX = panelWidth/2;
    }

    public void calcCompass(){
        /* Math:
            A=lookDirection ; B=goalPos ; C=playerPos
            a=dist(B,C) ; b=dist(A,C) ; c=dist(A,B)
            u(radianAngle)=angle between A and B from C point

            Formula:

            Loi des cosinus pour l'angle u:
            - c² = a² + b² - 2ab*cos(u) <=> u = acos((c²-a²-b²)/-2ab)
         */
        final int a = goalPos.distInt(playerPos);
        final int b = lookDirection.distInt(playerPos);
        final int c = lookDirection.distInt(goalPos);
        Vector2 playerLookVec = new Vector2(playerPos, lookDirection);
        Vector2 playerGoalVec = new Vector2(playerPos, goalPos);
        double scalar = playerLookVec.normalizeDot(playerGoalVec);
        System.out.println(playerLookVec+"/"+playerGoalVec+"/"+ scalar +"\n--------------");
        boolean facingGoal = scalar>0.9999;
        double angle = 0;
        if(!facingGoal){
            //System.out.printf("G=%s ; D=%s ; P=%s / a=%d ; b=%d ; c=%d \n", goalPos, lookDirection, playerPos, a, b ,c);
            double radianAngle = Math.acos(scalar);//Math.acos((sq(c)-sq(a)-sq(b))/(-2*a*b));
            double degreeAngle = Math.toDegrees(radianAngle); // *180/pi
            System.out.printf("\nRadiant=%.2f ; Degree=%.2f\n", radianAngle, degreeAngle);

            // Try rotating to know if it's positive or negative angle
            double newAngle = Math.toDegrees(Math.acos(playerLookVec.rot(angleLimit).normalizeDot(playerGoalVec)));
            if(newAngle>angleLimit) degreeAngle *= -1;
            System.out.println(newAngle+"/If");

            angle = Math.clamp(degreeAngle, min, max);
            //System.out.print(" ; Clamped angle="+angle+"\n----------");
        }
        else System.out.println("COLINEAIRES DONC ANGLE=NaN ????");
        /*
        -45/45 to 0/100

        lerp(a,b,t)->x = Renvoie une valeur entre a et b correspond au ratio t (nb entre 0 et 1)
        inverse lerp(a,b,x)->t = renvoie t selon la valeur de retour contenue entre a et b

        ex: lerp(0, 100, 0.4)=40    /      inverseLerp(0, 100, 40)=0.4
        -> inverseLerp(-45, 45, u) -> ratio pour ensuite créer le compassX
         */
        // inverseLerp(a,b,x) = (x-a) / (b-a)
        System.out.println(lerp(-45, 45, 0.5));
        System.out.println(inverseLerp(-45, 45, 0));

        double t = (angle-min) / (max-min);
        System.out.printf("Angle=%.2f / min-max=%d~%d / t=%f \n", angle, min, max, t);
        compassX = (int) ((double) panelWidth /2 + lerp(-barWidth/2, barWidth/2, t));
    }

    private double lerp(int min, int max, double ratio){
        return min + ratio * (max - min);
    }
    private double inverseLerp(int min, int max, double value){
        return (value-min) / (max-min);
    }


    private double sq(int n){
        return Math.pow(n, 2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(panelWidth==0) initDefaultValues();

        String str = "Compass calc: "+ (refreshCompass ? "ON": "OFF");
        g.setColor(Color.BLACK);
        g.drawString(str, barX, barY+barHeight+15);

        if(playerPos!=null){
            drawPoint(g, Color.YELLOW, playerPos.x, playerPos.y, 20, "Player");
            if(lookDirection!=null){
                drawPoint(g, Color.ORANGE, lookDirection.x, lookDirection.y, 20);
                g.setColor(Color.BLACK);
                g.drawLine(playerPos.x, playerPos.y, lookDirection.x, lookDirection.y);

                Vector2 playerLookVec = new Vector2(playerPos, lookDirection);

                // Center gizmo
                Vector2 lookDirVec = playerLookVec.normalize().mult(50);
                g.setColor(Color.PINK);
                g.drawLine(panelWidth/2, panelHeight/2, (int) ((double) panelWidth /2+lookDirVec.x), (int) ((double) panelHeight /2+lookDirVec.y));

                // Finding the player to goal vec with vector addition
                Vector2 lookGoalVec = new Vector2(lookDirection, goalPos);
                Vector2 playerGoalVec = playerLookVec.add(lookGoalVec);
                g.setColor(Color.GREEN);
                g.drawLine(playerPos.x, playerPos.y, (int) (playerPos.x+playerGoalVec.x/2), (int) (playerPos.y+playerGoalVec.y/2));

                // Rendering the rotated vec used to check if the angle is + or -
                Vector2 rotatedVec = playerLookVec.rot(angleLimit).normalize().mult(50);
                g.setColor(Color.CYAN);
                System.out.println("x="+rotatedVec.x+"/y="+rotatedVec.y);
                g.drawLine(playerPos.x, playerPos.y, (int) (playerPos.x+rotatedVec.x), (int) (playerPos.y+rotatedVec.y));
            }
            if(goalPos!=null){
                drawPoint(g, Color.RED, goalPos.x, goalPos.y, 20, "Goal");

                // Center gizmo
                Vector2 goalVec = new Vector2(playerPos, goalPos).normalize().mult(100);
                g.setColor(Color.BLUE);
                g.drawLine(panelWidth/2, panelHeight/2, (int) ((double) panelWidth /2+goalVec.x), (int) ((double) panelHeight /2+goalVec.y));

                // Draw the angle limits
                Vector2 minLimitVec = goalVec.rot(-angleLimit).normalize().mult(30);
                Vector2 maxLimitVec = goalVec.rot(angleLimit).normalize().mult(30);
                g.setColor(Color.RED);
                g.drawLine(playerPos.x, playerPos.y, (int) (playerPos.x+minLimitVec.x), (int) (playerPos.y+minLimitVec.y));
                g.drawLine(playerPos.x, playerPos.y, (int) (playerPos.x+maxLimitVec.x), (int) (playerPos.y+maxLimitVec.y));
            }
        }

        g.setColor(Color.BLACK);
        g.drawRect(barX, barY, barWidth, barHeight);

        g.setColor(Color.GREEN);
        g.drawOval(compassX-compassSize/2, barY, compassSize, compassSize);
    }

    private void drawPoint(Graphics g, Color color, int x, int y, int size, String txt){
        g.setColor(color);
        g.drawOval(x-size/2, y-size/2, size, size);
        g.setColor(Color.BLACK);
        g.drawString(txt+"="+x+";"+y, x, y);
    }
    private void drawPoint(Graphics g, Color color, int x, int y, int size){
        drawPoint(g, color, x, y, size, "");
    }

    boolean refreshCompass = true;
    long delay;

    // MouseListener implementations
    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        if(e.getID()==MouseEvent.MOUSE_RELEASED){
            int x = e.getX();
            int y = e.getY();
            Point2 v = new Point2(x, y);
            System.out.println("CLICKED "+ x +"/"+ y);
            switch (e.getButton()){
                case MouseEvent.BUTTON1: // Left
                    playerPos = v;
                    break;
                case MouseEvent.BUTTON2: // Middle
                    refreshCompass = !refreshCompass;
                    if(lookDirection!=null) calcCompass();
                    break;
                case MouseEvent.BUTTON3: // Right
                    goalPos = v;
                    break;
                default: break;
            }
            reloadGraphics();
        }
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
    // MouseMotionListener implementations
    @Override
    public void mouseDragged(MouseEvent e) {
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        if(e.getID()==MouseEvent.MOUSE_MOVED && e.isShiftDown() && playerPos!=null && delay<System.currentTimeMillis()){
            System.out.println("MOVE");
            delay = System.currentTimeMillis()+50;
            lookDirection = new Point2(e.getX(), e.getY());
            if(refreshCompass) calcCompass();
            reloadGraphics();
        }
    }
}