package com.turtletest.turtle;

import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.madrapps.pikolo.HSLColorPicker;
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener;

/*
 * LightsActivity is a fragment of BluetoothActivity; allows the user to
 * set the saturation, hue, and brightness of their desired colour and send the
 * data as alpha, and rgb values through the connected bluetooth thread.
 */

public class LightsActivity extends Fragment {

    //Bluetooth thread from BluetoothActivity
    private ConnectedThread thread;

    //Colour slider GUI Components
    private HSLColorPicker colorPicker;
    private ImageView imageView;

    //Preset Colour Buttons
    private ImageButton imageButton1;
    private ImageButton imageButton2;
    private ImageButton imageButton3;
    private ImageButton imageButton4;
    private ImageButton imageButton5;

    private int a, r, g, b;

    int colour; //hex colour value
    private Button sendBtn;  //send data button

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.newlightactivity, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //Connect GUI Components
        colorPicker = view.findViewById(R.id.colorPicker);
        imageView = view.findViewById(R.id.imageView);
        sendBtn = view.findViewById(R.id.randomColorButton);
        imageButton1 = view.findViewById(R.id.imageButton1);
        imageButton2 = view.findViewById(R.id.imageButton2);
        imageButton3 = view.findViewById(R.id.imageButton3);
        imageButton4 = view.findViewById(R.id.imageButton4);
        imageButton5 = view.findViewById(R.id.imageButton5);
    }

    /* Sets up GUI components to response to user input when fragment is created.
     * @param Bundle savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Obtain thread from parent activity
        BluetoothActivity act = (BluetoothActivity) getActivity();
        thread = act.getThread();

        colorPicker.setColorSelectionListener(new SimpleColorSelectionListener() {
            @Override
            public void onColorSelected(int color) {
                colour = color;
                imageView.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            }
        });

        imageButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Click(v);
            }
        });  //Set onClick events
        imageButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Click(v);
            }
        });
        imageButton3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Click(v);
            }
        });
        imageButton4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Click(v);
            }
        });
        imageButton5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Click(v);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Only compute a, r, g, b values if bluetooth connected
               if (thread!=null){
                   // Compute a, r, g, b values from hex value
                   a = (50* (colour >> 24) & 0xff)/255; // or color >>> 24
                   r = (50* (colour >> 16) & 0xff)/255;
                   g = (50* (colour >>  8) & 0xff)/255;
                   b = (50* (colour      ) & 0xff)/255;

                   thread.write("L " + ((r < 10)? "0" + Integer.toString(r): Integer.toString(r)) +
                           " " + ((g < 10)? "0" + Integer.toString(g): Integer.toString(g)) + " "
                           + ((b < 10)? "0" + Integer.toString(b): Integer.toString(b) + "L"));

                   //Display data sent to Arduino on app for debugging
                   Toast.makeText(getContext(), "L: " + Integer.toString(a) + " " +
                           Integer.toString(r) + " " + Integer.toString(g) + " "
                           + Integer.toString(b) + "L", Toast.LENGTH_SHORT).show();
               }
            }
        });
    }

    /* Changes colour slider image to display preset colour
     * @param View v   current view of fragment
     */
    public void Click(View v){
        if(v instanceof ImageButton){
            final int color = ((ColorDrawable) ((ImageButton) v).getDrawable()).getColor();
            imageView.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            colorPicker.setColor(color);
        }
    }
}


