package fr.seeden.compass;

import fr.seeden.core.event.*;
import fr.seeden.core.window.AppKeybinding;
import fr.seeden.core.window.AppWindow;

import java.awt.*;
import java.awt.event.KeyEvent;

public class CompassWindow extends AppWindow implements EventListener {

    public static final AppKeybinding TEST_KEY = new AppKeybinding("szszs", KeyEvent.VK_E);

    public CompassWindow(CompassApp compassApp) {
        super("CompassApp", 600, 600, false, compassApp);

        registerKeybinding(TEST_KEY);
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        if(TEST_KEY.isPressed()) System.out.println("e");
    }

    @Override
    public void render(Graphics g, double deltaTime) {
        super.render(g, deltaTime);
    }
}