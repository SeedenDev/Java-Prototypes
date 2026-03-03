package fr.seeden.renderer;

import fr.seeden.core.event.*;
import fr.seeden.core.math.MathUtil;
import fr.seeden.core.math.Vector2;
import fr.seeden.core.math.Vector3;
import fr.seeden.core.window.AppKeybinding;
import fr.seeden.core.window.AppWindow;
import fr.seeden.renderer.renderer.BlockRenderer;
import fr.seeden.renderer.world.Block;
import fr.seeden.renderer.world.Camera;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class CubeRendererWindow extends AppWindow<CubeRendererApp> implements EventListener {

    public final AppKeybinding.OnPressKeybinding resetKey = new AppKeybinding.OnPressKeybinding(
            "reset", KeyEvent.VK_ENTER, this::reset);
    public final AppKeybinding.OnPressKeybinding pauseKey = new AppKeybinding.OnPressKeybinding(
            "pause", KeyEvent.VK_ESCAPE, () -> getApp().setPaused(!getApp().isPaused()));
    public final AppKeybinding forwardKey = new AppKeybinding("forward", KeyEvent.VK_Z);
    public final AppKeybinding backwardKey = new AppKeybinding("backward", KeyEvent.VK_S);
    public final AppKeybinding leftKey = new AppKeybinding("left", KeyEvent.VK_Q);
    public final AppKeybinding rightKey = new AppKeybinding("right", KeyEvent.VK_D);
    public final AppKeybinding upKey = new AppKeybinding("up", KeyEvent.VK_SPACE);
    public final AppKeybinding downKey = new AppKeybinding("down", KeyEvent.VK_SHIFT, InputEvent.SHIFT_DOWN_MASK);

    private final Camera camera = new Camera();
    private final BlockRenderer blockRenderer = new BlockRenderer();

    public Block block = new Block(2, 0, 10);

    public int windowCenterX;
    public int windowCenterY;
    private int lastMouseX;
    private int lastMouseY;
    private boolean speedOrFov = false;

    private long pauseTextDuration;

    public CubeRendererWindow(CubeRendererApp mainApp) {
        super("CubeRenderer", true, mainApp);

        registerKeybindings(resetKey, pauseKey, forwardKey, backwardKey, leftKey, rightKey, upKey, downKey);
    }

    //region Draw util methods
    private void drawPoint(Graphics g, Color color, int x, int y, int size, String txt){
        g.setColor(color);
        g.drawOval(x-size/2, y-size/2, size, size);
        g.setColor(Color.BLACK);
        g.drawString(txt+"="+x+";"+y, x, y);
    }
    private void drawPoint(Graphics g, Color color, int x, int y, int size){
        drawPoint(g, color, x, y, size, "");
    }

    int angleOffset = -90;
    int time = 0;
    private void drawGradientCircle(Color colorStart, Color colorEnd, int scaleFactor, int pointSize, Graphics g, boolean clockWiseRotation){
        for (int angle = 0; angle < 360; angle+=2) {
            int offsetAngle = angle + (clockWiseRotation ? angleOffset : -angleOffset);
            double radians = Math.toRadians(offsetAngle);
            int x = (int) (Math.cos(radians)*scaleFactor+ (double) getWindowWidth() /2);
            int y = (int) (Math.sin(radians)*scaleFactor+ (double) getWindowHeight() /2);
            double ratio = angle>180 ? MathUtil.inverseLerp(0f, 180, 360-angle) : MathUtil.inverseLerp(0f, 180, angle);
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

    @Override
    public void render(Graphics g, double deltaTime) {
        final String fpsCounter = String.format("%.2fFPS/delta=%f - click=edit %s",
                getApp().getFps(), deltaTime, speedOrFov?"FOV":"Speed");
        final String playerInfo = String.format("xyz: %.2f %.2f %.2f speed=%.2f",
                camera.getX(), camera.getY(), camera.getZ(), camera.getMoveSpeed());
        final String cameraInfo = String.format("yaw=%.3f pitch=%.3f fov=%.1f",
                camera.getYaw(), camera.getPitch(), camera.getFov());
        final String keysInfo = String.format("up=%s down=%s front=%s back=%s left=%s right=%s",
                upKey.isPressed(), downKey.isPressed(), forwardKey.isPressed(), backwardKey.isPressed(), leftKey.isPressed(), rightKey.isPressed());
        g.setColor(Color.BLACK);
        g.drawString(fpsCounter, 10, 10);
        g.drawString(playerInfo, 10, 30);
        g.drawString(cameraInfo, 10, 50);
        g.drawString(keysInfo, 10, 70);

        blockRenderer.render(g, camera, block, new Vector2(getWindowWidth(), getWindowHeight()));
        if(getApp().isPaused()) {
            pauseTextDuration -= 1;
            if(pauseTextDuration<0) {
                g.setColor(Color.BLACK);
                g.drawString("PAUSE !", windowCenterX, windowCenterY);
            }
            if(pauseTextDuration<=-50) pauseTextDuration = 50;
        }
    }

    @Override
    public void update(double deltaTime) {
        handleMovement(deltaTime);
        refreshWindow();
    }

    //TODO: move to Camera
    private void handleMovement(double deltaTime) {
        double cos = Math.cos(camera.getYaw());
        double sin = Math.sin(camera.getYaw());
        Vector3 direction = Vector3.ZERO;
        if(forwardKey.isPressed()) direction = direction.add(new Vector3(-sin, 0, cos)); // +Z
        if(backwardKey.isPressed()) direction = direction.add(new Vector3(sin, 0, -cos)); // -Z
        if(leftKey.isPressed()) direction = direction.add(new Vector3(cos, 0, sin)); // +X
        if(rightKey.isPressed()) direction = direction.add(new Vector3(-cos, 0, -sin)); // -X
        if(upKey.isPressed()) direction = direction.add(Vector3.UP);
        if(downKey.isPressed()) direction = direction.add(Vector3.UP.mult(-1));

        if(direction.length==0) return;

        Vector3 velocity = direction/*.normalize()*/.mult((float) (camera.getMoveSpeed()*(deltaTime*1000)));
        camera.move(velocity);
    }

    public void reset(){
        camera.setPos(0f, 0f, 0f);
        camera.setDirection(0f, 0f);
    }

    @EventHandler
    public void onMouseClick(MouseEvent.MouseClickedEvent<CubeRendererWindow> event){
        if(getApp().isPaused()) return;
        switch (event.getClickedButton()) {
            case LEFT:
                if(speedOrFov) camera.setFov(camera.getFov()-10);
                else camera.setMoveSpeed(camera.getMoveSpeed()-0.1f);
                break;
            case MIDDLE:
                speedOrFov = !speedOrFov;
                break;
            case RIGHT:
                if(speedOrFov) camera.setFov(camera.getFov()+10);
                else camera.setMoveSpeed(camera.getMoveSpeed()+0.1f);
                break;
        }
    }
    
    @EventHandler
    public void onMouseMoved(MouseEvent.MouseMovedEvent<CubeRendererWindow> event) {
        int mouseX = event.getMouseX();
        int mouseY = event.getMouseY();
        if(!getApp().isPaused() && (mouseX!=0 || mouseY!=0)) camera.rotate(mouseX-lastMouseX, lastMouseY-mouseY);
        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    @EventHandler
    public void onWindowResized(ComponentEvent.ComponentResizedEvent<CubeRendererWindow> event){
        Dimension newSize = event.getNewSize();
        windowCenterX = newSize.width/2;
        windowCenterY = newSize.height/2;
    }
}
