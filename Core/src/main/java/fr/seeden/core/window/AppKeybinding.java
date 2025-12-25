package fr.seeden.core.window;

public class AppKeybinding {

    private final String name;
    private final int keyCode;
    private final int modifiers;
    private boolean pressed;

    public AppKeybinding(String name, int keyCode, int modifiers) {
        this.name = name;
        this.keyCode = keyCode;
        this.modifiers = modifiers;
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

    public int getModifiers() {
        return modifiers;
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
        public OnPressKeybinding(String name, int keyCode, int modifiers, Runnable onPress) {
            super(name, keyCode, modifiers);
            this.onPress = onPress;
        }
        public OnPressKeybinding(String name, int keyCode, Runnable onPress){
            this(name, keyCode, 0, onPress);
        }

        protected void runPressAction(){
            onPress.run();
        }
    }
}