package ivanauskas.tadas.heartmonitor;

import android.app.Fragment;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

// TODO implement home page


public class HomeFragment extends Fragment implements BroadcastListener{
    private OnFragmentInteractionListener mListener;
    private TextView rate;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        rate = view.findViewById(R.id.beatRate);

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void handleBroadcast(HashMap data) {
        this.rate.setText(String.format("%s %s", String.valueOf(data.get("rate")), getString(R.string.BPM)));
    }
}
