package ivanauskas.tadas.heartmonitor.Model;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.HashSet;

import static android.content.Context.SENSOR_SERVICE;

@Deprecated
public class HeartrateMonitor implements SensorEventListener {

    private Sensor mHeartRateSensor;
    private SensorManager mSensorManager;


    public HeartrateMonitor(Context context) {
        try {
            mSensorManager = ((SensorManager) context.getSystemService(SENSOR_SERVICE));
            mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_BEAT);
        } catch (NullPointerException e) {
            Log.e("HeartrateMonitor", e.getMessage());
        }

    }

    private HashSet<HeartListener> mListeners = new HashSet<>();

    public void start() {
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        mSensorManager.unregisterListener(this);
    }

    public void addListener(HeartListener listener) {
        mListeners.add(listener);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_BEAT) {
            for (HeartListener listener : mListeners) {
                listener.heartBeat(event.values[0]);
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public interface HeartListener {
        void heartBeat(double rate);
    }
}
