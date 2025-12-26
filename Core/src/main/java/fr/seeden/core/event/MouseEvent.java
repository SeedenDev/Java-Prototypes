package fr.seeden.core.event;

import fr.seeden.core.window.AppWindow;

public abstract class MouseEvent extends WindowEvent {

    private final int mouseX, mouseY;
    private final boolean shiftPressed, altPressed, altGraphPressed, ctrlPressed;

    protected MouseEvent(AppWindow window, int mouseX, int mouseY, boolean shiftPressed, boolean altPressed, boolean altGraphPressed, boolean ctrlPressed) {
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

    public static class MouseClickedEvent extends MouseEvent {

        private final EMouseButton clickedButton;

        public MouseClickedEvent(AppWindow window, EMouseButton clickedButton, int mouseX, int mouseY, boolean shiftPressed, boolean altPressed, boolean altGraphPressed, boolean ctrlPressed) {
            super(window, mouseX, mouseY, shiftPressed, altPressed, altGraphPressed, ctrlPressed);
            this.clickedButton = clickedButton;
        }

        public EMouseButton getClickedButton() {
            return clickedButton;
        }
    }
    public static class MousePressedEvent extends MouseClickedEvent {
        public MousePressedEvent(AppWindow window, EMouseButton clickedButton, int mouseX, int mouseY, boolean shiftPressed, boolean altPressed, boolean altGraphPressed, boolean ctrlPressed) {
            super(window, clickedButton, mouseX, mouseY, shiftPressed, altPressed, altGraphPressed, ctrlPressed);
        }
    }
    public static class MouseReleasedEvent extends MouseClickedEvent {
        public MouseReleasedEvent(AppWindow window, EMouseButton clickedButton, int mouseX, int mouseY, boolean shiftPressed, boolean altPressed, boolean altGraphPressed, boolean ctrlPressed) {
            super(window, clickedButton, mouseX, mouseY, shiftPressed, altPressed, altGraphPressed, ctrlPressed);
        }
    }

    public static class MouseEnteredEvent extends MouseEvent {
        public MouseEnteredEvent(AppWindow window, int mouseX, int mouseY, boolean shiftPressed, boolean altPressed, boolean altGraphPressed, boolean ctrlPressed) {
            super(window, mouseX, mouseY, shiftPressed, altPressed, altGraphPressed, ctrlPressed);
        }
    }
    public static class MouseExitedEvent extends MouseEvent {
        public MouseExitedEvent(AppWindow window, int mouseX, int mouseY, boolean shiftPressed, boolean altPressed, boolean altGraphPressed, boolean ctrlPressed) {
            super(window, mouseX, mouseY, shiftPressed, altPressed, altGraphPressed, ctrlPressed);
        }
    }

    public static class MouseDragged extends MouseEvent {
        public MouseDragged(AppWindow window, int mouseX, int mouseY, boolean shiftPressed, boolean altPressed, boolean altGraphPressed, boolean ctrlPressed) {
            super(window, mouseX, mouseY, shiftPressed, altPressed, altGraphPressed, ctrlPressed);
        }
    }
    public static class MouseMovedEvent extends MouseEvent {
        public MouseMovedEvent(AppWindow window, int mouseX, int mouseY, boolean shiftPressed, boolean altPressed, boolean altGraphPressed, boolean ctrlPressed) {
            super(window, mouseX, mouseY, shiftPressed, altPressed, altGraphPressed, ctrlPressed);
        }
    }

    public static class MouseScrollEvent extends MouseEvent {

        private final EMouseScrollDirection direction;

        public MouseScrollEvent(AppWindow window, EMouseScrollDirection direction, int mouseX, int mouseY, boolean shiftPressed, boolean altPressed, boolean altGraphPressed, boolean ctrlPressed) {
            super(window, mouseX, mouseY, shiftPressed, altPressed, altGraphPressed, ctrlPressed);
            this.direction = direction;
        }

        public EMouseScrollDirection getDirection() {
            return direction;
        }
    }
}