package fr.seeden.gps.window;

import fr.seeden.gps.graph.GraphUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class DebugFrame extends JFrame {

    public DebugFrame(String name) {
        setTitle("Test GPS Algorithms - "+name);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 800);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                DebugPanel panel = (DebugPanel) getContentPane();
                if (e.getKeyChar()== KeyEvent.VK_ENTER) {
                    panel.setRenderedGraph(GraphUtil.generateRandomGraph(700, 700, 20, 3));
                    panel.renderGraph();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }
}