package com.okseby.core;

import com.okseby.core.utils.Constants;
import org.lwjgl.Version;

public class Launcher {

    private static WindowManager window;
    private static EngineManager engine;

    public static void main(String[] args) {
        System.out.println("LWJGL Version: " + Version.getVersion());

        window = new WindowManager(Constants.title, 1600, 900, false);
        engine = new EngineManager();

        try {
            engine.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static WindowManager getWindow() {
        return window;
    }
}
