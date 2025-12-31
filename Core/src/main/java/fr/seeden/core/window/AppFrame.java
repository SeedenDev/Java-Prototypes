package fr.seeden.core.window;

import fr.seeden.core.event.EventBus;

import javax.swing.*;
import java.awt.event.*;

public final class AppFrame extends JFrame implements ComponentListener, FocusListener, KeyListener {

    //Memo to get the screen size: Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    private final AppWindow window;

    AppFrame(String name, int width, int height, boolean resizable, AppPanel panel, AppWindow window){
        this.window = window;

        setTitle(name);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(width, height);
        setResizable(resizable);
        setLocationRelativeTo(null);
        setContentPane(panel);
        setVisible(true);
        setFocusable(true);
        requestFocusInWindow();

        addComponentListener(this);
        addFocusListener(this);
        addKeyListener(this);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                window.close();
            }
        });
    }

    AppFrame(String name, boolean resizable, AppPanel panel, AppWindow window){
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

    // KeyListener, used only for the modifiers key as the Keybindings system is much better
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        AppKeybinding.Modifiers.setShiftPressed(e.isShiftDown());
        AppKeybinding.Modifiers.setAltPressed(e.isAltDown());
        AppKeybinding.Modifiers.setAltGraphPressed(e.isAltGraphDown());
        AppKeybinding.Modifiers.setCtrlPressed(e.isControlDown());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        AppKeybinding.Modifiers.setShiftPressed(e.isShiftDown());
        AppKeybinding.Modifiers.setAltPressed(e.isAltDown());
        AppKeybinding.Modifiers.setAltGraphPressed(e.isAltGraphDown());
        AppKeybinding.Modifiers.setCtrlPressed(e.isControlDown());
    }
}