package fr.seeden.core.event;

import fr.seeden.core.Application;
import fr.seeden.core.window.AppWindow;

import java.awt.*;

public abstract class FocusEvent<W extends AppWindow<? extends Application<?>>> extends WindowEvent<W> {

    /*
    (temporary ? ",temporary" : ",permanent") + ",opposite=" + getOppositeComponent() + ",cause=" + getCause()
    As for now it is only called by the AppFrame, it doesn't support focus gained/lost from different components
    of the same panel (AppPanel/JPanel), so oppositeComponent/cause/temporary from the java.awt event are useless
     */

    private final Component component;

    protected FocusEvent(W window, Component component) {
        super(window);
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }

    public static class FocusGainedEvent<W extends AppWindow<? extends Application<?>>> extends FocusEvent<W> {
        public FocusGainedEvent(W window, Component component) {
            super(window, component);
        }
    }

    public static class FocusLostEvent<W extends AppWindow<? extends Application<?>>> extends FocusEvent<W> {
        public FocusLostEvent(W window, Component component) {
            super(window, component);
        }
    }
}