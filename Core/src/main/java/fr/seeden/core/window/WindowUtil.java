package fr.seeden.core.window;

import fr.seeden.core.event.EMouseButton;

import java.awt.*;
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

    public static void drawCircleWithLabel(Graphics g, Color color, int x, int y, int size, String txt){
        drawCircle(g, color, x, y, size);
        g.drawString(txt+"("+x+";"+y+")", x, y);
    }

    public static void drawCircle(Graphics g, Color color, int x, int y, int size){
        g.setColor(color);
        g.drawOval(x-size/2, y-size/2, size, size);
        g.setColor(Color.BLACK);
    }

    private WindowUtil(){}
}