package com.okseby.core.test;

import com.okseby.core.*;
import com.okseby.core.entity.Entity;
import com.okseby.core.entity.Model;
import com.okseby.core.entity.Texture;
import com.okseby.core.utils.Constants;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class TestGame implements ILogic {
    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;

    private Entity entity;
    private Camera camera;

    Vector3f cameraInc;

    public TestGame() {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
    }

    @Override
    public void init() throws Exception {
        renderer.init();

        Model model = loader.loadOBJModel("/models/cube.obj");
        model.setTexture(new Texture(loader.loadTexture("res/textures/grassblock.jpg")));

        entity = new Entity(model, new Vector3f(0, 0, -5), new Vector3f(0, 0, 0), 1);
    }

    @Override
    public void input() {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW.GLFW_KEY_W))
            cameraInc.z = -1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_A))
            cameraInc.x = 1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_S))
            cameraInc.z = 1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_D))
            cameraInc.x = -1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_Z))
            cameraInc.y = 1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_X))
            cameraInc.y = -1;
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        camera.movePosition(cameraInc.x * Constants.cameraMoveSpeed, cameraInc.y * Constants.cameraMoveSpeed, cameraInc.z * Constants.cameraMoveSpeed);

        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotationVector = mouseInput.getDisplayVector();
            camera.moveRotation(rotationVector.x * Constants.mouseSensitivity, rotationVector.y * Constants.mouseSensitivity, 0);
        }

        entity.moveRotation(0.0f, 0.5f, 0.0f);
    }

    @Override
    public void render() {
        if (window.isResizeable()) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResizeable(true);
        }

        window.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        renderer.render(entity, camera);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
