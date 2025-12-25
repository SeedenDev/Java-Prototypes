package fr.seeden.renderer.world;

import fr.seeden.core.math.Vector3;
import fr.seeden.renderer.RenderAppMain;
import fr.seeden.renderer.ui.RenderAppKeybindings;

public class Player {

    public final Camera camera = new Camera();

    public float x, y, z;
    private float speed = 1.5f;

    public Player() {
        this.x = 0;
        this.y = 2;
        this.z = 0;
    }

    public void handleMovement() {
        double cos = Math.cos(camera.getYaw());
        double sin = Math.sin(camera.getYaw());
        Vector3 direction = Vector3.ZERO;
        if(RenderAppKeybindings.FORWARD.isPressed()) direction = direction.add(new Vector3(-sin, 0, cos)); // +Z
        if(RenderAppKeybindings.BACKWARD.isPressed()) direction = direction.add(new Vector3(sin, 0, -cos)); // -Z
        if(RenderAppKeybindings.LEFT.isPressed()) direction = direction.add(new Vector3(cos, 0, sin)); // +X
        if(RenderAppKeybindings.RIGHT.isPressed()) direction = direction.add(new Vector3(-cos, 0, -sin)); // -X
        if(RenderAppKeybindings.UP.isPressed()) direction = direction.add(Vector3.UP);
        if(RenderAppKeybindings.DOWN.isPressed()) direction = direction.add(Vector3.UP.mult(-1));

        Vector3 pos = new Vector3(x, y, z).add(direction.normalize().mult(speed*(RenderAppMain.DELTA_TIME/500)));
        x = pos.x;
        y = pos.y;
        z = pos.z;
    }

    public void setSpeed(float speed) {
        this.speed = Math.clamp(speed, 0, 50);
    }

    public float getSpeed() {
        return speed;
    }
}