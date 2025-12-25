package fr.seeden.core.window;

import fr.seeden.core.event.EventBus;

import javax.swing.*;
import java.awt.event.*;

public final class AppFrame extends JFrame implements ComponentListener, FocusListener {

    //Memo to get the screen size: Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    private final AppWindow window;

    public AppFrame(String name, int width, int height, boolean resizable, AppPanel panel, AppWindow window){
        this.window = window;

        setTitle(name);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(width, height);
        setResizable(resizable);
        setLocationRelativeTo(null);
        setVisible(true);
        setContentPane(panel);

        addComponentListener(this);
        addFocusListener(this);
    }

    public AppFrame(String name, boolean resizable, AppPanel panel, AppWindow window){
        this.window = window;

        setTitle(name);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(resizable);
        setLocationRelativeTo(null);
        setVisible(true);
        setContentPane(panel);
    }

    // ComponentListener
    @Override
    public void componentResized(ComponentEvent e) {
        this.window.resizeWindow(e.getComponent().getWidth(), e.getComponent().getHeight());
        EventBus.dispatchEvent(new fr.seeden.core.event.ComponentEvent.ComponentResizedEvent(window, e.getComponent()));
    }
    @Override
    public void componentMoved(ComponentEvent e) {
        EventBus.dispatchEvent(new fr.seeden.core.event.ComponentEvent.ComponentMovedEvent(window, e.getComponent()));
    }
    @Override
    public void componentShown(ComponentEvent e) {
    }
    @Override
    public void componentHidden(ComponentEvent e) {
    }

    // FocusListener
    @Override
    public void focusGained(FocusEvent e) {
        EventBus.dispatchEvent(new fr.seeden.core.event.FocusEvent.FocusGainedEvent(window, e.getComponent()));
    }
    @Override
    public void focusLost(FocusEvent e) {
        EventBus.dispatchEvent(new fr.seeden.core.event.FocusEvent.FocusLostEvent(window, e.getComponent()));
    }
}