package com.turtletest.turtle;

import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.graphics.ColorUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.madrapps.pikolo.HSLColorPicker;
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener;

import java.util.Random;

public class LightsActivity extends Fragment {

    private ConnectedThread thread;

    private HSLColorPicker colorPicker;
    private ImageView imageView;
    private ImageButton imageButton1;
    private ImageButton imageButton2;
    private ImageButton imageButton3;
    private ImageButton imageButton4;
    private ImageButton imageButton5;

    private int a, r, g, b;

    int colour;

    private Button randomColorButton;

    private Random random = new Random();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.newlightactivity, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        colorPicker = (HSLColorPicker) view.findViewById(R.id.colorPicker);
        imageView = view.findViewById(R.id.imageView);
        randomColorButton = view.findViewById(R.id.randomColorButton);
        imageButton1 = view.findViewById(R.id.imageButton1);
        imageButton2 = view.findViewById(R.id.imageButton2);
        imageButton3 = view.findViewById(R.id.imageButton3);
        imageButton4 = view.findViewById(R.id.imageButton4);
        imageButton5 = view.findViewById(R.id.imageButton5);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        BluetoothActivity act = (BluetoothActivity) getActivity();
        thread = act.getThread();

        colorPicker.setColorSelectionListener(new SimpleColorSelectionListener() {
            @Override
            public void onColorSelected(int color) {
                // Do whatever you want with the color
                colour = color;
                imageView.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            }
        });

        imageButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Click(v);
            }
        });

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

        randomColorButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               if (thread!=null){

                   a = (colour >> 24) & 0xff; // or color >>> 24
                   r = (colour >> 16) & 0xff;
                   g = (colour >>  8) & 0xff;
                   b = (colour      ) & 0xff;
                   thread.write("L " + Integer.toString(a) + " " +
                           Integer.toString(r) + " " + Integer.toString(g) + " "
                           + Integer.toString(b) + "L");
                   Toast.makeText(getContext(), "L: " + Integer.toString(a) + " " +
                           Integer.toString(r) + " " + Integer.toString(g) + " "
                           + Integer.toString(b) + "L", Toast.LENGTH_SHORT).show();
               }
            }
        });
    }

    public void Click(View v){
        if(v.getId() == R.id.randomColorButton){
            final int color = ColorUtils.HSLToColor(new float[]{random.nextInt(360), random.nextFloat(), random.nextFloat()});
            final String hexColor = String.format("#%06X", (0xFFFFFF & color));
            randomColorButton.setText(hexColor);
            randomColorButton.setBackgroundColor(color);
            imageView.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            colorPicker.setColor(color);
        }
        if(v instanceof ImageButton){
            final int color = ((ColorDrawable) ((ImageButton) v).getDrawable()).getColor();
            imageView.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            colorPicker.setColor(color);
        }
    }


}


