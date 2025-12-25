package fr.seeden.renderer.world;

import fr.seeden.core.math.Matrix;
import fr.seeden.core.math.Point2;
import fr.seeden.core.math.Point3;
import fr.seeden.core.math.Vector3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Block {

    public int x, y, z;

    public Block(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static class Renderer {

        private final List<Face[]> facesBuffer = new ArrayList<>();

        public void buildModel(Block block){
            // moi : Face/Nord=Z - Gauche/Ouest=X - Haut=Y
            //NORTH FACE
            Face north = new Face(Color.CYAN);
            north.vertices[0] = new Vertex(block.x, block.y, block.z+1);
            north.vertices[1] = new Vertex(block.x+1, block.y, block.z+1);
            north.vertices[2] = new Vertex(block.x, block.y+1, block.z+1);
            north.vertices[3] = new Vertex(block.x+1, block.y+1, block.z+1);
            north.indexes[0]=0;
            north.indexes[1]=2;
            north.indexes[2]=1;
            north.indexes[3]=1;
            north.indexes[4]=2;
            north.indexes[5]=3;
            //SOUTH FACE
            Face south = new Face(Color.GRAY);
            south.vertices[0] = new Vertex(block.x, block.y, block.z);
            south.vertices[1] = new Vertex(block.x+1, block.y, block.z);
            south.vertices[2] = new Vertex(block.x, block.y+1, block.z);
            south.vertices[3] = new Vertex(block.x+1, block.y+1, block.z);
            south.indexes[0]=0;
            south.indexes[1]=2;
            south.indexes[2]=1;
            south.indexes[3]=1;
            south.indexes[4]=2;
            south.indexes[5]=3;
            //TOP FACE
            Face top = new Face(Color.BLUE);
            top.vertices[0] = new Vertex(block.x, block.y+1, block.z);
            top.vertices[1] = new Vertex(block.x+1, block.y+1, block.z);
            top.vertices[2] = new Vertex(block.x, block.y+1, block.z+1);
            top.vertices[3] = new Vertex(block.x+1, block.y+1, block.z+1);
            top.indexes[0]=0;
            top.indexes[1]=1;
            top.indexes[2]=2;
            top.indexes[3]=1;
            top.indexes[4]=3;
            top.indexes[5]=2;
            //BOTTOM FACE
            Face bottom = new Face(Color.GREEN);
            bottom.vertices[0] = new Vertex(block.x, block.y, block.z);
            bottom.vertices[1] = new Vertex(block.x+1, block.y, block.z);
            bottom.vertices[2] = new Vertex(block.x, block.y, block.z+1);
            bottom.vertices[3] = new Vertex(block.x+1, block.y, block.z+1);
            bottom.indexes[0]=0;
            bottom.indexes[1]=1;
            bottom.indexes[2]=2;
            bottom.indexes[3]=1;
            bottom.indexes[4]=3;
            bottom.indexes[5]=2;
            //WEST FACE
            Face west = new Face(Color.ORANGE);
            west.vertices[0] = new Vertex(block.x+1, block.y, block.z);
            west.vertices[1] = new Vertex(block.x+1, block.y, block.z+1);
            west.vertices[2] = new Vertex(block.x+1, block.y+1, block.z);
            west.vertices[3] = new Vertex(block.x+1, block.y+1, block.z+1);
            west.indexes[0]=0;
            west.indexes[1]=1;
            west.indexes[2]=2;
            west.indexes[3]=1;
            west.indexes[4]=3;
            west.indexes[5]=2;
            //EAST FACE
            Face east = new Face(Color.PINK);
            east.vertices[0] = new Vertex(block.x, block.y, block.z);
            east.vertices[1] = new Vertex(block.x, block.y+1, block.z);
            east.vertices[2] = new Vertex(block.x, block.y, block.z+1);
            east.vertices[3] = new Vertex(block.x, block.y+1, block.z+1);
            east.indexes[0]=0;
            east.indexes[1]=1;
            east.indexes[2]=2;
            east.indexes[3]=2;
            east.indexes[4]=1;
            east.indexes[5]=3;
            final Face[] faces = new Face[6];
            faces[2]=top; // BLUE
            faces[1]=bottom; // GREEN
            faces[3]=west; // ORANGE
            faces[4]=east; // PINK
            faces[5]=south; // GRIS
            faces[0]=north; // CYAN
            facesBuffer.add(faces);
        }

        public void render(Graphics g, Player player, Point2 windowCenter){
            for (Face[] faces : facesBuffer) {
                boolean stopRenderingCurrentBlock = false;
                //System.out.println("BLOCK");
                for (Face face : faces) {
                    if(face==null) continue;
                    g.setColor(face.color);
                    //System.out.println("FACE");
                    for(int i = 0; i < 2; i++){
                        Vertex v1 = face.vertices[face.indexes[3*i]];
                        Vertex v2 = face.vertices[face.indexes[3*i+1]];
                        Vertex v3 = face.vertices[face.indexes[3*i+2]];

                        // Check if we're facing the block, if not just don't try to render it
                        /*if(!isPlayerFacing(player, v1) || !isPlayerFacing(player, v1) || !isPlayerFacing(player, v1)) {
                            stopRenderingCurrentBlock = true;
                            break;
                        }*/
                        Point2 p1 = projectVertex(v1, player, windowCenter);
                        Point2 p2 = projectVertex(v2, player, windowCenter);
                        Point2 p3 = projectVertex(v3, player, windowCenter);
                        if(p1==null||p2==null||p3==null) break;
                        g.drawLine(p1.x, p1.y, p2.x, p2.y);
                        g.drawLine(p2.x, p2.y, p3.x, p3.y);
                        g.drawLine(p3.x, p3.y, p1.x, p1.y);
                    }
                    if(stopRenderingCurrentBlock) break;
                }
            }
            g.setColor(Color.MAGENTA);
            g.drawLine(0,0,windowCenter.x, windowCenter.y);
            g.drawLine(windowCenter.x, windowCenter.y, 1486, 363);
        }

        private Point2 projectVertex(Vertex v, Player player, Point2 windowCenter){
            int width = windowCenter.x*2;
            int height = windowCenter.y*2;
            final Point3 vertexPos = new Point3(v.position[0], v.position[1], v.position[2]);
            final Camera camera = player.camera;
            float yaw = camera.getYaw();
            float pitch = camera.getPitch();
            yaw = (float) Math.toRadians(yaw);
            pitch = (float) Math.toRadians(pitch);
            final float fov = camera.getFov();
            Matrix vertexPosition = new Matrix(new double[][]{ {vertexPos.x}, {vertexPos.y}, {vertexPos.z}, {1f} });
            //System.out.println("default"+vertexPosition);
            // Model matrix
            float near = 0.1f, far = 100f;
            float aspect = width / Math.max(1f, height);
            float top = (float) (Math.tan(Math.toRadians(fov*0.5f)) * near);
            float bottom = -top;
            float right = top * aspect;
            float left = -right;
            Matrix modelMatrixBis = new Matrix(new double[][]{
                    {(2.0f * near) / (right - left), 0, 0, 0},
                    {0, (2.0f * near) / (top - bottom), 0, 0},
                    {0, 0, -(far + near) / (far - near), -1},
                    {0, 0, (-2.0f * far * near) / (far - near), 0}
            }); //=> this is a bad glFrustum equivalent = a perspective matrix

            double translationX = 0f;
            double translationY = 0f;
            double translationZ = 0f;
            double scaleX = 1f;
            double scaleY = 1f;
            double scaleZ = 1f;
            Matrix modelMatrix = new Matrix(new double[][]{
                    {scaleX, 0, 0, translationX},
                    {0, scaleY, 0, translationY}, // 0, scaleY, 1, translationY
                    {0, 0, scaleZ, translationZ}, // 0, -1
                    {0, 0, 0, 1}
            });
            vertexPosition = modelMatrix.multiply(vertexPosition);
            //System.out.println("postModel"+vertexPosition);
            // View Matrix
            Vector3 front = new Vector3(Math.cos(pitch)*Math.sin(yaw), Math.sin(pitch), Math.cos(pitch)*Math.cos(yaw)).normalize();
            //front = new Vector3(Math.cos(yaw)*Math.cos(pitch), Math.sin(pitch), Math.sin(yaw)*Math.cos(pitch)).normalize();
            Vector3 position = new Vector3(player.x, player.y, player.z);
            Vector3 target = position.add(front);
            Vector3 z = target.substract(position).normalize();
            Vector3 x = Vector3.UP.cross(z).normalize();//z.product(Vector3.UP).normalize();
            Vector3 y = z.cross(x);
            Matrix viewMatrix = new Matrix(new double[][]{
                    {x.x, y.x, z.x, 0},
                    {x.y, y.y, z.y, 0},
                    {x.z, y.z, z.z, 0},
                    {-x.dot(position), -y.dot(position), -z.dot(position), 1}
            });
            /*Vector3 s = z.product(Vector3.UP);
            Vector3 u = s.normalize().product(z);
            viewMatrix = new Matrix(new double[][]{
                    {s.x, s.x, s.x, -s.dot(position)},
                    {u.y, u.y, u.y, -u.dot(position)},
                    {z.x, z.y, z.z, -z.dot(position)},
                    {0, 0, 0, 1}
            });*/

            Vector3 xaxis = new Vector3( -Math.cos(yaw), 0, -Math.sin(yaw));
            Vector3 yaxis = new Vector3(Math.sin(yaw)*Math.sin(pitch), Math.cos(pitch), Math.cos(yaw)*Math.sin(pitch));
            Vector3 zaxis = new Vector3(Math.sin(yaw)*Math.cos(pitch), -Math.sin(pitch), Math.cos(pitch)*Math.cos(yaw));
            System.out.println(String.format("Front: %s /Right: %s /Up: %s", zaxis, xaxis, yaxis));

            viewMatrix = new Matrix(new double[][]{
                    {xaxis.x, yaxis.x, zaxis.x, 0},
                    {xaxis.y, yaxis.y, zaxis.y, 0},
                    {xaxis.z, yaxis.z, zaxis.z, 0},
                    {-xaxis.dot(position), -yaxis.dot(position), -zaxis.dot(position), 1}
            });
            System.out.println(viewMatrix);

            //viewMatrix = viewMatrix.translate(-player.x, -player.y, -player.z);
            vertexPosition = viewMatrix.multiply(vertexPosition);
            //vertexPosition = vertexPosition.translate(-player.x, -player.y, -player.z);
            //            //System.out.println("postView"+vertexPosition);
            // Projection Matrix
            Matrix projectionMatrix = new Matrix(new double[][]{
                    {Math.atan(fov/2), 0, 0, 0},
                    {0, Math.atan(fov/2), 0, 0},
                    {0, 0, -((far+near)/(far-near)), -((2*near*far)/(far-near))},
                    {0, 0, -1, 0}
            });
            float rFov = (float) Math.toRadians(fov);
            aspect = (float) height/width;
            float f = (float) Math.tan(rFov/2);//(Math.cos(rFov/2)*Math.sin(rFov/2));
            near = 0.001f;
            far = 1000f;
            projectionMatrix = new Matrix(new double[][]{
                    {f/aspect, 0, 0, 0},
                    {0, f, 0, 0},
                    {0, 0, (far+near)/(near-far), (2*far*near)/(near-far)},
                    {0, 0, -1, 0}
            });
            f = 1.0f/f;
            float q = far/(far-near);
            projectionMatrix = new Matrix(new double[][]{
                    {aspect*f, 0, 0, 0},
                    {0, f, 0, 0},
                    {0, 0, q, 1},
                    {0, 0, -near*q, 0}
            });

            vertexPosition = projectionMatrix.multiply(vertexPosition);//projectionMatrix.multiply(viewMatrix).multiply(modelMatrix).multiply(vertexPosition);
            //System.out.println("postProj"+vertexPosition);

            double xx = vertexPosition.get(0, 0);
            double yy = vertexPosition.get(1, 0);
            double zz = vertexPosition.get(2, 0);
            double w = vertexPosition.get(3, 0);
            if(w!=0&&w!=1){
                xx /= w;
                yy /= w;
                zz /= w;
            }
            //System.out.println("preNorm"+xx+"/"+yy+"/"+zz+"/"+w);
            Vector3 pos = new Vector3(xx, yy, zz);
            //System.out.println("postNorm"+pos);
            //if (pos.x < -1 || pos.x > 1 || pos.y < -1 || pos.y > 1) return null;
            //final int posX = (int) (pos.x*windowCenter.x/pos.z+windowCenter.x);
            //final int posY = (int) (pos.y*windowCenter.y/pos.z+windowCenter.y);
            //System.out.println(pos.x+"/"+pos.y+"/"+windowCenter.x+";"+windowCenter.y+"/"+pos.z);
            int posX = (int) ((pos.x+1)*windowCenter.x);
            int posY = ((int) ((pos.y+1)*windowCenter.y));
            //System.out.println(posX+"/"+posY+"/"+(pos.x+1)* windowCenter.x+"/"+(pos.y+1)*windowCenter.y);
            //System.out.println(RenderAppMain.INSTANCE.getSize());
            return new Point2(posX, posY);
        }

        private Point2 getVertexScreenPositionFromCamView(Vertex v, Player player, Point2 windowCenter){
            int width = windowCenter.x*2;
            int height = windowCenter.y*2;
            final Point3 vertexPos = new Point3(v.position[0], v.position[1], v.position[2]);
            Camera camera = player.camera;
            float yaw = camera.getYaw();
            float pitch = camera.getPitch();
            // Model to World ????? Pas compris mais jcrois pas besoin perso
            Matrix m3 = new Matrix(new double[][]{
                    {vertexPos.x}, {vertexPos.y}, {vertexPos.z}, {1}
            });
            // World to View
            //Vector3 dir = new Vector3(Math.cos(yaw)-Math.sin(yaw), Math.sin(pitch)+Math.cos(pitch), 1);
            //Vector3 dir = new Vector3(Math.cos(pitch)*Math.cos(yaw), Math.sin(pitch), Math.cos(pitch)*Math.sin(-yaw));
            /*                         (gauche,en face, haut)
            lui : F=X / G=Y / H=Z   => (u,v,w)
            si  : F=-Z / G=-X / H=Y => (-v,w,-u) (ptit doute, ptet (-v,-w,u)
            moi : F=Z / G=X / H=Y => (v,-w,-u)   (donc ptet (v,w,u)

            et (u,v,w)= [cos(yaw)*cos(pitch) ; sin(yaw)*cos(pitch) ; sin(pitch)]
            donc pour moi (v,-w,-u) = [sin(yaw)*cos(pitch) ; -sin(pitch) ; -(cos(yaw)*cos(pitch))]
            mais ptet (v,w,u) = [sin(yaw)*cos(pitch) ; sin(pitch) ; cos(yaw)*cos(pitch)]
             */
            Vector3 dir = new Vector3(Math.cos(pitch)*Math.sin(yaw), Math.sin(pitch), Math.cos(pitch)*Math.cos(yaw));
            final Vector3 UP = Vector3.UP.mult(-1).normalize();
            final Vector3 F = new Vector3(player.x-dir.x, player.y-dir.y, player.z-dir.z).normalize();
            //final Vector3 F = new Vector3(dir.x-player.x, dir.y-player.y, dir.z-player.z).normalize();
            final Vector3 S = F.cross(UP).normalize();
            final Vector3 U = S.cross(F).normalize();
            // VIEW MATRIX
            Matrix m1 = new Matrix(new double[][]{
                    {S.x, S.y, S.z, 0},
                    {U.x, U.y, U.z, 0},
                    {-F.x, -F.y, -F.z, 0},
                    {0, 0, 0, 1}
            });
            // MODEL MATRIX
            Matrix m2 = new Matrix(new double[][]{
                    {1, 0, 0, -player.x},
                    {0, 1, 0, -player.y},
                    {0, 0, 1, -player.z},
                    {0, 0, 0, 1}
            });
            //region new code with proj matrix
            /*m3 = m2.multiply(m3);
            double w = m3.get(3, 0);
            // normalize if w is different than 1 (convert from homogeneous to Cartesian coordinates)
            if (w != 1) {
                Vector3 pos = new Vector3(m3.get(0,0)/w, m3.get(1,0)/w, m3.get(2, 0)/w);
                m3 = new Matrix(new double[][]{{pos.x}, {pos.y}, {pos.z}, {1}});
            }
            //m3 = m1.multiply(m3);
            // PROJECTION MATRIX????
            float near = 0.1f, far = 100f;
            float imageAspectRatio = (float) width / height;
            float scale = (float) (1 / Math.tan(camera.getFov() * 0.5 * Math.PI / 180))*near;
            float r = imageAspectRatio*scale, l = -r;
            float t = scale, b = -t;
            double[][] data = {
                    {2*near/ (r-l), 0, 0, 0},
                    {0, 2*near/(t-b), 0, 0},
                    {(r+l)/(r-l), (t+b)/(t-b), -(far+near)/(far-near), -1},
                    {0, 0, -2*far*near/(far-near), 0}
            };
            *//*OLD
            data[0][0] = scale;  // scale the x coordinates of the projected point
            data[1][1] = scale;  // scale the y coordinates of the projected point
            data[2][2] = -far / (far - near);  // used to remap z to [0,1]
            data[3][2] = -far * near / (far - near);  // used to remap z [0,1]
            data[2][3] = -1;  // set w = -z
            data[3][3] = 0;*//*
            Matrix projMat = new Matrix(data);
            m3 = projMat.multiply(m3);
            w = m3.get(3, 0);
            Vector3 pos = new Vector3(m3.get(0,0)/w, m3.get(1,0)/w, m3.get(2, 0)/w);

            //if (pos.x < -1 || pos.x > 1 || pos.y < -1 || pos.y > 1) continue;
            // convert to raster space and mark the position of the vertex in the image with a simple dot
            final int x = (int) Math.min(width - 1, (pos.x + 1) * 0.5 * width);
            final int y = (int) Math.min(height - 1, (1 - (pos.y + 1) * 0.5) * height);
            return new Point2(x, y);*/
            //endregion

            Matrix m4 = m1.multiply(m2).multiply(m3);
            double w = m4.get(3, 0);
            Vector3 pos = new Vector3(m4.get(0,0)/w, m4.get(1,0)/w, m4.get(2, 0)/w).normalize();
            // View to Projection
            final int x = (int) (pos.x*windowCenter.x/pos.z+windowCenter.x);
            final int y = (int) (pos.y*windowCenter.y/pos.z+windowCenter.y);
            return new Point2(x, y);
        }

        private boolean isPlayerFacing(Player player, Vertex vertex){
            float yaw = player.camera.getYaw();
            float pitch = player.camera.getPitch();
            Vector3 playerDirection = new Vector3(Math.cos(pitch)*Math.sin(yaw), Math.sin(pitch), Math.cos(pitch)*Math.cos(yaw));

            Point3 playerPos = new Point3(player.x, player.y, player.z);
            Point3 vertexPos = new Point3(vertex.position[0], vertex.position[1], vertex.position[2]);
            Vector3 playerToVertex = new Vector3(playerPos, vertexPos);

            double scalar = playerDirection.normalizeDot(playerToVertex);
            double radianAngle = Math.acos(scalar);
            double degreeAngle = Math.toDegrees(radianAngle);
            return degreeAngle < player.camera.getFov();
        }

        public void clearBuffer(){
            facesBuffer.clear();
        }

        class Face {
            public final Vertex[] vertices = new Vertex[4];
            public final int[] indexes = new int[6];
            public final Color color;//temp
            public Face(Color color){
                this.color = color;
            }
        }

        class Vertex {
            public final int[] position;
            public Vertex(int x, int y, int z){
                this.position = new int[]{x, y, z};
            }
        }
    }

    @Override
    public String toString() {
        return "Block{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}