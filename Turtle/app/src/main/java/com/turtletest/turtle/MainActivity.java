package com.turtletest.turtle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private LinearLayout mainLayout;
    private AnimationDrawable animationbg;
    private Button connectBtn;
    private ProgressBar spinner;
    private ValueAnimator animator;
    private TextView loading_txt;
    private Button bluetoothBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        loading_txt=(TextView) findViewById(R.id.loading_txt);
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        connectBtn = (Button) findViewById(R.id.connect_btn);
        mainLayout = (LinearLayout) findViewById(R.id.main);
        animator = ValueAnimator.ofInt(0, spinner.getMax());
        spinner.setVisibility(View.GONE);
        loading_txt.setVisibility(View.GONE);
        bluetoothBtn = (Button) findViewById(R.id.bluetoothbtn);

        connectBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                connectBtn.setVisibility(View.GONE);
                bluetoothBtn.setVisibility(View.GONE);
                loading_txt.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);

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

                        openGyro();
                    }
                };
          timer.schedule(timerTask, totalDuration);

            }
        });

        bluetoothBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getBluetoothActivity();
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation){
                spinner.setProgress((Integer)animation.getAnimatedValue());
            }
        });


        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                openGyro();
            }
        });

    }
    private void animate(){

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
