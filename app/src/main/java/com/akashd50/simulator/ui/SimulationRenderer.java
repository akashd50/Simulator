package com.akashd50.simulator.ui;


import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.akashd50.simulator.R;
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
    ParticleSystem particleSystem;
    Texture texture;
    TypePolygon obstacle;

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


        //GLES30.glEnable( GLES30.GL_DEPTH_TEST );
        //GLES30.glDepthFunc( GLES30.GL_LESS);
        //GLES30.glEnable(GLES30.GL_CULL_FACE);

        ShaderProgram s = new ShaderProgram(context, R.raw.particle_shader_ii);
        particleSystem = new ParticleSystem(1000);
        particleSystem.setProgram(s);

        texture = new Texture("particle", context, R.raw.q_particle_iii);
        particleSystem.setTexture(texture);

        //obstacle = new TypePolygon(new Vector(0f,-0.5f,0f), new Vector(0.5f,0.1f,0f));
        obstacle = new TypePolygon();
        //obstacle.addVertex(new Vector(0f,-0.9f,0f));
       // obstacle.addVertex(new Vector(-0.5f,-1.1f,0f));
       // obstacle.addVertex(new Vector(0f,-1.3f,0f));
       // obstacle.addVertex(new Vector(0.5f,-1.1f,0f));

        for(float angle = 0;angle<=360;angle+=36) {
            float x = (float) (0 + Math.cos(radians(angle)) * 0.3);
            float y = (float) (-1.3 + Math.sin(radians(angle)) * 0.3);
            obstacle.addVertex(new Vector(x,y,0f));
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClearColor(((float)0/255), (float)0/255, (float)0/255,1f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        camera.updateView();
        float[] mainMatrix = camera.getMVPMatrix();

       /* particleSystem.addParticle(new Vector(0f,0f,0f), Color.rgb(255,0,0),
                new Vector((float)(Math.random()*0.2f)-0.1f,-(float)(Math.random()*0.1f),0.0f),
                new Vector(0,-0.1f,0.0f));*/

        particleSystem.addParticle(
                new Vector(0,1.0f,0.0f),
                Color.rgb(255,0,0),
                new Vector((float)(Math.random()*0.01f)-0.005f,random(-0.01f,-0.005f),0.0f),
                new Vector(0,-0.0001f,0.0f));

        particleSystem.addParticle(new Vector(0,1.0f,0f), Color.rgb(255,0,0),
                new Vector((float)(Math.random()*0.01f)-0.005f,-(float)(Math.random()*0.01f),0.0f),
                new Vector(0,-0.0001f,0.0f));

        particleSystem.update(obstacle);
        particleSystem.onDrawFrame(mainMatrix);
    }

    public float random(float l, float r){
        return (float)(Math.random()*(r-l) + l);
    }

    public float radians(float degrees){
        return (float)(degrees* Math.PI/180);
    }
}
