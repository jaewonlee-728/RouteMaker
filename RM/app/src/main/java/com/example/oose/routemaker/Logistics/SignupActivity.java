package com.example.oose.routemaker.Logistics;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.oose.routemaker.API.RmAPI;
import com.example.oose.routemaker.Concrete.User;
import com.example.oose.routemaker.R;

import java.util.List;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignupActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton radioAgeButton;
    CheckBox museumBox;
    CheckBox artBox;
    CheckBox nightLifeBox;
    CheckBox entertainmentBox;
    CheckBox foodBox;
    CheckBox landmarkBox;
    CheckBox outdoorBox;
    CheckBox shoppingBox;

    //Retrofit
    String baseURL = "https://routemaker.herokuapp.com";   //BASE URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signUp();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_default);
        setSupportActionBar(toolbar);
    }

    /**
     * On click method to sign up a new user.
     */
    public void signUp() {
        final EditText firstNameField = (EditText) findViewById(R.id.first_name_field);
        final EditText lastNameField = (EditText) findViewById(R.id.last_name_field);
        final EditText emailField = (EditText) findViewById(R.id.email_field);
        final EditText passwordField = (EditText) findViewById(R.id.password_field);
        final EditText passwordField_confirm = (EditText) findViewById(R.id.password_field_confirm);
        radioGroup = (RadioGroup) findViewById(R.id.age_group_radio);

        museumBox = (CheckBox) findViewById(R.id.preference_museum);
        artBox = (CheckBox) findViewById(R.id.preference_art);
        nightLifeBox = (CheckBox) findViewById(R.id.preference_nightlife);
        entertainmentBox = (CheckBox) findViewById(R.id.preference_entertainment);
        foodBox = (CheckBox) findViewById(R.id.preference_food);
        landmarkBox = (CheckBox) findViewById(R.id.preference_landmark);
        outdoorBox = (CheckBox) findViewById(R.id.preference_outdoor);
        shoppingBox = (CheckBox) findViewById(R.id.preference_shopping);

        final Button button = (Button) findViewById(R.id.register_button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioAgeButton = (RadioButton) findViewById(selectedId);
                final List<String> preferenceList = new ArrayList<>();

                final String firstName = firstNameField.getText().toString();
                final String lastName = lastNameField.getText().toString();
                final String email = emailField.getText().toString();
                final String password = passwordField.getText().toString();
                final String password_confirm = passwordField_confirm.getText().toString();
                final String ageGroup = radioAgeButton.getText().toString();

                boolean isChecked = false;
                if (museumBox.isChecked()) {
                    preferenceList.add(museumBox.getText().toString());
                    isChecked = true;
                }
                if (artBox.isChecked()) {
                    preferenceList.add(artBox.getText().toString());
                    isChecked = true;
                }
                if (nightLifeBox.isChecked()) {
                    preferenceList.add("NightLife");
                    isChecked = true;
                }
                if (entertainmentBox.isChecked()) {
                    preferenceList.add(entertainmentBox.getText().toString());
                    isChecked = true;
                }
                if (foodBox.isChecked()) {
                    preferenceList.add(foodBox.getText().toString());
                    isChecked = true;
                }
                if (landmarkBox.isChecked()) {
                    preferenceList.add(landmarkBox.getText().toString());
                    isChecked = true;
                }
                if (outdoorBox.isChecked()) {
                    preferenceList.add(outdoorBox.getText().toString());
                    isChecked = true;
                }
                if (shoppingBox.isChecked()) {
                    preferenceList.add(shoppingBox.getText().toString());
                    isChecked = true;
                }

                if(isChecked) {
                    if (password.equals(password_confirm)) {
                        User user = new User(email, firstName, lastName, password, ageGroup, preferenceList);
                        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(baseURL).build();
                        RmAPI signUpAPI = restAdapter.create(RmAPI.class);
                        signUpAPI.createUser(user, new Callback<Boolean>() {
                            @Override
                            public void success(Boolean ret, Response response) {
                                if (ret) {
                                    Toast.makeText(getApplicationContext(), "Successfully registered.", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "ID already used! Please use different ID!", Toast.LENGTH_LONG).show();
                                }
                            }
                            @Override
                            public void failure(RetrofitError error) {
                                System.out.println(error);
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Password did not match. Please confirm password!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Choose at least one preference!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
