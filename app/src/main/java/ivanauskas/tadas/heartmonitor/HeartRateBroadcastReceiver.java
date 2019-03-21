package ivanauskas.tadas.heartmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.HashMap;

class HeartRateBroadcastReceiver extends BroadcastReceiver {
    private BroadcastListener listener;
    public HeartRateBroadcastReceiver(BroadcastListener listener){
        this.listener=listener;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        HashMap<String, String> data = new HashMap<>();
        data.put("rate", intent.getStringExtra("rate"));
        listener.handleBroadcast(data);
    }
}
