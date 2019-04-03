package ivanauskas.tadas.heartmonitor.Model;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;


import ivanauskas.tadas.heartmonitor.HomeFragment;
import ivanauskas.tadas.heartmonitor.R;


public class BackgroundService extends IntentService implements MovementDetector.Listener,HeartrateMonitor.HeartListener{
    private float motion = 0;
    private boolean running = false;
    private double heartRate = 0;
    private FirestoreConnector connector;



    public BackgroundService() {
        super("BackgroundService");
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        connector = new FirestoreConnector(getSharedPreferences("settings",MODE_PRIVATE).getString("email",null));
        MovementDetector movementDetector = new MovementDetector(getBaseContext());
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
        if ((motion>3&& heartRate>172)||(motion<3&& heartRate>90)){ // bpm is way too high
            sendWarning("too high");
        }else if(motion<3&& heartRate<60) {
            sendWarning("too low");
        }
        sendData();
    }

    private void sendWarning(String message){

        createNotificationChannel();
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this,"HealthApp")
                        .setSmallIcon(R.drawable.hearth_ico)
                        .setContentTitle("Are you ok?")
                        .setContentText("Your hearth rate is "+message+"!");

        Toast.makeText(getApplication().getApplicationContext(),"Notified!",Toast.LENGTH_SHORT).show();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(1, mBuilder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "HealthApp";
            String description = "HealthApp";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("HealthApp", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
