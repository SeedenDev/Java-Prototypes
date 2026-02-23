package fr.seeden.renderer;

import fr.seeden.core.event.EventListener;
import fr.seeden.core.window.AppWindow;

import java.awt.*;

public class CubeRendererWindow extends AppWindow implements EventListener {

    public CubeRendererWindow(CubeRendererApp mainApp) {
        super("CubeRenderer", true, mainApp);
        //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //setSize(screenSize.width, screenSize.height-100);
    }


}
