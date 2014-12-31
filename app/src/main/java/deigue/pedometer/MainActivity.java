package deigue.pedometer;

import java.util.Arrays;

import android.app.Activity;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;


public class MainActivity extends Activity {

    //Initializations:
    LinearLayout layout;
    LineGraphView graph;
    SensorManager sensorManager;
    Sensor lightSensor, rotationSensor, accelerometerSensor, magneticSensor;
    LightSensorEventListener lightListener;
    RotationSensorEventListener rotationListener;
    MagneticSensorEventListener magneticListener;




    float[] accelerationData = new float[3];
    float[] rotationData = new float[3];


    float[] smoothGraph = new float[1];


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Layout
        layout = (LinearLayout) findViewById(R.id.layout);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 0);

        //Sensor Initializations:
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        //Light Intensity Data:
        TextView lightView = new TextView(getApplicationContext());
        lightListener = new LightSensorEventListener(lightView);
        sensorManager.registerListener(lightListener, lightSensor, sensorManager.SENSOR_DELAY_NORMAL);
        lightView.setTypeface(Typeface.DEFAULT_BOLD);
        layout.addView(lightView);

        //Rotation Vector Data:
        TextView rotationVectors = new TextView(getApplicationContext());
        TextView rotationView = new TextView(getApplicationContext());
        rotationVectors.setLayoutParams(layoutParams);
        rotationVectors.setText("Rotation Vectors: ");
        rotationListener = new RotationSensorEventListener(rotationView);
        sensorManager.registerListener(rotationListener, rotationSensor, sensorManager.SENSOR_DELAY_FASTEST);
        rotationVectors.setTypeface(Typeface.DEFAULT_BOLD);
        layout.addView(rotationVectors);
        layout.addView(rotationView);

        //Magnetic Field Strength Data:
        TextView magneticFieldStrength = new TextView(getApplicationContext());
        TextView magneticView = new TextView(getApplicationContext());
        magneticFieldStrength.setLayoutParams(layoutParams);
        magneticFieldStrength.setText("Magnetic Field Strength: ");
        magneticListener = new MagneticSensorEventListener(magneticView);
        sensorManager.registerListener(magneticListener, magneticSensor, sensorManager.SENSOR_DELAY_FASTEST);
        magneticFieldStrength.setTypeface(Typeface.DEFAULT_BOLD);
        layout.addView(magneticFieldStrength);
        layout.addView(magneticView);

        graph = new LineGraphView(getApplicationContext(), 100, Arrays.asList("X", "Y", "Z"));


        /*
        //Graph Title:
        TextView graphtitle = new TextView(getApplicationContext());
        graphtitle.setText("\nAcceleration Graph:");
        layout.addView(graphtitle);
        */
        graph.setVisibility(View.VISIBLE);

        //TextView Initialize: Steps, North, East
        TextView steps = new TextView(getApplicationContext());
        TextView north = new TextView(getApplicationContext());
        TextView east = new TextView(getApplicationContext());

        mSensorEventListener msel;
        //Sensor Manager and Listener initialise:  
        msel = new mSensorEventListener(steps, north, east, graph);

        //Listeners Registered:
        sensorManager.registerListener(msel, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(msel, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_FASTEST);


        //Initialization of TextViews:

        layout.addView(graph); //Displays graph
        layout.addView(steps); //Displays number of steps taken
        layout.addView(north); //Displays displacement in North direction
        layout.addView(east); //Displays displacement in East direction

    }  // End of OnCreate() Method


    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightListener, lightSensor, sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(rotationListener, rotationSensor, sensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(magneticListener, magneticSensor, sensorManager.SENSOR_DELAY_FASTEST);
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightListener);
        sensorManager.unregisterListener(rotationListener);
        sensorManager.unregisterListener(magneticListener);
    }


    //Sensor Listener Class Definition:
    class mSensorEventListener implements SensorEventListener {

        TextView steps, n, e;
        LineGraphView G;
        int step = 0;
        double ns = 0.0f; //north-south
        double ew = 0.0f; //east-west
        boolean s1, s2, s3, s4 = false;

        float[] ROTATION = new float[9]; // Rotation Matrix
        float[] orientationData = new float[3]; //Orientation Matrix

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
                    accelerationData = event.values.clone();
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    rotationData = event.values.clone();
                    break;
            }


            float smoothaccel = 0.0f;  //Initialized float to store low filtered Z-axis accelerometer readings.


            //Get Orientation:
            SensorManager.getRotationMatrixFromVector(ROTATION, rotationData);
            SensorManager.getOrientation(ROTATION, orientationData);

            //Low Pass Filter Z-Axis Steps:
            float c = 14f;
            float acceleration = MainActivity.this.accelerationData[2];
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
            G.addPoint(smoothGraph);
            steps.setText(" ---Steps--- \n Steps: " + step);

            //Displays Displacement due North,East:
            n.setText("\n--- Displacement--- \n North: " + ns);
            e.setText("East: " + ew);
        }
    }
}