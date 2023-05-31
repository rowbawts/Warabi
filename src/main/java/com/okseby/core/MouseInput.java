package com.okseby.core;

import lombok.Getter;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class MouseInput {
    private final Vector2d previousPosition, currentPosition;
    @Getter private final Vector2f displayVector;

    @Getter private boolean inWindow = false, leftButtonPressed = false, rightButtonPressed = false;

    public MouseInput() {
        previousPosition = new Vector2d(-1, -1);
        currentPosition = new Vector2d(0, 0);
        displayVector = new Vector2f();
    }

    public void init() {
        GLFW.glfwSetCursorPosCallback(Launcher.getWindow().getWindow(), (window, xpos, ypos) -> {
            currentPosition.x = xpos;
            currentPosition.y = ypos;
        });

        GLFW.glfwSetCursorEnterCallback(Launcher.getWindow().getWindow(), (window, entered) -> {
            inWindow = entered;
        });

        GLFW.glfwSetMouseButtonCallback(Launcher.getWindow().getWindow(), (window, buton, action, mods) -> {
            leftButtonPressed = buton == GLFW.GLFW_MOUSE_BUTTON_LEFT && action == GLFW.GLFW_PRESS;
            rightButtonPressed = buton == GLFW.GLFW_MOUSE_BUTTON_RIGHT && action == GLFW.GLFW_PRESS;
        });
    }

    public void input() {
        displayVector.x = 0;
        displayVector.y = 0;

        if (previousPosition.x > 0 && previousPosition.y > 0 && inWindow) {
            double x = currentPosition.x - previousPosition.x;
            double y = currentPosition.y - previousPosition.y;

            boolean rotateX = x != 0;
            boolean rotateY = y != 0;

            if (rotateX)
                displayVector.y = (float) x;
            if (rotateY)
                displayVector.x = (float) y;
        }

        previousPosition.x = currentPosition.x;
        previousPosition.y = currentPosition.y;
    }
}
