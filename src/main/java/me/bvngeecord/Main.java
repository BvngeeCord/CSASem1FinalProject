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
import java.util.Scanner;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {

	private long window;
	private Renderer renderer;
	private List<GameObject> gameObjects;

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter FPS (float): ");
		float framesPerSecond = scanner.nextFloat();
		System.out.print("Enter Updates per second (float): ");
		float updatesPerSecond = scanner.nextFloat();
		System.out.print("Enter seconds in between new cubes (float): ");
		float secondsPerNewCube = scanner.nextFloat();
		new Main().app(framesPerSecond, updatesPerSecond, secondsPerNewCube);
    }

	private void app(float framesPerSecond, float updatesPerSecond, float secondsPerNewCube) {
		initGlWindow();
		this.renderer = new Renderer(window);

		CubeGenerator cubeGenerator = new CubeGenerator();
		gameObjects = new ArrayList<>();
		List<Vector3f> motions = new ArrayList<>();
		List<Vector3f> rotations = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			gameObjects.add(cubeGenerator.generateRandomCubeObject());
			motions.add(cubeGenerator.generateRandomMotion());
			rotations.add(cubeGenerator.generateRandomRotation());
		}

		// Application loop
		long currentTime = System.currentTimeMillis();
		long lastUpdateTime = currentTime;
		long lastRenderTime = currentTime;
		long lastNewCubeTime = currentTime;
		while ( !glfwWindowShouldClose(window) ) {
			// Update game state (cube motion)
			if (System.currentTimeMillis() - lastUpdateTime >= (long) 1000 / updatesPerSecond) {
				for (int i = 0; i < gameObjects.size(); i++) {
					GameObject obj = gameObjects.get(i);
					obj.setPosition(obj.getPosition().add(motions.get(i)));
					obj.setRotation(obj.getRotation().add(rotations.get(i)));
				}
				lastUpdateTime = System.currentTimeMillis();
			}

			// Render scene
			if (System.currentTimeMillis() - lastRenderTime >= (long) 1000 / framesPerSecond) {
				renderer.render(gameObjects);
				lastRenderTime = System.currentTimeMillis();
			}

			// Add new cubes
			if ((System.currentTimeMillis() - lastNewCubeTime) / 1000f >= secondsPerNewCube) {
				gameObjects.add(cubeGenerator.generateRandomCubeObject());
				motions.add(cubeGenerator.generateRandomMotion());
				rotations.add(cubeGenerator.generateRandomRotation());
				lastNewCubeTime = System.currentTimeMillis();
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
			if ( key == GLFW_KEY_SPACE && action == GLFW_RELEASE )
				this.gameObjects.forEach(obj -> obj.setPosition(0, 0, -2));
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