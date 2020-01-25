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
    public static float radians(float degrees){
        return (float)(degrees* Math.PI/180);
    }
    public static float random(float l, float r){
        return (float)(Math.random()*(r-l) + l);
    }


}
