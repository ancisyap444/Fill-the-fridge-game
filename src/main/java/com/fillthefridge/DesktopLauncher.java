package com.fillthefridge;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] args) {
        boolean smokeTest = false;
        if (args != null) {
            for (String arg : args) {
                if ("--smoke-test".equalsIgnoreCase(arg)) {
                    smokeTest = true;
                    break;
                }
            }
        }

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Fill The Fridge - Roguelike Sorting Puzzle");
        config.setWindowedMode(1280, 720);
        config.setForegroundFPS(60);
        config.useVsync(true);
        config.setResizable(true);

        new Lwjgl3Application(new FillTheFridgeGame(smokeTest), config);
    }
}
