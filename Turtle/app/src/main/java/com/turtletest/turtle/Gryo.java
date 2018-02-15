package com.turtletest.turtle;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import java.lang.Math;

/**
 * Created by Grace on 2018-02-10.
 */

public class Gryo extends AppCompatActivity{
    TextView xaxis;
    TextView yaxis;
    TextView zaxis;
    TextView xorientation;
    TextView yorientation;
    TextView zorientation;

    ImageView innerCircle;
    ImageView outerCircle;

    int[] location = new int[2];
    int[] test = new int[2];
    int radius = 200/3;

    int vtilt;
    int htilt;

    int maxSpeed = 0;

    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            location[0] = 210;
            location[1] = 1482;
            outerCircle.getLocationOnScreen(test);
            System.out.println(test[0]);
            System.out.println(test[1]);

            System.out.println(location[0]);
            System.out.println(location[1]);
            System.out.println(radius);
            innerCircle.setX(location[0]);
            innerCircle.setY(location[1]);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SensorManager sensorManager =
                (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor gyroscopeSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Sensor rotationVectorSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.gryo);

        xaxis = (TextView) findViewById(R.id.xaxis);
        yaxis = (TextView) findViewById(R.id.yaxis);
        zaxis = (TextView) findViewById(R.id.zaxis);

        xorientation = (TextView) findViewById(R.id.xorientation);
        yorientation = (TextView) findViewById(R.id.yorientation);
        zorientation = (TextView) findViewById(R.id.zorientation);

        innerCircle = (ImageView) findViewById(R.id.innercircle);
        outerCircle = (ImageView) findViewById(R.id.outercircle);




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

                vtilt = location[1] + Math.round(orientations[1]/90 * (radius));
                htilt = location[0] + Math.round(orientations[2]/90) * (radius);

                innerCircle.setX(htilt * 1.5f);
                innerCircle.setY(vtilt * 1.5f);

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
    };


}
