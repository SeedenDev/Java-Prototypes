package fr.seeden.core.window;

import fr.seeden.core.AppLogger;
import fr.seeden.core.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AppWindow {

    private final Application mainApp;
    private final AppFrame frame;
    private final AppPanel panel;
    private String windowName;
    private int windowWidth, windowHeight;

    public AppWindow(String windowName, int windowWidth, int windowHeight, boolean resizable, Application mainApp) {
        this.mainApp = mainApp;
        this.panel = new AppPanel(mainApp, this);
        this.frame = new AppFrame(windowName, windowWidth, windowHeight, resizable, panel, this);
        this.windowName = windowName;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    public void update(double deltaTime) {}
    public void render(Graphics g, double deltaTime) {}

    public void refreshWindow(){
        this.panel.refreshPanel();
    }

    public void registerKeybinding(AppKeybinding keybinding){
        this.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(getKeyStroke(keybinding,true), keybinding.getName()+"Press");
        this.panel.getActionMap().put(keybinding.getName()+"Press", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keybinding.setPressed(true);
                if(keybinding instanceof AppKeybinding.OnPressKeybinding) ((AppKeybinding.OnPressKeybinding) keybinding).runPressAction();
            }
        });
        this.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(getKeyStroke(keybinding, false), keybinding.getName()+"Release");
        this.panel.getActionMap().put(keybinding.getName()+"Release", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keybinding.setPressed(false);
            }
        });
    }
    public void registerKeybindings(AppKeybinding... keybindings){
        for (AppKeybinding keybinding : keybindings) {
            registerKeybinding(keybinding);
        }
    }

    private KeyStroke getKeyStroke(AppKeybinding binding, boolean pressed){
        return KeyStroke.getKeyStroke(binding.getKeyCode(), pressed ? binding.getModifiers() : 0, !pressed);
    }

    public final void renameWindow(String windowName) {
        this.windowName = windowName;
        this.frame.setTitle(windowName);
    }

    /**
     * Resize the window frame and update the window size
     */
    public final void resizeFrame(int width, int height){
        this.windowWidth = width;
        this.windowHeight = height;
        this.frame.setSize(width, height);
    }
    /**
     * Update the window size without resizing the window frame
     */
    public final void resizeWindow(int width, int height){
        this.windowWidth = width;
        this.windowHeight = height;
    }

    public final int getPanelWidth(){
        return panel.getWidth();
    }

    public final int getPanelHeight(){
        return panel.getHeight();
    }

    public final AppPanel getPanel() {
        return panel;
    }

    public final String getWindowName() {
        return windowName;
    }

    public final int getWindowWidth() {
        return windowWidth;
    }

    public final int getWindowHeight() {
        return windowHeight;
    }

    public final AppLogger getLogger(){
        return mainApp.getLogger();
    }
}