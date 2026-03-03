package fr.seeden.renderer;

import fr.seeden.core.Application;
import fr.seeden.core.event.EventBus;

public class CubeRendererApp extends Application<CubeRendererApp> {

    private boolean paused = false;

    protected CubeRendererApp() {
        super("CubeRenderer");

        CubeRendererWindow cubeRendererWindow = new CubeRendererWindow(this);
        addWindow(cubeRendererWindow);

        EventBus.addEventListener(cubeRendererWindow);
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public static void main(String[] args) {
        new CubeRendererApp();
    }
}