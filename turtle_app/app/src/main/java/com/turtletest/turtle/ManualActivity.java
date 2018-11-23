package com.turtletest.turtle;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/** ManualActivity is a fragment of BluetoothActivity
 *  Allows the user to control the robot's different modes: obstacle
 *  and line following
 */

public class ManualActivity extends Fragment{

    private ConnectedThread mConnectedThread; //thread from Bluetooth

    //Fragment's GUI Components
    private CardView manualBTbtn;
    private CardView obstacleBtn;
    private Button stopBtn;

    /* Set relevant layout
     * @param LayoutInflater inflater
     * @param ViewGroup container
     * @param Bundle savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.manualactivity, container, false);
    }
    /* Finds GUI components
      * @param View view
      * @param Bundle savedInstanceState
      */
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //Connect GUI Components
        manualBTbtn = view.findViewById(R.id.manualBTbtn);
        obstacleBtn = view.findViewById(R.id.obstacleBtn);
        stopBtn = view.findViewById(R.id.stopBtn);
    }

    /* Sets up onclick listeners, collects bluetooth thread
    * @param Bundle savedInstanceState
    */
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
                    mConnectedThread.write("FF");
                }
                //Display sent data for debugging
                Toast.makeText(getContext(), "FF", Toast.LENGTH_SHORT).show();

            }
        });
        obstacleBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //If bluetooth is connected, send data to Arduino
                if (mConnectedThread!= null) {
                    mConnectedThread.write("OO");
                    //Display sent data for debugging
                }
                Toast.makeText(getContext(),"OO", Toast.LENGTH_SHORT).show();

            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //If bluetooth is connected, send data to Arduino
                if (mConnectedThread!= null) {
                    mConnectedThread.write("SS");
                    //Display sent data for debugging
                }
                Toast.makeText(getContext(), "SS", Toast.LENGTH_SHORT).show();

            }
        });
    }

}
