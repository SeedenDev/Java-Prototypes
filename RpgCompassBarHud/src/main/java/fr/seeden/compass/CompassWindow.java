package fr.seeden.compass;

import fr.seeden.core.event.*;
import fr.seeden.core.math.Point2;
import fr.seeden.core.math.Vector2;
import fr.seeden.core.window.AppKeybinding;
import fr.seeden.core.window.AppWindow;
import fr.seeden.core.window.WindowUtil;

import java.awt.*;
import java.awt.event.KeyEvent;

public class CompassWindow extends AppWindow<CompassApp> implements EventListener {

    private static final short UP_BIT = 1;
    private static final short LEFT_BIT = 10;
    private static final short DOWN_BIT = 100;
    private static final short RIGHT_BIT = 1000;
    private static final int MOVE_STEP = 3;

    private final CompassHandler handler = new CompassHandler();

    public final AppKeybinding resetKey = new AppKeybinding.OnPressKeybinding("reset", KeyEvent.VK_ENTER, () -> {
        handler.reset();
        this.refreshWindow();
    });
    public final AppKeybinding upKey = new AppKeybinding("up", KeyEvent.VK_UP);
    public final AppKeybinding leftKey = new AppKeybinding("left", KeyEvent.VK_LEFT);
    public final AppKeybinding downKey = new AppKeybinding("down", KeyEvent.VK_DOWN);
    public final AppKeybinding rightKey = new AppKeybinding("right", KeyEvent.VK_RIGHT);

    // Cooldown before refreshing the player "lookAt" vector while moving mouse
    private long lookAtCooldown;

    public CompassWindow(CompassApp compassApp) {
        super("CompassApp", 600, 600, false, compassApp);
        registerKeybindings(resetKey, upKey, leftKey, downKey, rightKey);
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        if(handler.getPlayerPos()==null) return;

        short bitMask = 0;
        if(upKey.isPressed()) bitMask |= UP_BIT;
        if(leftKey.isPressed()) bitMask |= LEFT_BIT;
        if(downKey.isPressed()) bitMask |= DOWN_BIT;
        if(rightKey.isPressed()) bitMask |= RIGHT_BIT;

        if(bitMask>0){
            Point2 playerPos = handler.getPlayerPos();
            int x = playerPos.x;
            int y = playerPos.y;
            if((bitMask & UP_BIT) == UP_BIT) y -= MOVE_STEP;
            if((bitMask & LEFT_BIT) == LEFT_BIT) x -= MOVE_STEP;
            if((bitMask & DOWN_BIT) == DOWN_BIT) y += MOVE_STEP;
            if((bitMask & RIGHT_BIT) == RIGHT_BIT) x += MOVE_STEP;

            x = Math.clamp(x, 10, getPanelWidth()-10);
            y = Math.clamp(y, 10, getPanelHeight()-10);
            handler.setPlayerPos(new Point2(x, y));
            refreshWindow();
        }
    }

    @Override
    public void render(Graphics g, double deltaTime) {
        super.render(g, deltaTime);

        // Could be better if not local variables
        int panelWidth = getPanelWidth();
        int panelHeight = getPanelHeight();
        // The compass bar size
        int barX = (panelWidth-CompassHandler.BAR_WIDTH)/2;
        int barY = panelHeight-100;
        // The X position of the goal indicator point in the compass bar
        int compassX = panelWidth/2 + handler.calc();

        // Compass bar section
        g.setColor(Color.BLACK);
        g.drawRect(barX, barY, CompassHandler.BAR_WIDTH, CompassHandler.BAR_HEIGHT);

        WindowUtil.drawCircle(g, Color.GREEN, compassX, barY+CompassHandler.COMPASS_DOT_SIZE/2, CompassHandler.COMPASS_DOT_SIZE);

        // Positions and vectors section
        Point2 playerPos = handler.getPlayerPos();
        if(playerPos != null){
            WindowUtil.drawCircleWithLabel(g, Color.YELLOW, playerPos.x, playerPos.y, 20, "Player");
            Point2 lootAtPos = handler.getLootAtPos();
            if(lootAtPos!=null){
                WindowUtil.drawCircle(g, Color.ORANGE, lootAtPos.x, lootAtPos.y, 20);
                g.setColor(Color.BLACK);
                g.drawLine(playerPos.x, playerPos.y, lootAtPos.x, lootAtPos.y);

                Vector2 playerLookAtVec = handler.getPlayerLookAtVec();

                // PLayer-lookAt vector (playerLookAtVec) representation
                Vector2 lookDirVec = playerLookAtVec.normalize().mult(50);
                g.setColor(Color.PINK);
                g.drawLine(panelWidth/2, panelHeight/2, (int) ((double) panelWidth /2+lookDirVec.x), (int) ((double) panelHeight /2+lookDirVec.y));

                // Rendering the rotated vec used to check if the angle is + or - (kinda the player's FOV)
                Vector2 rotatedVec = playerLookAtVec.rot(CompassHandler.ANGLE_LIMIT).normalize().mult(50);
                g.setColor(Color.CYAN);
                g.drawLine(playerPos.x, playerPos.y, (int) (playerPos.x+rotatedVec.x), (int) (playerPos.y+rotatedVec.y));

                rotatedVec = playerLookAtVec.rot((double) -CompassHandler.ANGLE_LIMIT/2).normalize().mult(50);
                g.setColor(Color.GREEN);
                g.drawLine(playerPos.x, playerPos.y, (int) (playerPos.x+rotatedVec.x), (int) (playerPos.y+rotatedVec.y));
            }

            Point2 goalPos = handler.getGoalPos();
            if(goalPos!=null){
                WindowUtil.drawCircleWithLabel(g, Color.RED, goalPos.x, goalPos.y, 20, "Goal");

                // Player-goal vector representation
                Vector2 goalVec = new Vector2(playerPos, goalPos).normalize().mult(50);
                g.setColor(Color.BLUE);
                g.drawLine(panelWidth/2, panelHeight/2, (int) ((double) panelWidth /2+goalVec.x), (int) ((double) panelHeight /2+goalVec.y));

                // Draw the angle limits for the player to see the goal
                Vector2 minLimitVec = goalVec.rot(-CompassHandler.ANGLE_LIMIT).normalize().mult(30);
                Vector2 maxLimitVec = goalVec.rot(CompassHandler.ANGLE_LIMIT).normalize().mult(30);
                g.setColor(Color.RED);
                g.drawLine(playerPos.x, playerPos.y, (int) (playerPos.x+minLimitVec.x), (int) (playerPos.y+minLimitVec.y));
                g.drawLine(playerPos.x, playerPos.y, (int) (playerPos.x+maxLimitVec.x), (int) (playerPos.y+maxLimitVec.y));
            }
        }
    }

    @EventHandler
    public void onMouseReleased(MouseEvent.MouseReleasedEvent<CompassWindow> event){
        int x = event.getMouseX();
        int y = event.getMouseY();
        switch (event.getClickedButton()){
            case LEFT -> handler.setPlayerPos(new Point2(x, y));
            case RIGHT -> handler.setGoalPos(new Point2(x, y));
        }
        refreshWindow();
    }

    @EventHandler
    public void onMouseMoved(MouseEvent.MouseMovedEvent<CompassWindow> event){
        if(event.isShiftPressed() && lookAtCooldown < System.currentTimeMillis()){
            lookAtCooldown = System.currentTimeMillis()+50;
            handler.setLootAt(new Point2(event.getMouseX(), event.getMouseY()));
            refreshWindow();
        }
    }
}