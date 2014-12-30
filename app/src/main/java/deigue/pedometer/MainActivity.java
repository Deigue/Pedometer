package deigue.pedometer;

import java.util.Arrays;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import deigue.pedometer.R;

//Main Activity Class:
public class MainActivity extends Activity {

    //Graph, SensorManager
    LineGraphView graph;
    SensorManager sm;
    mSensorEventListener msel;

    //Initialization Arrays,Variables:
    float[] accel = new float[3];
    float[] rotation = new float[3];
    float[] smoothgraph = new float[1];


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Linear Layout:
        LinearLayout l = (LinearLayout) findViewById(R.id.layout);
        l.setOrientation(LinearLayout.VERTICAL);
        graph = new LineGraphView(getApplicationContext(),
                100,
                Arrays.asList("x", "y", "z"));

        //Enables the Graph to be visible
        graph.setVisibility(View.VISIBLE);

        //Graph Title:
        TextView graphtitle = new TextView(getApplicationContext());
        graphtitle.setText("\nAcceleration Graph:");
        l.addView(graphtitle);

        //TextView Initialize: Steps, North, East
        TextView steps = new TextView(getApplicationContext());
        TextView north = new TextView(getApplicationContext());
        TextView east = new TextView(getApplicationContext());

        //Sensor Manager and Listener initialise:  
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        msel = new mSensorEventListener(steps, north, east, graph);

        //Listeners Registered:
        sm.registerListener(msel, sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_FASTEST);
        sm.registerListener(msel, sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_FASTEST);


        //Initialization of TextViews:

        l.addView(graph); //Displays graph
        l.addView(steps); //Displays number of steps taken
        l.addView(north); //Displays displacement in North direction
        l.addView(east); //Displays displacement in East direction

    }  // End of OnCreate() Method


    //Sensor Listener Class Definition:
    class mSensorEventListener implements SensorEventListener {

        TextView steps, n, e;
        LineGraphView G;
        int step = 0;
        double ns = 0.0f; //north-south
        double ew = 0.0f; //east-west
        boolean s1, s2, s3, s4 = false;

        float[] ROTATION = new float[9]; // Rotation Matrix
        float[] ORIENT = new float[3]; //Orientation Matrix

        public mSensorEventListener(TextView steps, TextView n, TextView e, LineGraphView graph) {

            this.steps = steps;
            this.n = n;
            this.e = e;
            G = graph;

        }

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        @Override
        public void onSensorChanged(final SensorEvent event) {


            switch (event.sensor.getType()) {
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    accel = event.values.clone();
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    rotation = event.values.clone();
                    break;
            }


            float smoothaccel = 0.0f;  //Initialized float to store low filtered Z-axis accelerometer readings.


            //Get Orientation:
            SensorManager.getRotationMatrixFromVector(ROTATION, rotation);
            SensorManager.getOrientation(ROTATION, ORIENT);

            //Low Pass Filter Z-Axis Steps:
            float c = 14f;
            float acceleration = accel[2];
            smoothaccel += (acceleration - smoothaccel) / c;

            //Reset Button Definition:
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

            if ((s1 == true) && (smoothaccel > 0.012) && (smoothaccel < 0.19)) {
                s2 = true;
            }

            if ((s1 == true) && (s2 == true) && (smoothaccel < (-0.1))) {
                s3 = true;
            }

            if ((s1 == true) && (s2 == true) && (s3 == true) && ((smoothaccel > (-0.1)) && (smoothaccel < (-0.02)))) {
                s4 = true;
            }

            if ((s1 == true) && (s2 == true) && (s3 == true) && (s4 == true)) {
                s1 = false;
                s2 = false;
                s3 = false;
                s4 = false;
                step++;  //Step increments


                //Calculate Steps Walked North:
                ns = ns + (double) Math.cos(ORIENT[0]);

                //Calculate Steps Walked East:
                ew = ew + (double) Math.sin(ORIENT[0]);
            }

            //Displays number of Steps:
            smoothgraph[0] = smoothaccel;
            G.addPoint(smoothgraph);
            steps.setText(" ---Steps--- \n Steps: " + step);

            //Displays Displacement due North,East:
            n.setText("\n--- Displacement--- \n North: " + ns);
            e.setText("East: " + ew);
        }
    }
}