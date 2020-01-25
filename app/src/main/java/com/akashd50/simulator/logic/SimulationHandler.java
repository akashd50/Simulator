package com.akashd50.simulator.logic;

import com.akashd50.simulator.objects.ParticleSystem;

public class SimulationHandler {
    public static final int BOUNCY = 1001;

    private int type;
    private ParticleSystem particleSystem;

    public SimulationHandler(int type){
        this.type = type;

    }

}
