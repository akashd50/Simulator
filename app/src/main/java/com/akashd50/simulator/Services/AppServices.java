package com.akashd50.simulator.Services;

public class AppServices {
    private static int screenWidth, screenHeight;

    public void setScreenVariables(int sw, int sh){
        screenHeight = sh;
        screenWidth = sw;
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }
}
