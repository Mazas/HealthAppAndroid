package ivanauskas.tadas.heartmonitor.Model;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.support.annotation.Nullable;
import android.util.Log;


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
                        Thread.sleep(1000);
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
        connector.update(motion,heartrateMonitor.getHeartRate());
    }


    @Override
    public void onMotionDetected(SensorEvent event, float acceleration) {
        motion = acceleration;
    }
}
