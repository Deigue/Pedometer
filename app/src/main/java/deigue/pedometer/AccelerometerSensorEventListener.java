package deigue.pedometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Deigue on 12/31/2014.
 */
public class AccelerometerSensorEventListener implements SensorEventListener {

    LineGraphView graphOutput;
    float[] accelerationData = new float[3];
    float[] rotationVectors = new float[3];
    float[] rotationMatrix = new float[9];
    float[] orientationVectors = new float[3];

    TextView orientationOutput;
    TextView accelerationOutput;



    boolean s1,s2,s3,s4 = false;

    public AccelerometerSensorEventListener(LineGraphView graph, TextView orientationVectors, TextView acceleration){

        graphOutput = graph;
        orientationOutput = orientationVectors;
        accelerationOutput = acceleration;
    }



    @Override
    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {
            case Sensor.TYPE_LINEAR_ACCELERATION:
                accelerationData = event.values.clone();
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                rotationVectors = event.values.clone();
                break;
        }

        //Calculate Orientation Vectors:
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVectors);
        SensorManager.getOrientation(rotationMatrix, orientationVectors);
        orientationOutput.setText("Azimuth(X): " + (String.format("%.3f", orientationVectors[0]))
                + "           Pitch(Y): " + (String.format("%.3f", orientationVectors[1]))
                + "           Roll(Z): " + (String.format("%.3f", orientationVectors[2])));




        float[] smoothedAcceleration = new float[] {0f,0f,0f};
        float alpha = 3.0f;
        for(int i=0; i<3; i++)
            smoothedAcceleration[i] += (accelerationData[i] - smoothedAcceleration[i])/alpha;

        accelerationOutput.setText(smoothedAcceleration[0] + " " + smoothedAcceleration[1] + " " + smoothedAcceleration[2]);
        graphOutput.addPoint(smoothedAcceleration);


        /*
        float smoothaccel = 0.0f;  //Initialized float to store low filtered Z-axis accelerometer readings.

        //Low Pass Filter Z-Axis Steps:
        float c = 14f;
        float acceleration = MainActivity.this.accelerationData[2];
        smoothaccel += (acceleration - smoothaccel) / c;

        //Reset Button
        Button Resets = (Button) findViewById(R.id.reset);
        Resets.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                step = 0;
                ns = 0;
                ew = 0;
                steps.setText(" ---Steps--- \n Steps: " + step);
                n.setText("\n--- Displacement--- \n North: " + ns);
                e.setText("East: " + ew);
                graph.purge();
            }

        });

        //Finite state function to count steps:
        if (smoothaccel < (-0.02)) {
            s1 = true;
        }

        if ((s1) && (smoothaccel > 0.012) && (smoothaccel < 0.19)) {
            s2 = true;
        }

        if ((s1) && (s2) && (smoothaccel < (-0.1))) {
            s3 = true;
        }

        if ((s1) && (s2) && (s3) && ((smoothaccel > (-0.1)) && (smoothaccel < (-0.02)))) {
            s4 = true;
        }

        if ((s1) && (s2) && (s3) && (s4)) {
            s1 = false;
            s2 = false;
            s3 = false;
            s4 = false;
            step++;  //Step increments


            //Calculate Steps Walked North:
            ns = ns + (double) Math.cos(orientationData[0]);

            //Calculate Steps Walked East:
            ew = ew + (double) Math.sin(orientationData[0]);
        }

        //Displays number of Steps:
        smoothGraph[0] = smoothaccel;
        graphOutput.addPoint(smoothGraph);
        steps.setText(" ---Steps--- \n Steps: " + step);

        //Displays Displacement due North,East:
        n.setText("\n--- Displacement--- \n North: " + ns);
        e.setText("East: " + ew);
    }
    */
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}