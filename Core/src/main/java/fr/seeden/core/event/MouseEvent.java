package fr.seeden.core.event;

import fr.seeden.core.Application;
import fr.seeden.core.window.AppWindow;

public abstract class MouseEvent<W extends AppWindow<? extends Application<?>>> extends WindowEvent<W> {

    private final int mouseX, mouseY;
    private final boolean shiftPressed, altPressed, altGraphPressed, ctrlPressed;

    protected MouseEvent(W window, int mouseX, int mouseY, boolean shiftPressed, boolean altPressed, boolean altGraphPressed, boolean ctrlPressed) {
        super(window);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.shiftPressed = shiftPressed;
        this.altPressed = altPressed;
        this.altGraphPressed = altGraphPressed;
        this.ctrlPressed = ctrlPressed;
    }

    public int getMouseX() {
        return mouseX;
    }
    public int getMouseY() {
        return mouseY;
    }

    public boolean isShiftPressed() {
        return shiftPressed;
    }
    public boolean isAltPressed() {
        return altPressed;
    }
    public boolean isAltGraphPressed() {
        return altGraphPressed;
    }
    public boolean isCtrlPressed() {
        return ctrlPressed;
    }

    public static class MouseClickedEvent<W extends AppWindow<? extends Application<?>>> extends MouseEvent<W> {

        private final EMouseButton clickedButton;

        public MouseClickedEvent(W window, EMouseButton clickedButton, int mouseX, int mouseY, boolean shiftPressed, boolean altPressed, boolean altGraphPressed, boolean ctrlPressed) {
            super(window, mouseX, mouseY, shiftPressed, altPressed, altGraphPressed, ctrlPressed);
            this.clickedButton = clickedButton;
        }

        public EMouseButton getClickedButton() {
            return clickedButton;
        }
    }
    public static class MousePressedEvent<W extends AppWindow<? extends Application<?>>> extends MouseClickedEvent<W> {
        public MousePressedEvent(W window, EMouseButton clickedButton, int mouseX, int mouseY, boolean shiftPressed, boolean altPressed, boolean altGraphPressed, boolean ctrlPressed) {
            super(window, clickedButton, mouseX, mouseY, shiftPressed, altPressed, altGraphPressed, ctrlPressed);
        }
    }
    public static class MouseReleasedEvent<W extends AppWindow<? extends Application<?>>> extends MouseClickedEvent<W> {
        public MouseReleasedEvent(W window, EMouseButton clickedButton, int mouseX, int mouseY, boolean shiftPressed, boolean altPressed, boolean altGraphPressed, boolean ctrlPressed) {
            super(window, clickedButton, mouseX, mouseY, shiftPressed, altPressed, altGraphPressed, ctrlPressed);
        }
    }

    public static class MouseEnteredEvent<W extends AppWindow<? extends Application<?>>> extends MouseEvent<W> {
        public MouseEnteredEvent(W window, int mouseX, int mouseY, boolean shiftPressed, boolean altPressed, boolean altGraphPressed, boolean ctrlPressed) {
            super(window, mouseX, mouseY, shiftPressed, altPressed, altGraphPressed, ctrlPressed);
        }
    }
    public static class MouseExitedEvent<W extends AppWindow<? extends Application<?>>> extends MouseEvent<W> {
        public MouseExitedEvent(W window, int mouseX, int mouseY, boolean shiftPressed, boolean altPressed, boolean altGraphPressed, boolean ctrlPressed) {
            super(window, mouseX, mouseY, shiftPressed, altPressed, altGraphPressed, ctrlPressed);
        }
    }

    public static class MouseDragged<W extends AppWindow<? extends Application<?>>> extends MouseEvent<W> {
        public MouseDragged(W window, int mouseX, int mouseY, boolean shiftPressed, boolean altPressed, boolean altGraphPressed, boolean ctrlPressed) {
            super(window, mouseX, mouseY, shiftPressed, altPressed, altGraphPressed, ctrlPressed);
        }
    }
    public static class MouseMovedEvent<W extends AppWindow<? extends Application<?>>> extends MouseEvent<W> {
        public MouseMovedEvent(W window, int mouseX, int mouseY, boolean shiftPressed, boolean altPressed, boolean altGraphPressed, boolean ctrlPressed) {
            super(window, mouseX, mouseY, shiftPressed, altPressed, altGraphPressed, ctrlPressed);
        }
    }

    public static class MouseScrollEvent<W extends AppWindow<? extends Application<?>>> extends MouseEvent<W> {

        private final EMouseScrollDirection direction;

        public MouseScrollEvent(W window, EMouseScrollDirection direction, int mouseX, int mouseY, boolean shiftPressed, boolean altPressed, boolean altGraphPressed, boolean ctrlPressed) {
            super(window, mouseX, mouseY, shiftPressed, altPressed, altGraphPressed, ctrlPressed);
            this.direction = direction;
        }

        public EMouseScrollDirection getDirection() {
            return direction;
        }
    }
}