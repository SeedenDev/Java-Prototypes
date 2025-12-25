package fr.seeden.core.event;

import fr.seeden.core.window.AppWindow;

public abstract class MouseEvent extends WindowEvent {

    private final int mouseX, mouseY;

    protected MouseEvent(AppWindow window, int mouseX, int mouseY) {
        super(window);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public int getMouseX() {
        return mouseX;
    }
    public int getMouseY() {
        return mouseY;
    }

    public static class MouseClickedEvent extends MouseEvent {

        private final EMouseButton clickedButton;

        public MouseClickedEvent(AppWindow window, EMouseButton clickedButton, int mouseX, int mouseY) {
            super(window, mouseX, mouseY);
            this.clickedButton = clickedButton;
        }

        public EMouseButton getClickedButton() {
            return clickedButton;
        }
    }
    public static class MousePressedEvent extends MouseClickedEvent {
        public MousePressedEvent(AppWindow window, EMouseButton clickedButton, int mouseX, int mouseY) {
            super(window, clickedButton, mouseX, mouseY);
        }
    }
    public static class MouseReleasedEvent extends MouseClickedEvent {
        public MouseReleasedEvent(AppWindow window, EMouseButton clickedButton, int mouseX, int mouseY) {
            super(window, clickedButton, mouseX, mouseY);
        }
    }

    public static class MouseEnteredEvent extends MouseEvent {
        public MouseEnteredEvent(AppWindow window, int mouseX, int mouseY) {
            super(window, mouseX, mouseY);
        }
    }
    public static class MouseExitedEvent extends MouseEvent {
        public MouseExitedEvent(AppWindow window, int mouseX, int mouseY) {
            super(window, mouseX, mouseY);
        }
    }

    public static class MouseDragged extends MouseEvent {
        public MouseDragged(AppWindow window, int mouseX, int mouseY) {
            super(window, mouseX, mouseY);
        }
    }
    public static class MouseMovedEvent extends MouseEvent {
        public MouseMovedEvent(AppWindow window, int mouseX, int mouseY) {
            super(window, mouseX, mouseY);
        }
    }

    public static class MouseScrollEvent extends MouseEvent {

        private final EMouseScrollDirection direction;

        public MouseScrollEvent(AppWindow window, EMouseScrollDirection direction, int mouseX, int mouseY) {
            super(window, mouseX, mouseY);
            this.direction = direction;
        }

        public EMouseScrollDirection getDirection() {
            return direction;
        }
    }
}