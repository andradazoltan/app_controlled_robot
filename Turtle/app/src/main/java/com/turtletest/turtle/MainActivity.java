package com.turtletest.turtle;


import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.opengl.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private LinearLayout mainLayout;
    private AnimationDrawable animationbg;
    private TextView loading_txt;
    private Button bluetoothBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);


        mainLayout = (LinearLayout) findViewById(R.id.main);
        bluetoothBtn = (Button) findViewById(R.id.bluetoothbtn);


        animationbg = (AnimationDrawable) mainLayout.getBackground();
        animationbg.setEnterFadeDuration(2000);
        animationbg.setExitFadeDuration(2000);
        animationbg.start();

        long totalDuration = 0;
        for(int i = 0; i< animationbg.getNumberOfFrames();i++){
            totalDuration += animationbg.getDuration(i);
        }
        Timer timer = new Timer();

        TimerTask timerTask = new TimerTask(){
            @Override
            public void run() {

                animationbg.stop();
            }
        };
        timer.schedule(timerTask, totalDuration);

        bluetoothBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getBluetoothActivity();
            }
        });


    }


    public void getBluetoothActivity() {
        Intent intent = new Intent(getApplicationContext(), BluetoothActivity.class);
        startActivity(intent);
    }


    private void openGyro(){
        Intent intent = new Intent(this,Gryo.class);
        startActivity(intent);
    }

}
