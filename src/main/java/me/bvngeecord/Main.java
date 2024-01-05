package me.bvngeecord;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {

	private static final long FRAMES_PER_SECOND = 60;
	private static final long UPDATES_PER_SECOND = 20;
	private long window;
	private Renderer renderer;

	public static void main(String[] args) {
		new Main().app();
    }

	private void app() {
		initGlWindow();
		this.renderer = new Renderer(window);

		CubeGenerator cubeGenerator = new CubeGenerator();
		List<GameObject> gameObjects = new ArrayList<>();
		List<Vector3f> motions = new ArrayList<>();
		List<Vector3f> rotations = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			gameObjects.add(cubeGenerator.generateRandomCubeObject());
			motions.add(cubeGenerator.generateRandomMotion());
			rotations.add(cubeGenerator.generateRandomRotation());
		}

		// Application loop
		long lastUpdateTime = System.currentTimeMillis();
		while ( !glfwWindowShouldClose(window) ) {
			if (System.currentTimeMillis() - lastUpdateTime >= (long) 1000 / UPDATES_PER_SECOND) {
				for (int i = 0; i < gameObjects.size(); i++) {
					GameObject obj = gameObjects.get(i);
					obj.setPosition(obj.getPosition().add(motions.get(i)));
					obj.setRotation(obj.getRotation().add(rotations.get(i)));
				}
			}

			if (System.currentTimeMillis() - lastUpdateTime >= (long) 1000 / UPDATES_PER_SECOND) {
				renderer.render(gameObjects);
			}
		}

		gameObjects.forEach(o -> o.getMesh().cleanUp());
		renderer.cleanup();
		this.cleanup();
	}

	private void initGlWindow() {
		GLFWErrorCallback.createPrint(System.err).set();

		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

		this.window = glfwCreateWindow(960, 540, "CSA Sem1 Project", glfwGetPrimaryMonitor(), NULL);
		if (window == NULL) throw new RuntimeException("Failed to create the GLFW window");

		this.setCallbacks();

		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);

			glfwGetWindowSize(window, pWidth, pHeight);

			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		}

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1); // Enable v-sync
		glfwShowWindow(window);

		GL.createCapabilities();

		glEnable(GL_DEPTH_TEST);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	}

	private void setCallbacks() {
		glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true);
		});
		glfwSetFramebufferSizeCallback(window, (win, width, height) -> {
			renderer.resize(width, height);
			glViewport(0, 0, width, height);
		});
	}

	private void cleanup() {
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		GLFWErrorCallback callback = glfwSetErrorCallback(null);
        if (callback != null) {
            callback.free();
        }
	}

}