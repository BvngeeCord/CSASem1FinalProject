package me.bvngeecord;

import org.joml.Vector3f;

import java.util.Random;

public class CubeGenerator {

    private Random random;

    public CubeGenerator() {
        this.random = new Random();
    }

    public GameObject generateRandomCubeObject() {
        GameObject cube = new GameObject(new Mesh(
                defaultPositions(),
                defaultColors(),
                defaultIndices()
        ));
        cube.setPosition(
                random.nextFloat(-5, 5),
                random.nextFloat(-5, 5),
                random.nextFloat(-7, -2)
        );
        cube.setScale(random.nextFloat(0.3f, 0.5f));
        cube.setRotation(
                random.nextFloat(-90, 90),
                random.nextFloat(-90, 90),
                random.nextFloat(-90, 90)
        );
        return cube;
    }

    public Vector3f generateRandomMotion() {
        return new Vector3f (
                random.nextFloat(-0.02f, 0.02f),
                random.nextFloat(-0.02f, 0.02f),
                random.nextFloat(-0.02f, 0.02f)
        );
    }

    public Vector3f generateRandomRotation() {
        return new Vector3f (
                random.nextFloat(-0.8f, 0.8f),
                random.nextFloat(-0.8f, 0.8f),
                random.nextFloat(-0.8f, 0.8f)
        );
    }

    private static float[] defaultPositions() {
        return new float[] {
            // VO
            -0.5f,  0.5f,  0.5f,
            // V1
            -0.5f, -0.5f,  0.5f,
            // V2
            0.5f, -0.5f,  0.5f,
            // V3
            0.5f,  0.5f,  0.5f,
            // V4
            -0.5f,  0.5f, -0.5f,
            // V5
            0.5f,  0.5f, -0.5f,
            // V6
            -0.5f, -0.5f, -0.5f,
            // V7
            0.5f, -0.5f, -0.5f,
        };
    }

    private static int[] defaultIndices() {
        return new int[] {
            // Front face
            0, 1, 3, 3, 1, 2,
            // Top Face
            4, 0, 3, 5, 4, 3,
            // Right face
            3, 2, 7, 5, 3, 7,
            // Left face
            6, 1, 0, 6, 0, 4,
            // Bottom face
            2, 1, 6, 2, 6, 7,
            // Back face
            7, 6, 4, 7, 4, 5,
        };
    }

    private static float[] defaultColors() {
        return new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        };
    }
}
