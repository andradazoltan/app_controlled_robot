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
import android.widget.ImageView;
import android.widget.TextView;
import java.lang.Math;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Toast;

public class Gryo extends Fragment{
    TextView xaxis;
    TextView yaxis;
    TextView zaxis;
    TextView xorientation;
    TextView yorientation;
    TextView zorientation;

    ImageView innerCircle;
    ImageView outerCircle;

    int[] location = new int[2];
    int radius;

    int diameter;

    int vtilt;
    int htilt;

    int maxSpeed = 0;

    private ConnectedThread thread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.gryo, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        xaxis = (TextView) view.findViewById(R.id.xaxis);
        yaxis = (TextView) view.findViewById(R.id.yaxis);
        zaxis = (TextView) view.findViewById(R.id.zaxis);

        xorientation = (TextView) view.findViewById(R.id.xorientation);
        yorientation = (TextView) view.findViewById(R.id.yorientation);
        zorientation = (TextView) view.findViewById(R.id.zorientation);

        innerCircle = (ImageView) view.findViewById(R.id.innercircle);
        outerCircle = (ImageView) view.findViewById(R.id.outercircle);
        innerCircle.setVisibility(View.VISIBLE);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        SensorManager sensorManager =
                (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor gyroscopeSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Sensor rotationVectorSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);



        BluetoothActivity act = (BluetoothActivity) getActivity();
        thread = act.getThread();

        outerCircle.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener(){
            public void onGlobalLayout() {
                Rect rectf = new Rect();
                Rect recti = new Rect();

                int height = outerCircle.getHeight();
                int width = outerCircle.getWidth();
                diameter = outerCircle.getHeight();

                outerCircle.getLocalVisibleRect(rectf);
                innerCircle.getLocalVisibleRect(recti);

                location[0] = rectf.centerX() + (recti.width()/2);
                location[1] = rectf.centerY() - (recti.height()/2);

                radius = (int)(rectf.height()/2.5);


                //don't forget to remove the listener to prevent being called again by future layout events:
                outerCircle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        // Create a listener
        SensorEventListener gyroscopeSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                xaxis.setText(Float.toString(sensorEvent.values[0]));
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
                float[] orientations = new float[3];
                SensorManager.getOrientation(remappedRotationMatrix, orientations);


                for(int i = 0; i < 3; i++) {
                    orientations[i] = (float)(Math.toDegrees(orientations[i]));
                }

                xorientation.setText(Float.toString(orientations[0]));
                yorientation.setText(Float.toString(orientations[1]));
                zorientation.setText(Float.toString(orientations[2]));

                //send to bluetooth
                if (thread != null) {//First check to make sure thread created
                    thread.write("x: " + orientations[0]);
                    thread.write("y: " + orientations[1]);
                    thread.write("z: " + orientations[2]);
                    //Toast.makeText(getContext(), Integer.toString(i), Toast.LENGTH_SHORT).show();
                }
                vtilt = location[1] - Math.round(Math.max(-90,Math.min(90,orientations[1]/90)) * (radius - 5));
                htilt = location[0] - 5 + Math.round(Math.max(-90,Math.min(90,orientations[2]/90)) * (radius - 5));

                if (Math.sqrt(Math.pow((htilt - location[0]),2) + Math.pow((vtilt-location[1]),2)) <= radius) {
                    innerCircle.setX(htilt);
                    innerCircle.setY(vtilt);
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };



// Register it
        sensorManager.registerListener(rvListener,
                rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);

// Register the listener
        sensorManager.registerListener(gyroscopeSensorListener,
                gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }



}
