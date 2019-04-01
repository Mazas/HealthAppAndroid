package ivanauskas.tadas.heartmonitor.Model;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import ivanauskas.tadas.heartmonitor.HomeFragment;


public class BackgroundService extends IntentService implements MovementDetector.Listener,HeartrateMonitor.HeartListener{
    private float motion = 0;
    private boolean running = false;
    private double heartRate = 0;
    private MovementDetector movementDetector;
    private FirestoreConnector connector;



    public BackgroundService() {
        super("BackgroundService");
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        connector = new FirestoreConnector(getSharedPreferences("settings",MODE_PRIVATE).getString("email",null));
        movementDetector = new MovementDetector(getBaseContext());
        movementDetector.addListener(this);
        HomeFragment.attachListener(this);
        running = true;
        movementDetector.start();
//        new Thread() {
//            @Override
//            public void run() {
//                while (running) {
//                    try {
//                        sendData();
//                        Thread.sleep(10000);
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//                movementDetector.stop();
//            }
//        }.start();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    private void sendData(){
        Log.e("BackgroundService", "motion: "+motion);

        Intent intent = new Intent("intent_filter");
        intent.putExtra("rate",String.valueOf(heartRate));
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.sendBroadcast(intent);


        connector.update(motion,heartRate);
    }


    @Override
    public void onMotionDetected(SensorEvent event, float acceleration) {
        motion = acceleration;
    }

    @Override
    public void heartBeat(double rate) {
        heartRate = rate;
        sendData();
    }
}
