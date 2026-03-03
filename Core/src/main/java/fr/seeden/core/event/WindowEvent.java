package fr.seeden.core.event;

import fr.seeden.core.Application;
import fr.seeden.core.window.AppWindow;

public abstract class WindowEvent<W extends AppWindow<? extends Application<?>>> extends AppEvent {

    private final W window;

    protected WindowEvent(W window) {
        this.window = window;
    }

    public W getWindow() {
        return window;
    }
}