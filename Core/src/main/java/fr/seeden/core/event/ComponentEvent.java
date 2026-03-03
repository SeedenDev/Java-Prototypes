package fr.seeden.core.event;

import fr.seeden.core.Application;
import fr.seeden.core.window.AppWindow;

import java.awt.*;

public abstract class ComponentEvent<W extends AppWindow<? extends Application<?>>> extends WindowEvent<W> {

    private final Component component;

    protected ComponentEvent(W window, Component component) {
        super(window);
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }

    public static class ComponentResizedEvent<W extends AppWindow<? extends Application<?>>> extends ComponentEvent<W> {

        private final Dimension newSize;

        public ComponentResizedEvent(W window, Component component) {
            super(window, component);
            this.newSize = component.getSize();
        }

        public Dimension getNewSize() {
            return newSize;
        }
    }

    public static class ComponentMovedEvent<W extends AppWindow<? extends Application<?>>> extends ComponentEvent<W> {

        private final Point newPosition;

        public ComponentMovedEvent(W window, Component component) {
            super(window, component);
            this.newPosition = component.getLocation();
        }

        public Point getNewPosition() {
            return newPosition;
        }
    }
}