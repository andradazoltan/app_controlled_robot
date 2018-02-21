package com.turtletest.turtle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;


/**
 * Created by Grace on 2018-02-19.
 */

public class ActionActivity extends Fragment{
    private CardView aboutUsButton;
    private CardView lightsButton;
    private CardView bluetoothButton;
    private CardView autoButton;
    private CardView manualButton;
    public ConnectedThread thread;


    private AboutUs aboutus;
    private LightsActivity lightactivity;
    private ManualActivity manualactivity;
    private BluetoothActivity bluetoothactivity;
    private Gryo autoactivity;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.actionscreen, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        aboutUsButton = (CardView) view.findViewById(R.id.aboutusBtn);
        lightsButton = (CardView) view.findViewById(R.id.light);
        bluetoothButton = (CardView) view.findViewById(R.id.bluetooth);
        autoButton = (CardView) view.findViewById(R.id.automatic);
        manualButton = (CardView) view.findViewById(R.id.manual);

    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        fragmentManager = getChildFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        BluetoothActivity act = (BluetoothActivity) getActivity();
        thread = act.getThread();

        aboutUsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                aboutus = new AboutUs();
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(android.R.id.content, aboutus).addToBackStack(null).commit();
            }
        });

        lightsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                lightactivity = new LightsActivity();
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(android.R.id.content, lightactivity).addToBackStack(null).commit();
            }
        });
        bluetoothButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getBluetoothActivity();
            }
        });
        manualButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                autoactivity = new Gryo();
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(android.R.id.content, autoactivity).addToBackStack(null).commit();
            }
        });
        autoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                manualactivity = new ManualActivity();
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(android.R.id.content, manualactivity).addToBackStack(null).commit();
            }
        });
    }


    public void getBluetoothActivity() {
        Intent intent = new Intent(getContext(), BluetoothActivity.class);
        startActivity(intent);
    }
}


