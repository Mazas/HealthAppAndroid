package ivanauskas.tadas.heartmonitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ivanauskas.tadas.heartmonitor.Model.HttpHandler;

public class RegistrationActivity extends AppCompatActivity {
    private EditText email, password, passwordRepeat, name;
    private String emailText,passwordText, nameText, genderText, dobText;
    private DatePicker dateOfBirth;
    private NumberPicker gender;

    private JSONObject userObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        passwordRepeat = findViewById(R.id.register_password_repeat);
        name = findViewById(R.id.register_name);
        gender = findViewById(R.id.register_gender);
        dateOfBirth =findViewById(R.id.register_date_of_birth);

        String[] genders = {"Male", "Female"};
        gender.setMaxValue(1);
        gender.setDisplayedValues(genders);
        gender.setValue(0);

    }

    public void register(View view){
        readValues();
        validate();
        createJsonObject();

        new HttpHandler().requestCreateUser(userObject);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    private void readValues(){
        emailText = email.getText().toString();
        passwordText = password.getText().toString();
        nameText = name.getText().toString();
        genderText = gender.getDisplayedValues()[gender.getValue()];
        dobText = dateOfBirth.getYear()+"-"+dateOfBirth.getMonth()+"-"+dateOfBirth.getDayOfMonth();
    }

    private boolean validate(){
        if (!emailText.contains("@")){
            showError("Invalid Email Address.");
            return false;
        }
        if (!passwordText.equals(passwordRepeat.getText().toString())){
            showError("Passwords must match.");
            return false;
        }
        return true;

    }

    private void createJsonObject(){
        try {
            userObject = new JSONObject();
            userObject.put("email", emailText);
            userObject.put("name",nameText);
            userObject.put("password",passwordText);
            userObject.put("gender",genderText);
            userObject.put("date_of_birth",dobText);
            // yyyy-mm-dd - date format
        }catch (JSONException ex){
            Log.d("JSON",ex.getMessage());
        }


    }

    private void showError(String error_message){
        Toast.makeText(this,error_message,Toast.LENGTH_SHORT).show();
    }
}
