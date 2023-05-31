package com.okseby.core.test;

import com.okseby.core.*;
import com.okseby.core.entity.Entity;
import com.okseby.core.entity.Model;
import com.okseby.core.entity.Texture;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class TestGame implements ILogic {
    private int direction = 0;
    private float color = 0.0f;

    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;

    private Entity entity;

    public TestGame() {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
    }

    @Override
    public void init() throws Exception {
        renderer.init();

        float[] vertices = {
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f,
        };

        int[] indices = {
                0, 1, 3,
                3, 1, 2
        };

        float[] textureCoordinates = {
                0, 0,
                0, 1,
                1, 1,
                1, 0
        };

        Model model = loader.loadModel(vertices, textureCoordinates, indices);
        model.setTexture(new Texture(loader.loadTexture("res/textures/grassblock.png")));

        entity = new Entity(model, new Vector3f(1, 0, 0), new Vector3f(0, 0, 0), 1);
    }

    @Override
    public void input() {
        if (window.isKeyPressed(GLFW.GLFW_KEY_UP))
            direction = 1;
        else if (window.isKeyPressed(GLFW.GLFW_KEY_DOWN))
            direction = -1;
        else
            direction = 0;
    }

    @Override
    public void update() {
        color += direction * 0.01f;
        if (color > 1)
            color = 1.0f;
        else if (color <= 0)
            color = 0.0f;

        if (entity.getPosition().x < -1.5f)
            entity.getPosition().x = 1.5f;
        entity.getPosition().x -= 0.01f;
    }

    @Override
    public void render() {
        if (window.isResizeable()) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResizeable(true);
        }

        window.setClearColor(color, color, color, 0.0f);
        renderer.render(entity);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
