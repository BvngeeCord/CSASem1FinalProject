package me.bvngeecord;

import org.joml.Vector3f;

public class GameObject {

    private final Mesh mesh;

    private final Vector3f position;
    private float scale;
    private final Vector3f rotation;

    public GameObject(Mesh mesh) {
        this.mesh = mesh;
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0, 0, 0);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public void setPosition(Vector3f vec) {
        this.position.x = vec.x;
        this.position.y = vec.y;
        this.position.z = vec.z;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public void setRotation(Vector3f vec) {
        this.rotation.x = vec.x;
        this.rotation.y = vec.y;
        this.rotation.z = vec.z;
    }

    public Mesh getMesh() {
        return mesh;
    }
}