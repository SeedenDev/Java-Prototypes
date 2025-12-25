package fr.seeden.core.window;

import fr.seeden.core.event.EMouseButton;

import java.awt.event.MouseEvent;

public final class WindowUtil {

    public static EMouseButton getMouseButtonEquivalent(int btn){
        return switch(btn){
            case MouseEvent.BUTTON2 -> EMouseButton.MIDDLE;
            case MouseEvent.BUTTON3 -> EMouseButton.RIGHT;
            case 4 -> EMouseButton.BTN4;
            case 5 -> EMouseButton.BTN5;
            default -> EMouseButton.LEFT;
        };
    }

    //TODO: isAltKeyDown isMetaKeyDown isCtrlKeyDown isShiftKeyDown

    private WindowUtil(){}
}