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
import android.widget.Toast;


/**  ActionActivity is a fragment child of parent BluetoothActivity.
 *  The user can switch between different functions here.
 */

public class ActionActivity extends Fragment{
    private CardView aboutUsButton;
    private CardView lightsButton;
    private CardView bluetoothButton;
    private CardView autoButton;
    private CardView manualButton;
    public ConnectedThread thread;

    private Button mStart;
    private Button mScanBtn;
    private Button mOffBtn;
    private Button mListPairedDevicesBtn;
    private Button mDiscoverBtn;

    private Button startstopbtn;
    private CardView followBtn;
    private CardView obstacleBtn;

    private BluetoothActivity act;

    private AboutUs aboutus;
    private LightsActivity lightactivity;
    private ManualActivity manualactivity;
    private Gryo autoactivity;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.actionscreen, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        aboutUsButton = view.findViewById(R.id.aboutusBtn);
        lightsButton = view.findViewById(R.id.light);
        bluetoothButton = view.findViewById(R.id.bluetooth);
        autoButton = view.findViewById(R.id.automatic);
        manualButton = view.findViewById(R.id.manual);
        startstopbtn = view.findViewById(R.id.startstopbtn);
        followBtn = view.findViewById(R.id.manualBTbtn);
        obstacleBtn = view.findViewById(R.id.obstacleBtn);
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        fragmentManager = getChildFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        //Get BluetoothActivity
        act = (BluetoothActivity) getActivity();
        mStart = act.findViewById(R.id.startbtn);
        mScanBtn = act.findViewById(R.id.scan);
        mOffBtn = act.findViewById(R.id.off);
        mListPairedDevicesBtn = act.findViewById(R.id.PairedBtn);
        mDiscoverBtn = act.findViewById(R.id.discover);
        thread = act.getThread(); // Get bluetooth thread
        if (thread!= null) {
            Toast.makeText(getContext(), "hi", Toast.LENGTH_SHORT).show();
        }
        //Set onClick to Group info screen
        aboutUsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                aboutus = new AboutUs();
                disableCards(false);
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(android.R.id.content, aboutus).addToBackStack(null).commit();
            }
        });
        //Set onClick to lights screen
        lightsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                lightactivity = new LightsActivity();
                disableCards(false);
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(android.R.id.content, lightactivity).addToBackStack(null).commit();
            }
        });
        //Set onClick to bluetooth screen
        bluetoothButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getBluetoothActivity();
            }
        });
        //Set onClick to gyro screen
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
        //Set onClick to auto screen
        autoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                manualactivity = new ManualActivity();
                disableCards(false);
                act.changeCardVisibility(true, followBtn);
                act.changeCardVisibility(true, obstacleBtn);
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(android.R.id.content, manualactivity).addToBackStack(null).commit();
            }
        });
    }

    public void getBluetoothActivity() {
        Intent intent = new Intent(getContext(), BluetoothActivity.class);
        startActivity(intent);
    }

    /* Enable/disable all GUI Components
     */
    public void disableCards(boolean visible){
        act.changeCardVisibility(visible, aboutUsButton);
        act.changeCardVisibility(visible, autoButton);
        act.changeCardVisibility(visible, manualButton);
        act.changeCardVisibility(visible, lightsButton);
        act.changeCardVisibility(visible, bluetoothButton);
        act.changeButtonVisibility(visible, startstopbtn);
        act.changeCardVisibility(visible, followBtn);
        act.changeCardVisibility(visible, obstacleBtn);
        act.changeButtonVisibility(visible, mDiscoverBtn);
        act.changeButtonVisibility(visible, mListPairedDevicesBtn);
        act.changeButtonVisibility(visible, mOffBtn);
        act.changeButtonVisibility(visible, mScanBtn);
        act.changeButtonVisibility(visible, mStart);
    }

}


