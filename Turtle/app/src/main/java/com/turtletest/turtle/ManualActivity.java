package com.turtletest.turtle;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/** ManualActivity is a fragment of BluetoothActivity
 *  Allows the user to control the robot using the phone as a
 *  joystick.
 */

public class ManualActivity extends Fragment{

    private ConnectedThread mConnectedThread;

    //Fragment's GUI Components
    private CardView manualBTbtn;
    private CardView obstacleBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.manualactivity, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        //Connect GUI Components
        manualBTbtn = view.findViewById(R.id.manualBTbtn);
        obstacleBtn = view.findViewById(R.id.obstacleBtn);
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        //Collect bluetooth thread from parent activity
        BluetoothActivity act = (BluetoothActivity) getActivity();
        mConnectedThread = act.getThread();

        //Set Button on click actions
        manualBTbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //If bluetooth is connected, send data to Arduino
                if (mConnectedThread!= null) {
                    mConnectedThread.write("F");
                    //Display sent data for debugging
                    Toast.makeText(getContext(), "F", Toast.LENGTH_SHORT).show();

                }
            }
        });
        obstacleBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //If bluetooth is connected, send data to Arduino
                if (mConnectedThread!= null) {
                    mConnectedThread.write("O");
                    //Display sent data for debugging
                    Toast.makeText(getContext(),"O", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
