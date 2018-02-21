package com.turtletest.turtle;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Grace on 2018-02-19.
 */

public class ManualActivity extends Fragment{

    private ConnectedThread mConnectedThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.manualactivity, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState){

    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);


        BluetoothActivity act = (BluetoothActivity) getActivity();
        mConnectedThread = act.getThread();

    }

}
