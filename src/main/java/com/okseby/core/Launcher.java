package com.okseby.core;

import org.lwjgl.Version;

public class Launcher {
    public static void main(String[] args) {
        System.out.println("LWJGL Version: " + Version.getVersion());

        WindowManager window = new WindowManager("Warabi Engine", 1600, 900, false);
        window.init();

        while (!window.windowShouldClose())
            window.update();

        window.cleanup();
    }
}
