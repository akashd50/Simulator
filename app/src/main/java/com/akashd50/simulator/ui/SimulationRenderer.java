package com.akashd50.simulator.ui;


import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.akashd50.simulator.R;
import com.akashd50.simulator.logic.SimulationHandler;
import com.akashd50.simulator.objects.Camera;
import com.akashd50.simulator.objects.TypePolygon;
import com.akashd50.simulator.objects.ParticleSystem;
import com.akashd50.simulator.objects.ShaderProgram;
import com.akashd50.simulator.objects.Texture;
import com.akashd50.simulator.objects.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SimulationRenderer implements GLSurfaceView.Renderer {
    private static final float[] mProjectionMatrix = new float[16];
    private static Camera camera;
    private Context context;

    SimulationHandler bouncySim;

    public SimulationRenderer(Context context){
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        float ratio = (float) height/width;

        camera = new Camera();
        camera.setAdditionalParms(width, height);
        Matrix.orthoM(mProjectionMatrix, 0, -1,1,-ratio, ratio, 1,30);

        camera.setMatrices(new float[16],mProjectionMatrix,new float[16]);
        camera.setPosition(new Vector(0f,0f,5f));
        camera.lookAt(new Vector(0f,0f,0f));

        bouncySim = new SimulationHandler(0,context);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClearColor(((float)0/255), (float)0/255, (float)0/255,1f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        camera.updateView();
        float[] mainMatrix = camera.getMVPMatrix();
        
        bouncySim.onDrawFrame(mainMatrix);
    }

    public float random(float l, float r){
        return (float)(Math.random()*(r-l) + l);
    }

    public float radians(float degrees){
        return (float)(degrees* Math.PI/180);
    }
}
