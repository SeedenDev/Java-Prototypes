package fr.seeden.renderer.renderer;

import fr.seeden.core.math.Matrix;
import fr.seeden.core.math.Vector2;
import fr.seeden.core.math.Vector3;
import fr.seeden.renderer.math.RendererMathUtil;
import fr.seeden.renderer.math.SpecialMatrices;
import fr.seeden.renderer.world.Block;
import fr.seeden.renderer.world.Camera;
import fr.seeden.renderer.world.Player;

import java.awt.*;

public class BlockRenderer {

    // Defines default vertex pos of a block in a 1:1 scale. Each vertex has x,y,z coordinates and a line represents one of the eight vertices.
    // Axes are: front is Z+, right is X+, and top is Y+.
    private static final float[] defaultVertexPos = new float[] {
            0, 0, 0,
            1, 0, 0,
            1, 1, 0,
            0, 1, 0,
            0, 0, 1,
            1, 0, 1,
            1, 1, 1,
            0, 1, 1,
    };
    // Defines indices for the above vertices. A line being a vertex, the index 0 is the first line, the index 1 is the second line, etc...
    // Each line is a face, each face being 1° the bottom-right corner face from the origin (0,0,0) in reverse clockwise order and
    // 2° the top-left corner face also from the origin and in reverse clockwise order. And thus 1 face = 6 indexes = 4 vertices
    private static final int[] defaultIndexes = new int[]{
            0, 1, 2, 0, 2, 3, // SOUTH
            5, 4, 7, 5, 7, 6, // NORTH
            4, 0, 3, 4, 3, 7, // WEST
            1, 5, 6, 1, 6, 2, // EAST
            3, 2, 6, 3, 6, 7, // TOP
            1, 0, 4, 1, 4, 5 // BOTTOM
    };

    // --------- FOR DEBUG PURPOSES ONLY ------------
    private final Color[] facesColor = new Color[] {
            Color.CYAN, Color.GRAY, Color.ORANGE, Color.PINK, Color.BLUE, Color.GREEN
    };

    float rotX = 0, rotY = 0, rotZ = 0;

    // Handle the rendering of a block.
    public void render(Graphics g, Player player, Block block, Vector2 windowSize){
        int face = -1; // Also for debug purpose only
        for (int tri = 0; tri < defaultIndexes.length-2; tri++) {
            if(tri%6==0) face++;
            int i1 = defaultIndexes[tri];
            int i2 = defaultIndexes[++tri];
            int i3 = defaultIndexes[++tri];

            //rotX = (rotX+0.1f)%360;
            //rotY = (rotY+0.1f)%360;
            //rotZ = (rotZ+0.1f)%360;

            Vector3 blockPos = new Vector3(block.x, block.y, block.z);
            Vector3 blockRot = new Vector3(rotX, rotY, rotZ);
            Vector3 blockScale = new Vector3(1, 1, 1);

            Vector3 v1 = getVertexFromIndex(i1);
            Vector3 v2 = getVertexFromIndex(i2);
            Vector3 v3 = getVertexFromIndex(i3);
            Vector2 p1 = computeVertexPosition(g, v1, player, blockPos, blockRot, blockScale, windowSize);
            Vector2 p2 = computeVertexPosition(g, v2, player, blockPos, blockRot, blockScale, windowSize);
            Vector2 p3 = computeVertexPosition(g, v3, player, blockPos, blockRot, blockScale, windowSize);
            if(p1==null||p2==null||p3==null) break;
            g.setColor(facesColor[face]);
            g.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
            g.drawLine((int) p2.x, (int) p2.y, (int) p3.x, (int) p3.y);
            g.drawLine((int) p3.x, (int) p3.y, (int) p1.x, (int) p1.y);
        }
        Vector3 testPos = new Vector3(0, 0, 1); // Position de test devant la caméra
        Vector3 blockPos = new Vector3(0,0,4);
        Vector3 blockRot = new Vector3(0, 0, 0);
        Vector3 blockScale = new Vector3(1, 1, 1);
        Vector2 screenPos = computeVertexPosition(g, testPos, player, blockPos, blockRot, blockScale, windowSize);
        g.drawOval((int) screenPos.x, (int) screenPos.y, 10, 10);
        System.out.println("Test Position: " + screenPos);
    }

    // Compute the 3D vertex position from the world space to the 2D coordinates on the screen space
    private Vector2 computeVertexPosition(Graphics g, Vector3 vertexPos, final Player player, final Vector3 blockPos, final Vector3 blockRot, final Vector3 blockScale, final Vector2 windowSize){
        g.setColor(Color.BLACK);
        // Setting up important values for after
        final Vector3 playerPos = new Vector3(player.x, player.y, player.z);
        final Camera camera = player.camera;
        final double yaw = camera.getYaw();
        final double pitch = camera.getPitch();
        final double fov = Math.toRadians(camera.getFov());
        final int windowWidth = (int) windowSize.x;
        final int windowHeight = (int) windowSize.y;
        Matrix vertexPosition = new Matrix(new double[][]{
                {vertexPos.x}, {vertexPos.y}, {vertexPos.z}, {1f}
                //{ vertexPos.x, vertexPos.y, vertexPos.z, 1.0 }
        });
        System.out.println("VertexPosition: "+vertexPosition);

        // Model matrix (model space to world space)
        Matrix modelMatrix = new Matrix(new double[][]{
                {blockScale.x, 0, 0, blockPos.x},
                {0, blockScale.y, 0, blockPos.y}, // 0, blockScale.y, 1, blockPos.y
                {0, 0, blockScale.z, blockPos.z}, // 0, -1
                {0, 0, 0, 1}
        });
        /*System.out.println("ModelMatrix: "+modelMatrix);
        if(blockRot.x!=0) modelMatrix = modelMatrix.rotateX(blockRot.x);
        if(blockRot.y!=0) modelMatrix = modelMatrix.rotateY(blockRot.y);
        if(blockRot.z!=0) modelMatrix = modelMatrix.rotateZ(blockRot.z);*/
        //TEST CHATGPT
        Matrix scaleMatrix = SpecialMatrices.SCALE_MATRIX.apply(blockScale);
        Matrix rotationMatrix = SpecialMatrices.rotateXYZ(blockRot.x, blockRot.y, blockRot.z);
        Matrix translationMatrix = SpecialMatrices.TRANSLATION_MATRIX.apply(blockPos);
        modelMatrix = translationMatrix.translate(0.5, 0.5, 0.5) // Reverse offset
                .multiply(rotationMatrix)
                .translate(-0.5, -0.5, -0.5) // Offset for rotation to be centered
                .multiply(scaleMatrix);

        System.out.println("ModelMatrix(Rot): "+modelMatrix);
        vertexPosition = modelMatrix.multiply(vertexPosition);
        System.out.println("VertexPosition(MM): "+vertexPosition);

        // View matrix (world space to view space (=camera view/space))
        Vector3 xaxis = new Vector3( -RendererMathUtil.cosCheckZero(yaw), 0, -RendererMathUtil.sinCheckZero(yaw));
        Vector3 yaxis = new Vector3(RendererMathUtil.sinCheckZero(yaw)*RendererMathUtil.sinCheckZero(pitch), RendererMathUtil.cosCheckZero(pitch), RendererMathUtil.cosCheckZero(yaw)*RendererMathUtil.sinCheckZero(pitch));
        Vector3 zaxis = new Vector3(RendererMathUtil.sinCheckZero(yaw)*RendererMathUtil.cosCheckZero(pitch), -RendererMathUtil.sinCheckZero(pitch), RendererMathUtil.cosCheckZero(pitch)*RendererMathUtil.cosCheckZero(yaw));

        Matrix viewMatrix = new Matrix(new double[][]{
                {xaxis.x, yaxis.x, zaxis.x, 0},
                {xaxis.y, yaxis.y, zaxis.y, 0},
                {xaxis.z, yaxis.z, zaxis.z, 0},
                {-xaxis.dot(playerPos), -yaxis.dot(playerPos), -zaxis.dot(playerPos), 1}
        });
        // TEST NEW VIEW MATRIX (chatgpt)
        Vector3 forward = new Vector3(
                Math.cos(pitch) * Math.sin(yaw),
                -Math.sin(pitch),
                Math.cos(pitch) * Math.cos(yaw)
        ).normalize();
        Vector3 right = Vector3.UP.cross(forward).normalize(); // X-axis
        Vector3 up = forward.cross(right).normalize(); // Y-axis
        g.drawString(String.format("FORWARD: %s", forward), 10, 300);
        g.drawString(String.format("RIGHT: %s", right), 10, 320);
        g.drawString(String.format("UP: %s", up), 10, 340);
        viewMatrix = new Matrix(new double[][] {
                {right.x, up.x, -forward.x, 0},
                {right.y, up.y, -forward.y, 0},
                {right.z, up.z, -forward.z, 0},
                {-right.dot(playerPos), -up.dot(playerPos), forward.dot(playerPos), 1} // pas de -
        });

        System.out.println("ViewMatrix: "+viewMatrix);
        //vertexPosition = viewMatrix.multiply(vertexPosition);
        System.out.println("VertexPosition(VM): "+vertexPosition);
        //vertexPosition = vertexPosition.translate(-player.x, -player.y, -player.z);

        // Projection matrix (view space to projection space)
        final float aspect = (float) windowWidth/windowHeight;//windowHeight/windowWidth;
        final float f = (float) (1f/Math.tan(fov/2));
        final float near = 0.1f;
        final float far = 1000f;
        float q = far/(far-near);
        float qn = (far * near) / (far - near);
        Matrix projectionMatrix = new Matrix(new double[][]{
                {aspect*f, 0, 0, 0},
                {0, f, 0, 0},
                {0, 0, -q, -1},
                {0, 0, qn, 0}
        });
        /*projectionMatrix = new Matrix(new double[][]{ // ROW MAJOR
                {f / aspect, 0, 0, 0},
                {0, f, 0, 0},
                {0, 0, (far + near) / (near - far), -1},
                {0, 0, (2 * far * near) / (near - far), 0}
        });*/
        projectionMatrix = new Matrix(new double[][]{
                {f / aspect, 0, 0, 0},
                {0, f, 0, 0},
                {0, 0, (far + near) / (near - far), (2 * far * near) / (near - far)},
                {0, 0, -1, 0}
        });
        System.out.println("ProjMatrix: "+projectionMatrix);
        //vertexPosition = projectionMatrix.multiply(vertexPosition);
        System.out.println("VertexPosition(PM): "+vertexPosition);

        //vertexPosition = modelMatrix.multiply(vertexPosition);
        g.drawString(String.format("Before %s", vertexPosition), 10, 180);
        Matrix finalMatrix = projectionMatrix.multiply(viewMatrix);//.multiply(modelMatrix);
        g.drawString(String.format("Model %s", modelMatrix), 10, 200);
        g.drawString(String.format("View %s", viewMatrix), 10, 220);
        g.drawString(String.format("Proj %s", projectionMatrix), 10, 240);
        g.drawString(String.format("Final %s", finalMatrix), 10, 260);

        System.out.println("PVM: "+finalMatrix);
        vertexPosition = finalMatrix.multiply(vertexPosition);
        g.drawString(String.format("After %s", vertexPosition), 10, 280);

        // Last step, projection space to screen space (finally, 3D to 2D using z as the depth)
        double xx = vertexPosition.get(0, 0);
        double yy = vertexPosition.get(1, 0);
        double zz = vertexPosition.get(2, 0);
        double w = vertexPosition.get(3, 0);
        if(w<0){
            System.err.println("W NEGATIF");
            //return null;
        }
        if(w!=0&&w!=1){
            xx /= w;
            yy /= w;
            zz /= w;
        }
        final Vector3 pos = new Vector3(xx, yy, zz);
        System.out.println("VertexPosition(final): "+pos);
        //if (pos.x < -1 || pos.x > 1 || pos.y < -1 || pos.y > 1) return null;
        /*
        x = (x / width) * bounds.GetWidth() - bounds.GetWidth() * 0.5f;
		y = bounds.GetHeight() * 0.5f - (y / height) * bounds.GetHeight();
         */
        int posX = (int) (pos.x*(windowWidth/2)/pos.z+(windowWidth/2));
        int posY = (int) (pos.y*(windowHeight/2)/pos.z+(windowHeight/2));
        //BEELOW==chatgpt
        posX = (int)((pos.x + 1) * windowWidth / 2.0);
        posY = (int)((1 - pos.y) * windowHeight / 2.0); // Y inversé
        System.out.println("Pos: "+posX+"/"+posY+"\n---------------");
        return new Vector2(posX, posY);
    }

    private Vector3 getVertexFromIndex(int index){
        return new Vector3(defaultVertexPos[index*3], defaultVertexPos[index*3+1], defaultVertexPos[index*3+2]);
    }
}