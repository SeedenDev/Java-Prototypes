package fr.seeden.renderer.ui;

import fr.seeden.core.math.MathUtil;
import fr.seeden.core.math.Vector2;
import fr.seeden.renderer.RenderAppMain;
import fr.seeden.renderer.renderer.BlockRenderer;
import fr.seeden.renderer.world.Block;
import fr.seeden.renderer.world.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class RenderAppPanel extends JPanel implements MouseListener, MouseMotionListener {

    private final BlockRenderer blockRenderer = new BlockRenderer();
    private final RenderAppKeybindings keybindings = new RenderAppKeybindings();

    // Should be "final" but I can't initialize them in constructor because it is impossible to get the size of the window in it (without the Windows toolbar)
    public int windowWidth;
    public int windowHeight;
    public int windowCenterX;
    public int windowCenterY;

    public Block block = new Block(2, 0, 10);
    public Player player = new Player();

    private long lastRefresh = System.currentTimeMillis();
    private float fps;
    private int lastMouseX;
    private int lastMouseY;
    private int mouseX;
    private int mouseY;
    private boolean speedOrFov = false;

    private long pauseTextDuration;

    public RenderAppPanel() throws AWTException {
        setLayout(new BorderLayout());
        addMouseListener(this);
        addMouseMotionListener(this);
        keybindings.registerKeybindings(this);
    }

    // Init panel size here because I can't do it anywhere else
    public void initDefaultValues(){
        windowWidth = (int) this.getSize().getWidth();
        windowHeight = (int) this.getSize().getHeight();

        windowCenterX = windowWidth/2;
        windowCenterY = windowHeight/2;
    }

    //region Drawing on screen
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(windowWidth!=this.getSize().getWidth()) initDefaultValues();

        final String fpsCounter = String.format("%.2fFPS/delta=%f - click=edit %s", fps, RenderAppMain.DELTA_TIME, speedOrFov?"FOV":"Speed");
        final String playerInfo = String.format("xyz: %.2f %.2f %.2f speed=%.2f", player.x, player.y, player.z, player.getSpeed());
        final String cameraInfo = String.format("yaw=%.3f pitch=%.3f fov=%.1f", player.camera.getYaw(), player.camera.getPitch(), player.camera.getFov());
        final String keysInfo = String.format("up=%s down=%s front=%s back=%s left=%s right=%s",
                RenderAppKeybindings.UP, RenderAppKeybindings.DOWN, RenderAppKeybindings.FORWARD, RenderAppKeybindings.BACKWARD, RenderAppKeybindings.LEFT, RenderAppKeybindings.RIGHT);
        g.setColor(Color.BLACK);
        g.drawString(fpsCounter, 10, 10);
        g.drawString(playerInfo, 10, 30);
        g.drawString(cameraInfo, 10, 50);
        g.drawString(keysInfo, 10, 70);

        blockRenderer.render(g, player, block, new Vector2(windowWidth, windowHeight));
        if(RenderAppMain.pause) {
            pauseTextDuration -= 1;
            if(pauseTextDuration<0) {
                g.setColor(Color.BLACK);
                g.drawString("PAUSE !", windowCenterX, windowCenterY);
            }
            if(pauseTextDuration<=-50) pauseTextDuration = 50;
        }
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
    //endregion
    //region fun circles
    int angleOffset = -90;
    int time = 0;
    private void drawGradientCircle(Color colorStart, Color colorEnd, int scaleFactor, int pointSize, Graphics g, boolean clockWiseRotation){
        for (int angle = 0; angle < 360; angle+=2) {
            int offsetAngle = angle + (clockWiseRotation ? angleOffset : -angleOffset);
            double radians = Math.toRadians(offsetAngle);
            int x = (int) (Math.cos(radians)*scaleFactor+windowWidth/2);
            int y = (int) (Math.sin(radians)*scaleFactor+windowHeight/2);
            double ratio = angle>180 ? MathUtil.inverseLerp(0, 180, 360-angle) : MathUtil.inverseLerp(0, 180, angle);
            g.setColor(lerpColor(colorStart, colorEnd, ratio));
            g.fillRect(x-(pointSize/2), y-(pointSize/2), pointSize, pointSize);
        }
    }
    private Color lerpColor(Color start, Color end, double ratio){
        int startRed = start.getRed();
        int startBlue = start.getBlue();
        int startGreen = start.getGreen();
        int endRed = end.getRed();
        int endBlue = end.getBlue();
        int endGreen = end.getGreen();
        double red = MathUtil.lerp(startRed, endRed, ratio);
        double blue = MathUtil.lerp(startBlue, endBlue, ratio);
        double green = MathUtil.lerp(startGreen, endGreen, ratio);
        return new Color((int) red, (int) blue, (int) green);
    }
    //endregion

    public void tick() {
        repaint();
        revalidate();
        long currentTime = System.currentTimeMillis();
        fps = 1/((float) (currentTime - lastRefresh)/1000);
        lastRefresh = currentTime;
        if(RenderAppMain.pause) {
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return;
        }
        // Handle camera rotation
        if(mouseX!=0 || mouseY!=0) player.camera.rotate(mouseX-lastMouseX, lastMouseY-mouseY);
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        // Handle movements
        player.handleMovement();
    }

    public void reset(){
        player = new Player();
    }

    //region MouseListener implementations
    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        if(e.getID()==MouseEvent.MOUSE_RELEASED && !RenderAppMain.pause){
            switch (e.getButton()){
                case MouseEvent.BUTTON1: // Left
                    if(speedOrFov) player.camera.setFov(player.camera.getFov()-10);
                    else player.setSpeed(player.getSpeed()-0.1f);
                    break;
                case MouseEvent.BUTTON2: // Middle
                    speedOrFov = !speedOrFov;
                    break;
                case MouseEvent.BUTTON3: // Right
                    if(speedOrFov) player.camera.setFov(player.camera.getFov()+10);
                    else player.setSpeed(player.getSpeed()+0.1f);
                    break;
                default: break;
            }
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
    //endregion
    //region MouseMotionListener implementations
    @Override
    public void mouseDragged(MouseEvent e) {
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        this.mouseX = e.getX();
        this.mouseY = e.getY();
    }
    //endregion
}