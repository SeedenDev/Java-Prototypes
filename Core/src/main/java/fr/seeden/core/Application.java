package fr.seeden.core;

import fr.seeden.core.window.AppWindow;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Application {

    private final String appName;
    private final AppLogger logger;
    private final List<AppWindow> windowList = new ArrayList<>();
    private int fpsLimit = 60;
    private double deltaTime;

    protected Application(String appName) {
        this.appName = appName;
        logger = new AppLogger(appName);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Instant beginTime = Instant.now();
                Duration lastTime = Duration.ZERO;
                while(!Thread.currentThread().isInterrupted()){
                    Instant currentTime = Instant.now();
                    Duration runTime = Duration.between(beginTime, currentTime);
                    double deltaTimeMillis = Math.clamp((double) runTime.toMillis() - lastTime.toMillis(), 0.001, 0.1);
                    deltaTime = deltaTimeMillis / 1000;
                    lastTime = runTime;

                    double fps = 1.0 / deltaTime;
                    double renderMs = 1000.0 / fps;
                    logger.debug("FPS:%f DT:%f MS:%f", fps, deltaTime, renderMs);

                    internalTick();

                    if(fpsLimit>0){
                        try {
                            Thread.sleep(1000/fpsLimit);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
            }
        }, "AppRunThread");
        thread.start();
    }

    private void internalTick(){
        tick(deltaTime);
        for (AppWindow appWindow : this.windowList) {
            appWindow.update(deltaTime);
        }
    }
    public void tick(double deltaTime){}

    public void dispatchRender(Graphics graphics){
        for (AppWindow appWindow : this.windowList) {
            appWindow.render(graphics, deltaTime);
        }
    }

    public final void addWindow(AppWindow window){
        windowList.add(window);
    }

    public final void setFpsLimit(int fpsLimit) {
        this.fpsLimit = Math.max(fpsLimit, 0);
    }

    public final String getAppName() {
        return appName;
    }

    public final AppLogger getLogger() {
        return logger;
    }
}