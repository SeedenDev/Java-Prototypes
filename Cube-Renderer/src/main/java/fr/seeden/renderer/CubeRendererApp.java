package fr.seeden.renderer;

import fr.seeden.core.Application;
import fr.seeden.core.event.EventBus;

public class CubeRendererApp extends Application {

    protected CubeRendererApp() {
        super("CubeRenderer");

        CubeRendererWindow cubeRendererWindow = new CubeRendererWindow(this);
        addWindow(cubeRendererWindow);

        EventBus.addEventListener(cubeRendererWindow);
    }

    public static void main(String[] args) {
        new CubeRendererApp();
    }
}