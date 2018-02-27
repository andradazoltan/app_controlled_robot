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
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;


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

    private Button startstopbtn;

    private BluetoothActivity act;

    private SeekBar seekbar;

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

        startstopbtn = (Button) view.findViewById(R.id.startstopbtn);

        seekbar = (SeekBar) view.findViewById(R.id.seekbar);
    }


    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        fragmentManager = getChildFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        act = (BluetoothActivity) getActivity();
        seekbar.setEnabled(false);

        thread = act.getThread();
        if (thread!= null) {
            thread.write("andrada");
            Toast.makeText(getContext(), "hi", Toast.LENGTH_SHORT).show();
        }

        aboutUsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                aboutus = new AboutUs();
                disableCards(false);
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(android.R.id.content, aboutus).addToBackStack(null).commit();
            }
        });

        lightsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                lightactivity = new LightsActivity();
                seekbar.setEnabled(true);
                disableCards(false);
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
                disableCards(false);
                act.changeButtonVisibility(true, startstopbtn);
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(android.R.id.content, autoactivity).addToBackStack(null).commit();
            }
        });
        autoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                manualactivity = new ManualActivity();
                disableCards(false);
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(android.R.id.content, manualactivity).addToBackStack(null).commit();
            }
        });
    }

    public void getBluetoothActivity() {
        Intent intent = new Intent(getContext(), BluetoothActivity.class);
        startActivity(intent);
    }

    public void disableCards(boolean visible){
        act.changeCardVisibility(visible, aboutUsButton);
        act.changeCardVisibility(visible, autoButton);
        act.changeCardVisibility(visible, manualButton);
        act.changeCardVisibility(visible, lightsButton);
        act.changeCardVisibility(visible, bluetoothButton);
        act.changeButtonVisibility(visible, startstopbtn);
    }


}


