package com.akashd50.simulator.logic;

import android.content.Context;
import android.graphics.Color;

import com.akashd50.simulator.R;
import com.akashd50.simulator.Services.AppServices;
import com.akashd50.simulator.objects.ParticleSystem;
import com.akashd50.simulator.objects.ShaderProgram;
import com.akashd50.simulator.objects.Texture;
import com.akashd50.simulator.objects.TypePolygon;
import com.akashd50.simulator.objects.Vector;

public class SimulationHandler {
    public static final int BOUNCY = 1001;

    private int type;
    private ParticleSystem particleSystem;
    private Texture texture;
    private TypePolygon obstacle;

    public SimulationHandler(int type, Context context){
        this.type = type;

        ShaderProgram s = new ShaderProgram(context, R.raw.particle_shader_ii);
        particleSystem = new ParticleSystem(1000);
        particleSystem.setProgram(s);

        texture = new Texture("particle", context, R.raw.q_particle_iii);
        particleSystem.setTexture(texture);

        obstacle = new TypePolygon();
        for(float angle = 0;angle<=360;angle+=36) {
            float x = (float) (0 + Math.cos(AppServices.radians(angle)) * 0.3);
            float y = (float) (-1.3 + Math.sin(AppServices.radians(angle)) * 0.3);
            obstacle.addVertex(new Vector(x,y,0f));
        }
    }

    public void onDrawFrame(float[] mvpMatrix){
        particleSystem.addParticle(
                new Vector(0,1.0f,0.0f),
                Color.rgb(255,0,0),
                new Vector((float)(Math.random()*0.01f)-0.005f,AppServices.random(-0.01f,-0.005f),0.0f),
                new Vector(0,-0.0001f,0.0f));

        particleSystem.addParticle(new Vector(0,1.0f,0f), Color.rgb(255,0,0),
                new Vector((float)(Math.random()*0.01f)-0.005f,-(float)(Math.random()*0.01f),0.0f),
                new Vector(0,-0.0001f,0.0f));

        particleSystem.update(obstacle);
        particleSystem.onDrawFrame(mvpMatrix);
    }

}
