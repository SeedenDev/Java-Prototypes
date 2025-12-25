package fr.seeden.core.window;

import fr.seeden.core.Application;
import fr.seeden.core.event.EMouseButton;
import fr.seeden.core.event.EMouseScrollDirection;
import fr.seeden.core.event.EventBus;
import fr.seeden.core.event.MouseEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class AppPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

    private final Application mainApp;
    private final AppWindow window;

    public AppPanel(Application mainApp, AppWindow window) {
        this.mainApp = mainApp;
        this.window = window;
        setLayout(new BorderLayout());
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    public void clearWindow() {
        repaint();
        revalidate();
        mainApp.getLogger().debug("Cleared window");
    }

    @Override
    public void paintComponents(Graphics g) {
        super.paintComponents(g);
        this.mainApp.dispatchRender(g);
    }

    // MouseListener
    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
        EMouseButton mouseButton = WindowUtil.getMouseButtonEquivalent(e.getButton());
        EventBus.dispatchEvent(new MouseEvent.MouseClickedEvent(window, mouseButton, e.getX(), e.getY()));
    }
    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {
        EMouseButton mouseButton = WindowUtil.getMouseButtonEquivalent(e.getButton());
        EventBus.dispatchEvent(new MouseEvent.MousePressedEvent(window, mouseButton, e.getX(), e.getY()));
    }
    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {
        EMouseButton mouseButton = WindowUtil.getMouseButtonEquivalent(e.getButton());
        EventBus.dispatchEvent(new MouseEvent.MouseReleasedEvent(window, mouseButton, e.getX(), e.getY()));
    }
    @Override
    public void mouseEntered(java.awt.event.MouseEvent e) {
        EventBus.dispatchEvent(new MouseEvent.MouseEnteredEvent(window, e.getX(), e.getY()));
    }
    @Override
    public void mouseExited(java.awt.event.MouseEvent e) {
        EventBus.dispatchEvent(new MouseEvent.MouseExitedEvent(window, e.getX(), e.getY()));
    }

    // MouseMotionListener
    @Override
    public void mouseDragged(java.awt.event.MouseEvent e) {
        EventBus.dispatchEvent(new MouseEvent.MouseDragged(window, e.getX(), e.getY()));
    }
    @Override
    public void mouseMoved(java.awt.event.MouseEvent e) {
        EventBus.dispatchEvent(new MouseEvent.MouseMovedEvent(window, e.getX(), e.getY()));
    }

    // MouseWheelListener implementation
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        EMouseScrollDirection direction = e.getWheelRotation()<0 ? EMouseScrollDirection.FORWARD : EMouseScrollDirection.BACKWARD;
        EventBus.dispatchEvent(new fr.seeden.core.event.MouseEvent.MouseScrollEvent(window, direction, e.getX(), e.getY()));
    }
}