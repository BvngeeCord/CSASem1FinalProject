package me.bvngeecord;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL20.*;

public class Renderer {

    private static final float FOV = (float) Math.toRadians(100.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;

    private ShaderProgram shaderProgram;
    private UniformsMap uniformsMap;
    private long window;
    private int width;
    private int height;
    private float aspectRatio;
    private Matrix4f projectionMatrix;
    private Transformation transformation;

    public Renderer(long window) {
        this.window = window;
        int[] arrWidth = new int[1];
        int[] arrHeight = new int[1];
        glfwGetFramebufferSize(window, arrWidth, arrHeight);
        this.width = arrWidth[0];
        this.height = arrHeight[0];
        this.aspectRatio = (float) width / height;
        this.projectionMatrix = new Matrix4f().perspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
        this.transformation = new Transformation();
        createShaderProgram();
        createUniformsMap();
    }

    public void render(List<GameObject> gameObjects) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shaderProgram.bind();

        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, width, height, Z_NEAR, Z_FAR);
        uniformsMap.setUniform("projectionMatrix", projectionMatrix);

        for (GameObject gameObject : gameObjects) {
            Matrix4f worldMatrix = transformation.getWorldMatrix(
                    gameObject.getPosition(),
                    gameObject.getRotation(),
                    gameObject.getScale()
            );
            uniformsMap.setUniform("worldMatrix", worldMatrix);

//            glEnableVertexAttribArray(0);
//            glEnableVertexAttribArray(1);
            GL30.glBindVertexArray(gameObject.getMesh().getVaoId());
            glDrawElements(GL_TRIANGLES, gameObject.getMesh().getVertexCount(), GL_UNSIGNED_INT, 0);
//            glDisableVertexAttribArray(0);
//            glDisableVertexAttribArray(1);
        }

        GL30.glBindVertexArray(0);

        shaderProgram.unbind();

        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    public void cleanup() {
        shaderProgram.cleanup();
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        this.aspectRatio = (float) width / height;
        this.projectionMatrix = new Matrix4f().perspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
    }

    private void createShaderProgram() {
        List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData(
                "resources/shaders/scene.vert", GL_VERTEX_SHADER
        ));
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData(
                "resources/shaders/scene.frag", GL_FRAGMENT_SHADER
        ));
        this.shaderProgram = new ShaderProgram(shaderModuleDataList);
    }

    private void createUniformsMap() {
        this.uniformsMap = new UniformsMap(this.shaderProgram.getProgramId());
        this.uniformsMap.createUniform("projectionMatrix");
        this.uniformsMap.createUniform("worldMatrix");
    }

}
