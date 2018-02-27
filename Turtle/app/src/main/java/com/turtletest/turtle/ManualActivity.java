package com.turtletest.turtle;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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
    private CardView manualBTbtn;
    private CardView obstacleBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.manualactivity, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        manualBTbtn = view.findViewById(R.id.manualBTbtn);
        obstacleBtn = view.findViewById(R.id.obstacleBtn);
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        BluetoothActivity act = (BluetoothActivity) getActivity();

        mConnectedThread = act.getThread();


        manualBTbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (mConnectedThread!= null) {
                    mConnectedThread.write("F");
                    Toast.makeText(getContext(), "F", Toast.LENGTH_SHORT).show();

                }
            }
        });

        obstacleBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (mConnectedThread!= null) {
                    mConnectedThread.write("O");
                    Toast.makeText(getContext(),"O", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

}
