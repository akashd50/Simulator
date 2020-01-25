package com.akashd50.simulator.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SimulationActivity extends AppCompatActivity {
    private SimulationSurface surface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        surface = new SimulationSurface(this);
        //getActionBar().hide();
        setContentView(surface);
    }
}
