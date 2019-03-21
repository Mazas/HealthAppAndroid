package ivanauskas.tadas.heartmonitor;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.HashMap;


public class SettingsPage extends Fragment {

    private EditText name;
    private String dobText;
    private DatePicker dateOfBirth;
    private NumberPicker gender;


    private OnFragmentInteractionListener mListener;

    public SettingsPage() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_settings_page, container, false);

        name = view.findViewById(R.id.settings_name);
        gender = view.findViewById(R.id.settings_gender);
        dateOfBirth = view.findViewById(R.id.settings_date_of_birth);

        String[] genders = {"Male", "Female"};
        gender.setMaxValue(1);
        gender.setDisplayedValues(genders);
        gender.setValue(0);
        dateOfBirth.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);

        dateOfBirth.setMaxDate(System.currentTimeMillis());
        dateOfBirth.init(2000,6,6,null);
        // Inflate the layout for this fragment

        Button btn = view.findViewById(R.id.setting_update_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> data =  new HashMap<>();
                data.put("name", name.getText().toString());
                dobText = String.valueOf(dateOfBirth.getYear());
                if (dateOfBirth.getMonth()<10) {
                    dobText = dobText + "-0" + dateOfBirth.getMonth();
                }else {
                    dobText = dobText+"-"+dateOfBirth.getMonth();
                }
                if (dateOfBirth.getDayOfMonth()<10){
                    dobText = dobText+"-0"+dateOfBirth.getDayOfMonth();
                }else {
                    dobText = dobText+"-"+dateOfBirth.getDayOfMonth();
                }
                data.put("date_of_birth",dobText);
                data.put("gender",gender.getDisplayedValues()[gender.getValue()]);
                Toast.makeText(v.getContext(),"User updated",Toast.LENGTH_SHORT).show();
                mListener.onFragmentInteraction("updateUser", data);
            }
        });

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
}
