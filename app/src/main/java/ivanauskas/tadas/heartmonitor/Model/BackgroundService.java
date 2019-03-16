package ivanauskas.tadas.heartmonitor.Model;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.hardware.SensorEvent;
import android.support.annotation.Nullable;
import android.util.Log;


public class BackgroundService extends IntentService implements MovementDetector.Listener{
    private static final String START = "ivanauskas.tadas.heartmonitor.Model.action.START";
    private static final String STOP = "ivanauskas.tadas.heartmonitor.Model.action.STOP";
    private static final String RUN = "ivanauskas.tadas.heartmonitor.Model.action.RUN";
    private float motion = 0;
    private boolean running = false;



    public BackgroundService() {
        super("BackgroundService");
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        running = true;
        new Thread() {
            @Override
            public void run() {
                while (running) {
                    try {
                        sendData();
                        Thread.sleep(1000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    @Override
    public void onMotionDetected(SensorEvent event, float acceleration) {
        motion = acceleration;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    private void sendData(){
        Log.e("BackgroundService", "HELLO!");
        // TODO send data
    }
}
