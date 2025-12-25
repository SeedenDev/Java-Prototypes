package fr.seeden.renderer.world;

public class Camera {

    public static final float MIN_PITCH = -90;
    public static final float MAX_PITCH = -MIN_PITCH;

    private float yaw, pitch;
    private float fov = 90;
    private float sensitivity = 0.015f; //0.025f;

    public Camera() {
        this.yaw = 0;
        this.pitch = 0;
    }

    private float smoothedDeltaX = 0f;
    private float smoothedDeltaY = 0f;
    private final float smoothing = 0.1f; // Plus petit = plus fluide (0.05 à 0.2 en général)

    public void rotate(int deltaX, int deltaY){
        smoothedDeltaX += (deltaX - smoothedDeltaX) * smoothing;
        smoothedDeltaY += (deltaY - smoothedDeltaY) * smoothing;

        float newYaw = yaw + (float) (smoothedDeltaX)*sensitivity;
        float newPitch = (float) (pitch + -smoothedDeltaY *(sensitivity-0.01));
        //newYaw = newYaw>180 ? -180+newYaw%180 : newYaw< -180 ? 180+newYaw%180 : newYaw;
        float min = Math.min(newYaw, yaw);
        float max = Math.max(newYaw, yaw);
        this.yaw = newYaw%360;//MathUtil.lerp(yaw, newYaw, MathUtil.inverseLerp(-180, 180, newYaw));
        this.pitch = Math.clamp(newPitch, MIN_PITCH, MAX_PITCH);
    }

    public void setFov(float fov) {
        this.fov = Math.clamp(fov, 10, 180);
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public float getFov() {
        return fov;
    }
}