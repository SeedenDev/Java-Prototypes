package fr.seeden.renderer.ui;

import fr.seeden.renderer.RenderAppMain;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class RenderAppKeybindings {

    public static final OnPressKeybinding RESET = new OnPressKeybinding("reset", KeyEvent.VK_ENTER, () -> RenderAppMain.INSTANCE.appPanel.reset());
    public static final OnPressKeybinding PAUSE = new OnPressKeybinding("pause", KeyEvent.VK_ESCAPE, () -> RenderAppMain.pause = !RenderAppMain.pause);
    public static final Keybinding FORWARD = new Keybinding("forward", KeyEvent.VK_Z);
    public static final Keybinding BACKWARD = new Keybinding("backward", KeyEvent.VK_S);
    public static final Keybinding LEFT = new Keybinding("left", KeyEvent.VK_Q);
    public static final Keybinding RIGHT = new Keybinding("right", KeyEvent.VK_D);
    public static final Keybinding UP = new Keybinding("up", KeyEvent.VK_SPACE);
    public static final Keybinding DOWN = new Keybinding("down", KeyEvent.VK_SHIFT, InputEvent.SHIFT_DOWN_MASK);

    public void registerKeybindings(RenderAppPanel appPanel){
        registerOnPressKeybinding(appPanel, RESET);
        registerOnPressKeybinding(appPanel, PAUSE);
        registerKeybinding(appPanel, FORWARD);
        registerKeybinding(appPanel, BACKWARD);
        registerKeybinding(appPanel, LEFT);
        registerKeybinding(appPanel, RIGHT);
        registerKeybinding(appPanel, UP);
        registerKeybinding(appPanel, DOWN);
    }

    private void registerOnPressKeybinding(RenderAppPanel appPanel, OnPressKeybinding onPressKeybinding){
        appPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(getKeyStroke(onPressKeybinding,true), onPressKeybinding.name+"OnPress");
        appPanel.getActionMap().put(onPressKeybinding.name+"OnPress", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onPressKeybinding.onPress.run();
            }
        });
    }
    private void registerKeybinding(RenderAppPanel appPanel, Keybinding keybinding){
        appPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(getKeyStroke(keybinding,true), keybinding.name+"Press");
        appPanel.getActionMap().put(keybinding.name+"Press", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keybinding.isPressed = true;
            }
        });
        appPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(getKeyStroke(keybinding, false), keybinding.name+"Release");
        appPanel.getActionMap().put(keybinding.name+"Release", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keybinding.isPressed = false;
            }
        });
    }
    public KeyStroke getKeyStroke(AbstractKeybinding binding, boolean pressed){
        return KeyStroke.getKeyStroke(binding.keyCode, pressed ? binding.modifiers : 0, !pressed);
    }

    private static class OnPressKeybinding extends AbstractKeybinding {
        private final Runnable onPress;
        private OnPressKeybinding(String name, int keyCode, int modifiers, Runnable onPress) {
            super(name, keyCode, modifiers);
            this.onPress = onPress;
        }
        private OnPressKeybinding(String name, int keyCode, Runnable onPress){
            this(name, keyCode, 0, onPress);
        }
    }
    public static class Keybinding extends AbstractKeybinding {
        private boolean isPressed = false;
        private Keybinding(String name, int keyCode, int modifiers) {
            super(name, keyCode, modifiers);
        }
        private Keybinding(String name, int keyCode){
            super(name, keyCode, 0);
        }
        public boolean isPressed() { return isPressed; }

        /*@Override
        public String toString() {
            return "Keybinding{" +
                    "name='" + name + '\'' +
                    ", isPressed=" + isPressed +
                    '}';
        }*/
        @Override
        public String toString() {
            return ""+isPressed;
        }
    }
    private static abstract class AbstractKeybinding {
        public final String name;
        public final int keyCode;
        public final int modifiers;
        private AbstractKeybinding(String name, int keyCode, int modifiers) {
            this.name = name;
            this.keyCode = keyCode;
            this.modifiers = modifiers;
        }
    }
}