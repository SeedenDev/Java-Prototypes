package fr.seeden.compass;

import fr.seeden.core.math.Point2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class CompassKeybinds {

    public void registerKeybindings(CompassPanel compassPanel){
        for(EKeyBindings binding : EKeyBindings.values()) {
           compassPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(binding.keyCode, 0), binding.name());
           compassPanel.getActionMap().put(binding.name(), new AbstractAction() {
               @Override
               public void actionPerformed(ActionEvent e) {
                   binding.performAction(compassPanel);
               }
           });
        }
    }

    private enum EKeyBindings {
        RESET(KeyEvent.VK_ENTER) {
            @Override
            void performAction(CompassPanel compassPanel) {
                System.out.println("RESET");
                compassPanel.reset();
                compassPanel.clearWindow();
            }
        },
        UP(KeyEvent.VK_UP) {
            @Override
            void performAction(CompassPanel compassPanel) {
                int y1 = Math.clamp(compassPanel.playerPos.y-10, 10, compassPanel.panelHeight-10);
                compassPanel.playerPos = new Point2(compassPanel.playerPos.x, y1);
                if(compassPanel.refreshCompass) compassPanel.compassHandler.calc();
                compassPanel.clearWindow();
            }
        },
        LEFT(KeyEvent.VK_LEFT) {
            @Override
            void performAction(CompassPanel compassPanel) {
                int x1 = Math.clamp(compassPanel.playerPos.x-10, 10, compassPanel.panelWidth-10);
                compassPanel.playerPos = new Point2(x1, compassPanel.playerPos.y);
                if(compassPanel.refreshCompass) compassPanel.compassHandler.calc();
                compassPanel.clearWindow();
            }
        },
        DOWN(KeyEvent.VK_DOWN) {
            @Override
            void performAction(CompassPanel compassPanel) {
                int y1 = Math.clamp(compassPanel.playerPos.y+10, 10, compassPanel.panelHeight-10);
                compassPanel.playerPos = new Point2(compassPanel.playerPos.x, y1);
                if(compassPanel.refreshCompass) compassPanel.compassHandler.calc();
                compassPanel.clearWindow();
            }
        },
        RIGHT(KeyEvent.VK_RIGHT) {
            @Override
            void performAction(CompassPanel compassPanel) {
                int x1 = Math.clamp(compassPanel.playerPos.x+10, 10, compassPanel.panelWidth-10);
                compassPanel.playerPos = new Point2(x1, compassPanel.playerPos.y);
                if(compassPanel.refreshCompass) compassPanel.compassHandler.calc();
                compassPanel.clearWindow();
            }
        };

        private final int keyCode;
        EKeyBindings(int keyCode){
            this.keyCode = keyCode;
        }
        abstract void performAction(CompassPanel compassPanel);
    }
}