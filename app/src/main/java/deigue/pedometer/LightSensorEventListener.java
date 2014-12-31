package deigue.pedometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

/**
 * Created by Deigue on 12/31/2014.
 */
public class LightSensorEventListener implements SensorEventListener {

       TextView lightOutput;

    public LightSensorEventListener(TextView outputView){
        lightOutput = outputView;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType()== Sensor.TYPE_LIGHT){
            lightOutput.setText("Light Intensity: " + event.values[0]);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
