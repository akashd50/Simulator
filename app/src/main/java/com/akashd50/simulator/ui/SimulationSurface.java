package com.akashd50.simulator.ui;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.View;

public class SimulationSurface extends GLSurfaceView {
    private SimulationRenderer renderer;

    public SimulationSurface(Context context){
       super(context);

        this.setEGLContextClientVersion(3);
        this.setEGLConfigChooser(8,8,8,8,24,8);
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
        this.setSystemUiVisibility(uiOptions);

        renderer = new SimulationRenderer(context);

        setRenderer(renderer);
    }

    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onResume() {
        super.onResume();

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
        this.setSystemUiVisibility(uiOptions);
    }
}
