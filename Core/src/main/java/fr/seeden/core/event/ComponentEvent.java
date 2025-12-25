package fr.seeden.core.event;

import fr.seeden.core.window.AppWindow;

import java.awt.*;

public abstract class ComponentEvent extends WindowEvent {

    private final Component component;

    protected ComponentEvent(AppWindow window, Component component) {
        super(window);
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }

    public static class ComponentResizedEvent extends ComponentEvent {

        private final Dimension newSize;

        public ComponentResizedEvent(AppWindow window, Component component) {
            super(window, component);
            this.newSize = component.getSize();
        }

        public Dimension getNewSize() {
            return newSize;
        }
    }

    public static class ComponentMovedEvent extends ComponentEvent {

        private final Point newPosition;

        public ComponentMovedEvent(AppWindow window, Component component) {
            super(window, component);
            this.newPosition = component.getLocation();
        }

        public Point getNewPosition() {
            return newPosition;
        }
    }
}