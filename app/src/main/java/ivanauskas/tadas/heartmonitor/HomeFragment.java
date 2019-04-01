package ivanauskas.tadas.heartmonitor;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import ivanauskas.tadas.heartmonitor.Model.HeartrateMonitor;
import ivanauskas.tadas.heartmonitor.Model.ImageProcessing;
import ivanauskas.tadas.heartmonitor.Model.RequestUserPermission;


public class HomeFragment extends Fragment implements BroadcastListener{
    private OnFragmentInteractionListener mListener;
    private static TextView rate;

    private static final String TAG = "HomeFragment";
    private static final AtomicBoolean processing = new AtomicBoolean(false);

    private static SurfaceView preview = null;
    private static SurfaceHolder previewHolder = null;
    private static Camera camera = null;
    private static View image = null;

    private static PowerManager.WakeLock wakeLock = null;

    private static int averageIndex = 0;
    private static final int averageArraySize = 4;
    private static final int[] averageArray = new int[averageArraySize];

    private static HeartrateMonitor.HeartListener listener;

    public static enum TYPE {
        GREEN, RED
    };

    private static TYPE currentType = TYPE.GREEN;

    public static TYPE getCurrent() {
        return currentType;
    }

    private static int beatsIndex = 0;
    private static final int beatsArraySize = 3;
    private static final int[] beatsArray = new int[beatsArraySize];
    private static double beats = 0;
    private static long startTime = 0;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RequestUserPermission requestUserPermission = new RequestUserPermission(getActivity());
        requestUserPermission.verifyStoragePermissions();
        PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "HealthApp:DoNotDimScreen");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        rate = view.findViewById(R.id.beatRate);


        preview = view.findViewById(R.id.preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        rate.setText(String.format("%s %s", String.valueOf(0.0), getString(R.string.BPM)));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new HeartRateBroadcastReceiver(this),new IntentFilter("intent_filter"));
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        wakeLock.acquire(10*60*1000L /*10 minutes*/);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //ask for authorisation
            Log.e("camera", "requesting permissions");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 50);
        } else {
            //start your camera
            Log.e("camera", "opening camera");
            camera = Camera.open();
            startTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        wakeLock.release();

        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void handleBroadcast(HashMap data) {
        this.rate.setText(String.format("%s %s", String.valueOf(data.get("rate")), getString(R.string.BPM)));
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static void attachListener(HeartrateMonitor.HeartListener listener){
        HomeFragment.listener = listener;
    }

    private static Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            try {
                if (data == null) throw new NullPointerException();
                Camera.Size size = cam.getParameters().getPreviewSize();
                if (size == null) throw new NullPointerException();

                if (!processing.compareAndSet(false, true)) return;

                int width = size.width;
                int height = size.height;

                int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), width,height);
                //Log.i(TAG, "imgAvg=" + imgAvg);
                if (imgAvg == 0 || imgAvg == 255) {
                    processing.set(false);
                    return;
                }

                int averageArrayAvg = 0;
                int averageArrayCnt = 0;
                for (int anAverageArray : averageArray) {
                    if (anAverageArray > 0) {
                        averageArrayAvg += anAverageArray;
                        averageArrayCnt++;
                    }
                }

                int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
                TYPE newType = currentType;
                if (imgAvg < rollingAverage) {
                    newType = TYPE.RED;
                    if (newType != currentType) {
                        beats++;
                        Log.d(TAG, "BEAT!! beats=" + beats);
                    }
                } else if (imgAvg > rollingAverage) {
                    newType = TYPE.GREEN;
                }

                if (averageIndex == averageArraySize) averageIndex = 0;
                averageArray[averageIndex] = imgAvg;
                averageIndex++;

                // Transitioned from one state to another to the same
                if (newType != currentType) {
                    currentType = newType;
                }

                long endTime = System.currentTimeMillis();
                double totalTimeInSecs = (endTime - startTime) / 1000d;
                if (totalTimeInSecs >= 10) {
                    double bps = (beats / totalTimeInSecs);
                    int dpm = (int) (bps * 60d);
                    if (dpm < 30 || dpm > 180) {
                        startTime = System.currentTimeMillis();
                        beats = 0;
                        processing.set(false);
                        return;
                    }

                    // Log.d(TAG,
                    // "totalTimeInSecs="+totalTimeInSecs+" beats="+beats);

                    if (beatsIndex == beatsArraySize) beatsIndex = 0;
                    beatsArray[beatsIndex] = dpm;
                    beatsIndex++;

                    int beatsArrayAvg = 0;
                    int beatsArrayCnt = 0;
                    for (int aBeatsArray : beatsArray) {
                        if (aBeatsArray > 0) {
                            beatsArrayAvg += aBeatsArray;
                            beatsArrayCnt++;
                        }
                    }
                    int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
                    listener.heartBeat(beatsAvg);
                    startTime = System.currentTimeMillis();
                    beats = 0;
                }
                processing.set(false);
            }catch (Exception e){
                e.printStackTrace();
                Snackbar.make(rate.getRootView(),e.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        }
    };
    private static SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
                Log.e("PreviewSurfaceCallback", "Exception in setPreviewDisplay()", t);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
            }
            camera.setParameters(parameters);
            camera.startPreview();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // Ignore
        }
    };
    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea < resultArea) result = size;
                }
            }
        }

        return result;
    }
}
