package com.okseby.core;

import com.okseby.core.utils.Constants;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class EngineManager {
    public static final long nanosecond = 1000000000;
    public static final float framerate = 1000;

    private static int fps;
    private static float frametime = 1.0f / framerate;
    public static float currentFrameTime = 0;

    private boolean isRunning;

    private WindowManager windowManager;
    private MouseInput mouseInput;
    private GLFWErrorCallback errorCallback;
    private ILogic gameLogic;

    private void init() throws Exception {
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        windowManager = Launcher.getWindow();
        gameLogic = Launcher.getGame();
        mouseInput = new MouseInput();

        windowManager.init();
        gameLogic.init();
        mouseInput.init();
    }

    public void start() throws Exception {
        init();

        if (isRunning)
            return;

        run();
    }

    public void run() {
        this.isRunning = true;

        int frames = 0;
        long frameCounter = 0;
        long lastTime = System.nanoTime();
        double unprocessedTime = 0;

        while (isRunning) {
            boolean render = false;
            long startTime = System.nanoTime();
            long passedTime = startTime - lastTime;

            lastTime = startTime;
            unprocessedTime += passedTime / (double) nanosecond;
            frameCounter += passedTime;

            input();

            while (unprocessedTime > frametime) {
                render = true;
                unprocessedTime -= frametime;

                if (windowManager.windowShouldClose())
                    stop();

                if (frameCounter >= nanosecond) {
                    setFps(frames);

                    currentFrameTime = 1.0f / fps;
                    windowManager.setTitle(Constants.title + " - FPS: " + getFps());

                    frames = 0;
                    frameCounter = 0;
                }
            }

            if (render) {
                update(frametime);
                render();
                frames++;
            }
        }

        cleanup();
    }

    private void stop() {
        if (!isRunning)
            return;
        isRunning = false;
    }

    private void input() {
        mouseInput.input();
        gameLogic.input();
    }

    private void render() {
        gameLogic.render();
        windowManager.update();
    }

    private void update(float interval) {
        gameLogic.update(interval, mouseInput);
    }

    private void cleanup() {
        windowManager.cleanup();
        gameLogic.cleanup();

        errorCallback.free();

        GLFW.glfwTerminate();
    }

    public static int getFps() {
        return fps;
    }

    public static void setFps(int fps) {
        EngineManager.fps = fps;
    }
}
