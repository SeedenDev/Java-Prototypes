package fr.seeden.renderer;

import fr.seeden.renderer.ui.RenderAppPanel;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;

public class RenderAppMain extends JFrame {

    public static RenderAppMain INSTANCE;
    public static RenderAppPanel appPanel;

    public static float DELTA_TIME = 0;
    public static boolean pause = true;

    public static void main(String[] args) {
        /*double[][] data = new double[5120][5120];
        Random r = new Random();
        int m = 10000;
        long[][] results = new long[3][m];
        for (int n = 0; n < m; n++) {
            for (int i = 0; i < data.length; i++) {
                for (int k = 0; k < data.length; k++) {
                    data[i][k] = r.nextDouble(1000000000);
                }
            }
            int angle = r.nextInt(10, 270);
            Matrix matrix = new Matrix(data);
            long start = System.currentTimeMillis();
            matrix.deepCopy().rotateX(angle);
            results[0][n] = System.currentTimeMillis()-start;
            start = System.currentTimeMillis();
            matrix.deepCopy().rotateY(angle);
            results[1][n] = System.currentTimeMillis()-start;
            start = System.currentTimeMillis();
            matrix.deepCopy().rotateZ(angle);
            results[2][n] = System.currentTimeMillis()-start;
        }
        System.out.println(Arrays.toString(results[0]));
        long[] sum = new long[3];
        for (int n = 0; n < 3; n++) {
            long k = 0;
            for (int i = 0; i < results[n].length; i++) {
                k += results[n][i];
            }
            sum[n] = k/m;
        }
        System.out.println("Moyenne 1: "+sum[0]);
        System.out.println("Moyenne 2: "+sum[1]);
        System.out.println("Moyenne 3: "+sum[2]);*/

        /*Matrix mTranslate = new Matrix(new double[][]{
                {1, 0, 0, 1.5},
                {0, 1, 0, 1.0},
                {0, 0, 1, 1.5},
                {0, 0, 0, 1.0}
        });
        Matrix mRotateY = SpecialMatrices.yRotationMatrix(90);
        Matrix mRotateX = SpecialMatrices.xRotationMatrix.apply(180d);
        System.out.println(((int)Math.sin(Math.toRadians(180)))+"/"+Math.sin(Math.toRadians(180))+"/"+ MathUtil.cosCheckZero(180));
        System.out.println("X180"+mRotateX);
        System.out.println("Y90"+mRotateY);
        System.out.println(mTranslate.multiply(mRotateX).multiply(mRotateY));*/

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }
                try {
                    appPanel = new RenderAppPanel();
                    INSTANCE = new RenderAppMain();
                } catch (AWTException e) {
                    throw new RuntimeException(e);
                }
                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        while(!Thread.currentThread().isInterrupted()){
                                            Instant beginTime = Instant.now();
                                            appPanel.tick();
                                            DELTA_TIME = Math.clamp(Duration.between(beginTime, Instant.now()).getSeconds(), 0016.7f, 100.0f);
                                            try {
                                                Thread.sleep(1000/120);
                                            } catch (InterruptedException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    }
                                }, "RenderThread");
                thread.start();
            }
        });
    }

    public RenderAppMain() throws AWTException {
        setTitle("3D Renderer OMG????");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height-100);
        //setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
        setContentPane(appPanel);
    }
}