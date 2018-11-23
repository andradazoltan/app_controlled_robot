package com.turtletest.turtle;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.lang.Math;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Toast;

/** Gyro is a child fragment of BluetoothActivity
 *  Allows the user to use the phone as a joystick to control the robot
 */
public class Gryo extends Fragment{
    //GUI Textviews
    TextView xaxis;
    TextView yaxis;
    TextView zaxis;
    TextView extra;

    TextView xorientation;
    TextView yorientation;
    TextView zorientation;

    //Fragment GUI Components
    private Button startstopBtn;
    ImageView innerCircle;
    ImageView outerCircle;

    private float[] orientations = new float[3]; //rotation in x, y, z plane
    private boolean startGyro = false;           //boolean that tracks when button is pressed
    int[] location = new int[2];                 //center of outer circle
    int radius;                                  //radius of outer circle
    int vtilt;                                   //y coordinate of inner circle
    int htilt;                                   //x coordinate of inner circle

    private ConnectedThread thread;              //BluetoothActivity thread

    /* Opens gryo layout.
     * @param LayoutInflater inflater
     * @param ViewGroup container
     * @param Bundle savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.gryo, container, false);
    }

    /* Sets GUI Components once layout loaded.
     * @param View view
     * @param Bundle savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        //Set GUI Components
        startstopBtn = view.findViewById(R.id.startstopbtn);
        xaxis = view.findViewById(R.id.xaxis);
        yaxis = view.findViewById(R.id.yaxis);
        zaxis = view.findViewById(R.id.zaxis);
        extra = view.findViewById(R.id.extra);

        xorientation = view.findViewById(R.id.xorientation);
        yorientation = view.findViewById(R.id.yorientation);
        zorientation = view.findViewById(R.id.zorientation);
        innerCircle = view.findViewById(R.id.innercircle);
        outerCircle = view.findViewById(R.id.outercircle);
        innerCircle.setVisibility(View.VISIBLE);
    }

    /* Sets up sensor listeners, bluetooth connection,
     * and allows user to send data to Arduino.
     * @param Bundle savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Set up sensor variables
        SensorManager sensorManager =
                (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor rotationVectorSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        //Get bluetooth thread from Activity
        BluetoothActivity act = (BluetoothActivity) getActivity();
        thread = act.getThread();

        //Find outer circle coordinates relative to screen size
        outerCircle.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                Rect rectf = new Rect();  //Used to find initial position of outerCircle for diagram
                Rect recti = new Rect();

                outerCircle.getLocalVisibleRect(rectf);
                innerCircle.getLocalVisibleRect(recti);

                //Locate the center of the outerCircle using rectangles
                location[0] = rectf.centerX() + (recti.width() / 2);
                location[1] = rectf.centerY() - (recti.height() / 2);

                radius = (int) (rectf.height() / 2.5);  //radius of outerCircle

                innerCircle.setX(location[0]);  //sets innerCircle to center of outer Circle
                innerCircle.setY(location[1]);
                //Remove the listener to prevent being called again by future layout events:
                outerCircle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        // Create a listener
        SensorEventListener rvListener = new SensorEventListener() {
            /* Collects new phone position when sensor registers a change
             * @param SensorEvent sensorEvent
             */
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float[] rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(
                        rotationMatrix, sensorEvent.values);

                // Remap coordinate system
                float[] remappedRotationMatrix = new float[16];
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Z,
                        remappedRotationMatrix);
                // Convert to orientations
                SensorManager.getOrientation(remappedRotationMatrix, orientations);

                //Convert sensor data to degrees
                for (int i = 0; i < 3; i++) {
                    orientations[i] = (float) (Math.toDegrees(orientations[i]));
                }

                //Display position of phone for user
                xorientation.setText(Float.toString(orientations[0]));
                yorientation.setText(Float.toString(orientations[1]));
                zorientation.setText(Float.toString(orientations[2]));

                //Parse sensor data into usable form for transmission to Arduino
                vtilt = location[1] - Math.round(Math.max(-90, Math.min(90, orientations[1] / 90)) * (radius - 5));
                htilt = location[0] - 5 + Math.round(Math.max(-90, Math.min(90, orientations[2] / 90)) * (radius - 5));

                //Set innerCircle to display position of phone
                if (Math.sqrt(Math.pow((htilt - location[0]), 2) + Math.pow((vtilt - location[1]), 2)) <= radius) {
                    innerCircle.setX(htilt);
                    innerCircle.setY(vtilt);
                }

                //Map sensor data to a range of [-90, 90]
                float x = (orientations[1] > 90 || orientations[1] < -90) ? 90 : orientations[1];
                float z = (orientations[2] > 90 || orientations[2] < -90) ? 90 : orientations[2];

                //Determines if phone is rotated forwards/backwards
                int dirx = (orientations[1] >= 0) ? 1 : 0;
                //Determines if phone is rotated left/right
                int dirz = (orientations[2] >= 0) ? 1 : 0;

                // Display sent data on UI
                xaxis.setText("*");   //Display no data on screen if thread is null
                yaxis.setText("*");
                zaxis.setText("*");
                extra.setText("*");

                // If button was pressed to start, commence sending sensor data to Arduino, otherwise ignore
                if (startGyro) {
                    if (thread != null) {//First check to make sure thread created
                        // Send relevant data to Arduino
                        thread.write("M " + ((Math.abs(x) < 10) ?
                                ("0" + String.valueOf((int) Math.abs(x))) :
                                String.valueOf((int) Math.abs(x))) + " " +
                                String.valueOf(dirx) + " " + String.valueOf(dirz) + " "
                                + ((Math.abs(z) < 10) ?
                                ("0" + String.valueOf((int) Math.abs(z))) :
                                String.valueOf((int) Math.abs(z)))
                                + "M");

                        //Display sent data to user
                        xaxis.setText(((Math.abs(x) < 10) ?
                                ("0" + String.valueOf((int) Math.abs(x))) :
                                String.valueOf((int) Math.abs(x))));   //Display sensor data on screen
                        yaxis.setText(String.valueOf(dirx));
                        zaxis.setText(((Math.abs(z) < 10) ?
                                ("0" + String.valueOf((int) Math.abs(z))) :
                                String.valueOf((int) Math.abs(z))));
                        extra.setText(String.valueOf(dirz));
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        //Start sending sensor data to phone on button click
        startstopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGyro = !startGyro;      //Switch states at user input
                if (thread != null) {
                    thread.write("SS"); //Notify Arduino of changed State
                }
                Toast.makeText(getContext(), String.valueOf(startGyro), Toast.LENGTH_SHORT).show();
            }
        });

// Register rotationVectorSensor
        sensorManager.registerListener(rvListener,
                rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
