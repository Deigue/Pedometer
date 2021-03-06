package deigue.pedometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
    int steps= 0;
    float[] smoothedAcceleration = new float[]{0f, 0f, 0f};

    float[] accelerationMax = new float[]{0f, 0f, 0f};
    float[] accelerationMin = new  float[]{0f, 0f, 0f};

    TextView orientationOutput;
    TextView accelerationOutput;
    TextView accelerationMaxOutput;
    TextView accelerationMinOutput;
    TextView states;
    TextView stepsDone;


    boolean state1, state2, state3, state4 = false;

    public AccelerometerSensorEventListener(LineGraphView graph, TextView orientationVectors, TextView acceleration,
                                            TextView accelerationMax1, float[] accelerationMaximums, TextView accelerationMin1, float[] accelerationMinimums, TextView stepsTaken, TextView currstate) {

        graphOutput = graph;
        orientationOutput = orientationVectors;
        accelerationOutput = acceleration;
        accelerationMaxOutput = accelerationMax1;
        accelerationMax = accelerationMaximums;
        accelerationMinOutput = accelerationMin1;
        accelerationMin = accelerationMinimums;
        stepsDone = stepsTaken;
        states = currstate;
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



        float alpha = 11.0f;
        for (int i = 0; i < 3; i++)
            smoothedAcceleration[i] += (accelerationData[i] - smoothedAcceleration[i]) / alpha;

        accelerationOutput.setText(String.format("X: %.3f", smoothedAcceleration[0]) + String.format("  Y: %.3f", smoothedAcceleration[1]) + String.format("  Z: %.3f", smoothedAcceleration[2]));


        if (smoothedAcceleration[0] > accelerationMax[0])
            accelerationMax[0] = smoothedAcceleration[0];
        if (smoothedAcceleration[1] > accelerationMax[1])
            accelerationMax[1] = smoothedAcceleration[1];
        if (smoothedAcceleration[2] > accelerationMax[2])
            accelerationMax[2] = smoothedAcceleration[2];

        if (smoothedAcceleration[0] < accelerationMin[0])
            accelerationMin[0] = smoothedAcceleration[0];
        if (smoothedAcceleration[1] < accelerationMin[1])
            accelerationMin[1] = smoothedAcceleration[1];
        if (smoothedAcceleration[2] < accelerationMin[2])
            accelerationMin[2] = smoothedAcceleration[2];

        accelerationMaxOutput.setText(String.format("X: %.3f", accelerationMax[0]) + String.format("  Y: %.3f", accelerationMax[1]) + String.format("  Z: %.3f", accelerationMax[2]));
        accelerationMinOutput.setText(String.format("X: %.3f", accelerationMin[0]) + String.format("  Y: %.3f", accelerationMin[1]) + String.format("  Z: %.3f", accelerationMin[2]));

        graphOutput.addPoint(smoothedAcceleration);

        //Finite State Function:

        float azimuth = orientationVectors[0];
        float pitch = orientationVectors[1];
        float roll = orientationVectors[2];

        //Screen is approximately facing upwards.
        if((pitch<0.75)&&(roll<0.75)){

            if((-0.4<=smoothedAcceleration[0])&&(smoothedAcceleration[0]<0.45)&&
                    (-0.6<=smoothedAcceleration[1])&&(smoothedAcceleration[1]< 0.6)&&
                    (-1.6<=smoothedAcceleration[2])&&(smoothedAcceleration[2]<0))
                state1=true;
            if((0.45<=smoothedAcceleration[0])&&(smoothedAcceleration[0]<=1.2)&&
                    (0.6<=smoothedAcceleration[1])&&(smoothedAcceleration[1]<= 1.2)&&
                    (0.3<=smoothedAcceleration[2])&&(smoothedAcceleration[2]<=2.3)&&
                    state1)
                state2=true;
            if((-1.5<=smoothedAcceleration[0])&&(smoothedAcceleration[0]< -0.4)&&
                    (-2<=smoothedAcceleration[1])&&(smoothedAcceleration[1]< -0.6)&&
                    (-2.7<=smoothedAcceleration[2])&&(smoothedAcceleration[2]<-1.6)&&
                    state2)
                state3=true;
            if((-0.4<=smoothedAcceleration[0])&&(smoothedAcceleration[0]<0.45)&&
                    (-0.6<=smoothedAcceleration[1])&&(smoothedAcceleration[1]< 0.6)&&
                    (-1.6<=smoothedAcceleration[2])&&(smoothedAcceleration[2]<0)&&
                    state3)
                state4=true;

            if(state4){
                steps++;
                state1=state2=state3=state4= false;
            }

        }

    states.setText(state1+ " " + state2 + " " + state3 + " " + state4);
    stepsDone.setText("Steps taken: "+ steps);


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

