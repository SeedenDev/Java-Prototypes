package fr.seeden.core.event;

import fr.seeden.core.window.AppWindow;

public abstract class WindowEvent extends AppEvent {

    private final AppWindow window;

    protected WindowEvent(AppWindow window) {
        this.window = window;
    }

    public AppWindow getWindow() {
        return window;
    }
}