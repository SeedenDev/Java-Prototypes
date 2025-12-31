package fr.seeden.compass;

import fr.seeden.core.Application;
import fr.seeden.core.event.EventBus;

public class CompassApp extends Application {

    public CompassApp() {
        super("CompassApp");
        //getLogger().enable(true);

        CompassWindow compassWindow = new CompassWindow(this);
        addWindow(compassWindow);

        EventBus.addEventListener(compassWindow);
    }

    public static void main(String[] args) {
        new CompassApp();
    }
}