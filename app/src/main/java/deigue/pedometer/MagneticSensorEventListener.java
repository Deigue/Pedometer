package deigue.pedometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

/**
 * Created by Deigue on 12/31/2014.
 */
public class MagneticSensorEventListener implements SensorEventListener {

    TextView magneticOutput;

    public MagneticSensorEventListener(TextView outputView){
        magneticOutput = outputView;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){

            magneticOutput.setText("X axis: " + (String.format("%.2f", event.values[0]))
                    + "                Y axis: " + (String.format("%.2f", event.values[1]))
                    + "                Z axis: " + (String.format("%.2f", event.values[2])));

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
