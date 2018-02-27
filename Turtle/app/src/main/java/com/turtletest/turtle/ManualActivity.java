package com.turtletest.turtle;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Grace on 2018-02-19.
 */

public class ManualActivity extends Fragment{

    private ConnectedThread mConnectedThread;
    private Button manualBTbtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.manualactivity, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        manualBTbtn = view.findViewById(R.id.manualBTbtn);
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        BluetoothActivity act = (BluetoothActivity) getActivity();

        mConnectedThread = act.getThread();
        if (mConnectedThread!= null) {
            //mConnectedThread.write("A");
        }

        manualBTbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getContext(), "manual", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
