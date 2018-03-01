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
    TextView xorientation;
    TextView yorientation;
    TextView zorientation;

    //Robot speed and radius variables
    int maxRadius = 300;
    int maxSpeed = 255;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.gryo, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        //Set GUI Components
        startstopBtn = view.findViewById(R.id.startstopbtn);
        xaxis = view.findViewById(R.id.xaxis);
        yaxis = view.findViewById(R.id.yaxis);
        zaxis = view.findViewById(R.id.zaxis);
        xorientation = view.findViewById(R.id.xorientation);
        yorientation = view.findViewById(R.id.yorientation);
        zorientation = view.findViewById(R.id.zorientation);
        innerCircle = view.findViewById(R.id.innercircle);
        outerCircle = view.findViewById(R.id.outercircle);
        innerCircle.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        //Set up sensor variables
        SensorManager sensorManager =
                (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor gyroscopeSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Sensor rotationVectorSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        //Get bluetooth thread from Activity
        BluetoothActivity act = (BluetoothActivity) getActivity();
        thread = act.getThread();

        //Find outer circle coordinates relative to screen size
        outerCircle.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener(){
            public void onGlobalLayout() {
                Rect rectf = new Rect();
                Rect recti = new Rect();

                outerCircle.getLocalVisibleRect(rectf);
                innerCircle.getLocalVisibleRect(recti);

                location[0] = rectf.centerX() + (recti.width()/2);
                location[1] = rectf.centerY() - (recti.height()/2);

                radius = (int)(rectf.height()/2.5);

                innerCircle.setX(location[0]);  //sets innerCircle to center of outer Circle
                innerCircle.setY(location[1]);
                //Remove the listener to prevent being called again by future layout events:
                outerCircle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        // Create a listener
        SensorEventListener gyroscopeSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                xaxis.setText(Float.toString(sensorEvent.values[0]));   //Display sensor data on screen
                yaxis.setText(Float.toString(sensorEvent.values[1]));
                zaxis.setText(Float.toString(sensorEvent.values[2]));
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        // Create a listener
        SensorEventListener rvListener = new SensorEventListener() {
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
                for(int i = 0; i < 3; i++) {
                    orientations[i] = (float)(Math.toDegrees(orientations[i]));
                }

                xorientation.setText(Float.toString(orientations[0]));
                yorientation.setText(Float.toString(orientations[1]));
                zorientation.setText(Float.toString(orientations[2]));

                vtilt = location[1] - Math.round(Math.max(-90,Math.min(90,orientations[1]/90)) * (radius - 5));
                htilt = location[0] - 5 + Math.round(Math.max(-90,Math.min(90,orientations[2]/90)) * (radius - 5));

                if (Math.sqrt(Math.pow((htilt - location[0]),2) + Math.pow((vtilt-location[1]),2)) <= radius) {
                    innerCircle.setX(htilt);
                    innerCircle.setY(vtilt);
                }

                float x = (maxSpeed*(orientations[1]))/90;
                float z = (maxRadius*(orientations[2]))/90;

                // If button was pressed to start, commence sending sensor data to Arduino, otherwise ignore
                if (startGyro) {
                    if (thread != null) {//First check to make sure thread created
                        thread.write("M " + String.valueOf((int) x)+ " " +
                                String.valueOf((int) z) + "M");
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        //Start sending sensor data to phone on button click
        startstopBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startGyro = !startGyro;
                Toast.makeText(getContext(), String.valueOf(startGyro), Toast.LENGTH_SHORT).show();
            }
        });

// Register it
        sensorManager.registerListener(rvListener,
                rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);

// Register the listener
        sensorManager.registerListener(gyroscopeSensorListener,
                gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
