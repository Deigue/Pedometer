package deigue.pedometer;

import java.util.Arrays;

import android.app.Activity;
import android.graphics.Typeface;
import android.hardware.Sensor;
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
    AccelerometerSensorEventListener accelerationListener;



   // float[] smoothGraph = new float[1];


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

      //  lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
       // magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

/*

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

*/

        //ACCELERATION ROTATION ACCESS TEST:
        TextView deviceOrientation = new TextView(getApplicationContext());
        TextView orientationView =  new TextView(getApplicationContext());
        TextView accelerationView =  new TextView(getApplicationContext());
        TextView accelerationMaxView = new TextView((getApplicationContext()));
        TextView accelerationMinView = new TextView((getApplicationContext()));
        TextView states = new TextView((getApplicationContext()));

        final float[] max = new float[3];
        final float[] min = new float[3];
        int steps=0;
        deviceOrientation.setLayoutParams(layoutParams);
        deviceOrientation.setText("Device Orientation: ");
        graph = new LineGraphView(getApplicationContext(), 100, Arrays.asList("X", "Y", "Z"));
        accelerationListener = new AccelerometerSensorEventListener(graph, orientationView, accelerationView,accelerationMaxView, max, accelerationMinView, min,steps, states);
        sensorManager.registerListener(accelerationListener, accelerometerSensor, sensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(accelerationListener, rotationSensor, sensorManager.SENSOR_DELAY_FASTEST);
        deviceOrientation.setTypeface(Typeface.DEFAULT_BOLD);
        layout.addView(deviceOrientation);
        layout.addView(orientationView);
        layout.addView(accelerationView);
        layout.addView(accelerationMaxView);
        layout.addView(accelerationMinView);
        TextView stepsDone = new TextView(getApplicationContext());
        stepsDone.setText("Steps taken: "+steps);
        layout.addView(stepsDone);
        layout.addView(states);
        layout.addView(graph);
        graph.setVisibility(View.VISIBLE);

        //Reset Button
        Button Resets = (Button) findViewById(R.id.reset);
        Resets.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                graph.purge();
                for(int i=0; i <3; i ++){
                    max[i] = 0;
                    min[i] = 0;
                }
            }

        });




        //---------------------------------



        /*
        //Graph Title:
        TextView graphtitle = new TextView(getApplicationContext());
        graphtitle.setText("\nAcceleration Graph:");
        layout.addView(graphtitle);
        */
        //graph.setVisibility(View.VISIBLE);

        //TextView Initialize: Steps, North, East
        //TextView steps = new TextView(getApplicationContext());
        //TextView north = new TextView(getApplicationContext());
        //TextView east = new TextView(getApplicationContext());

        //mSensorEventListener msel;
        //Sensor Manager and Listener initialise:  
      //  accelerationListener = new mSensorEventListener(steps, north, east, graph);

        //Listeners Registered:
       // sensorManager.registerListener(accelerationListener, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_FASTEST);
       // sensorManager.registerListener(accelerationListener, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_FASTEST);


        //Initialization of TextViews:
        /*
        layout.addView(graph); //Displays graph
        layout.addView(steps); //Displays number of steps taken
        layout.addView(north); //Displays displacement in North direction
        layout.addView(east); //Displays displacement in East direction
        */

    }


    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightListener, lightSensor, sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(rotationListener, rotationSensor, sensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(magneticListener, magneticSensor, sensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(accelerationListener, magneticSensor, sensorManager.SENSOR_DELAY_FASTEST);

    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightListener);
        sensorManager.unregisterListener(rotationListener);
        sensorManager.unregisterListener(magneticListener);
    }


}
