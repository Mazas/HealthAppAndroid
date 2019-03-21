package ivanauskas.tadas.heartmonitor.Model;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import ivanauskas.tadas.heartmonitor.HomeFragment;


public class BackgroundService extends IntentService implements MovementDetector.Listener{
    private static final String START = "ivanauskas.tadas.heartmonitor.Model.action.START";
    private static final String STOP = "ivanauskas.tadas.heartmonitor.Model.action.STOP";
    private static final String RUN = "ivanauskas.tadas.heartmonitor.Model.action.RUN";
    private float motion = 0;
    private boolean running = false;
    private MovementDetector movementDetector;
    private FirestoreConnector connector;
    private HeartrateMonitor heartrateMonitor;



    public BackgroundService() {
        super("BackgroundService");
        heartrateMonitor = new HeartrateMonitor();
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        connector = new FirestoreConnector(getSharedPreferences("settings",MODE_PRIVATE).getString("email",null));
        movementDetector = new MovementDetector(getBaseContext());
        movementDetector.addListener(this);
        running = true;
        movementDetector.start();
        new Thread() {
            @Override
            public void run() {
                while (running) {
                    try {
                        sendData();
                        Thread.sleep(10000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                movementDetector.stop();
            }
        }.start();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    private void sendData(){
        Log.e("BackgroundService", "motion: "+motion);
        double rate = heartrateMonitor.getHeartRate();

        Intent intent = new Intent("intent_filter");
        intent.putExtra("rate",String.valueOf(rate));
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.sendBroadcast(intent);


        connector.update(motion,rate);
    }


    @Override
    public void onMotionDetected(SensorEvent event, float acceleration) {
        motion = acceleration;
    }
}
