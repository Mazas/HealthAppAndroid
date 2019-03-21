package ivanauskas.tadas.heartmonitor.Model;

/**
 * https://github.com/alassadi/SmartHomeAndroid/edit/master/app/src/main/java/androidapp/smarthome/HttpHandler.java
 * */

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpHandler {
    private static final String TAG = HttpHandler.class.getSimpleName();

    public HttpHandler() {
    }

    public void requestCreateUser(JSONObject jsonObject) {
        new taskCreateUser().execute(jsonObject);
    }

    public static class taskCreateUser extends AsyncTask<JSONObject, Void, Void> {
        @Override
        protected Void doInBackground(JSONObject... jsonObjects) {

            try {
                // send post request
                URL url = new URL("https://europe-west1-cloudproject-f25f2.cloudfunctions.net/register/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

                connection.setRequestProperty("Content-type", "application/json");
                connection.setDoOutput(true);

                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.writeBytes(jsonObjects[0].toString());
                dataOutputStream.flush();
                dataOutputStream.close();

                Log.i(TAG, "post createUser: " + jsonObjects[0]);
                Log.i(TAG, "server status: " + connection.getResponseCode());
                Log.i(TAG, "server msg: " + connection.getResponseMessage());

            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            }

            return null;
        }
    }

}
