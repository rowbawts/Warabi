package com.okseby.core;

import com.okseby.core.test.TestGame;
import com.okseby.core.utils.Constants;
import org.lwjgl.Version;

public class Launcher {

    private static WindowManager window;
    private static TestGame game;

    public static void main(String[] args) {
        System.out.println("LWJGL Version: " + Version.getVersion());

        window = new WindowManager(Constants.title, 1600, 900, false);
        game = new TestGame();

        EngineManager engine = new EngineManager();

        try {
            engine.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static WindowManager getWindow() {
        return window;
    }

    public static TestGame getGame() {
        return game;
    }
}