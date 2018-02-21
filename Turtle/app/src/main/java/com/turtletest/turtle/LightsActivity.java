package com.turtletest.turtle;

import android.support.v4.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;


/**
 * Created by Grace on 2018-02-19.
 */

public class LightsActivity extends Fragment {

    private SeekBar bar;
    private ConnectedThread thread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lightsactivity, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        bar = view.findViewById(R.id.seekbar);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Drawable draw = getResources().getDrawable(R.drawable.progressbar);
        bar.setProgressDrawable(draw);

        BluetoothActivity act = (BluetoothActivity) getActivity();
        thread = act.getThread();

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
                Toast.makeText(getContext(), "Changing led display", Toast.LENGTH_SHORT).show();

                if (thread != null) //First check to make sure thread created
                    thread.write(Integer.toString(i));
                Toast.makeText(getContext(), Integer.toString(i), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getContext(), "Started tracking", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getContext(), "Stopped Tracking", Toast.LENGTH_SHORT).show();
            }
        });



    }

}


