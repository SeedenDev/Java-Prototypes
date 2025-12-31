package fr.seeden.core.window;

public class AppKeybinding {

    private final String name;
    private final int keyCode;
    private final int modifiersBitMask;
    private boolean pressed;

    public AppKeybinding(String name, int keyCode, int modifiersBitMask) {
        this.name = name;
        this.keyCode = keyCode;
        this.modifiersBitMask = modifiersBitMask;
    }
    public AppKeybinding(String name, int keyCode){
        this(name, keyCode, 0);
    }

    protected void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public String getName() {
        return name;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public int getModifiersBitMask() {
        return modifiersBitMask;
    }

    public final boolean isPressed() {
        return pressed;
    }

    @Override
    public String toString() {
        return "Keybinding{" +
                "name='" + name + '\'' +
                ", pressed=" + pressed +
                '}';
    }

    public static class OnPressKeybinding extends AppKeybinding {
        // A runnable that executes once when the keybinding is pressed
        private final Runnable onPress;
        public OnPressKeybinding(String name, int keyCode, int modifiersBitMask, Runnable onPress) {
            super(name, keyCode, modifiersBitMask);
            this.onPress = onPress;
        }
        public OnPressKeybinding(String name, int keyCode, Runnable onPress){
            this(name, keyCode, 0, onPress);
        }

        protected void runPressAction(){
            onPress.run();
        }
    }

    public static class Modifiers {
        private static boolean shiftPressed, altPressed, altGraphPressed, ctrlPressed;

        public static boolean isShiftPressed() {
            return shiftPressed;
        }

        protected static void setShiftPressed(boolean shiftPressed) {
            Modifiers.shiftPressed = shiftPressed;
        }

        public static boolean isAltPressed() {
            return altPressed;
        }

        protected static void setAltPressed(boolean altPressed) {
            Modifiers.altPressed = altPressed;
        }

        public static boolean isAltGraphPressed() {
            return altGraphPressed;
        }

        protected static void setAltGraphPressed(boolean altGraphPressed) {
            Modifiers.altGraphPressed = altGraphPressed;
        }

        public static boolean isCtrlPressed() {
            return ctrlPressed;
        }

        protected static void setCtrlPressed(boolean ctrlPressed) {
            Modifiers.ctrlPressed = ctrlPressed;
        }
    }
}