package com.turtletest.turtle;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * MainActivity launched on start of app.
 */


public class MainActivity extends AppCompatActivity {

    //GUI Components
    private LinearLayout mainLayout;
    private AnimationDrawable animationbg;  //Background animation
    private Button bluetoothBtn;

    /* Connect GUI Components with layout, set onclick listeners
     * @param Bundle savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

        //Find GUI Components
        mainLayout = findViewById(R.id.main);
        bluetoothBtn = findViewById(R.id.bluetoothbtn);

        //Dynamic background variables
        animationbg = (AnimationDrawable) mainLayout.getBackground();
        animationbg.setEnterFadeDuration(2000);
        animationbg.setExitFadeDuration(2000);
        animationbg.start();

        //Set animation background to run for certain duration once
        long totalDuration = 0;
        for(int i = 0; i< animationbg.getNumberOfFrames();i++){
            totalDuration += animationbg.getDuration(i);
        }

        // timer for animation bg
        Timer timer = new Timer();

        //Stop animation when it finishes executing once
        TimerTask timerTask = new TimerTask(){
            @Override
            public void run() {

                animationbg.stop();
            }
        };
        timer.schedule(timerTask, totalDuration);

        //Open bluetoothActivity when button is pushed
        bluetoothBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getBluetoothActivity();
            }
        });
    }

    /* Opens bluetoothActivity.
     */
    public void getBluetoothActivity() {
        Intent intent = new Intent(getApplicationContext(), BluetoothActivity.class);
        startActivity(intent);
    }
}
