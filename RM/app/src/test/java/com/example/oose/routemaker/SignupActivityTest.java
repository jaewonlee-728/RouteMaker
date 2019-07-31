package com.example.oose.routemaker;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.oose.routemaker.Logistics.SignupActivity;

public class SignupActivityTest extends ActivityInstrumentationTestCase2<SignupActivity> {

    SignupActivity activity;

    public SignupActivityTest() {
        super(SignupActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @SmallTest
    public void testViewNotNull() throws Exception {
        TextView firstNameText = (TextView) activity.findViewById(R.id.first_name_text);
        TextView lastNameText = (TextView) activity.findViewById(R.id.last_name_text);
        TextView emailText = (TextView) activity.findViewById(R.id.email_text);
        TextView passwordText = (TextView) activity.findViewById(R.id.password_text);
        TextView ageGroupText = (TextView) activity.findViewById(R.id.age_group_text);
        TextView preferenceText = (TextView) activity.findViewById(R.id.preference_text);

        EditText firstNameField = (EditText) activity.findViewById(R.id.first_name_field);
        EditText lastNameField = (EditText) activity.findViewById(R.id.last_name_field);
        EditText emailField = (EditText) activity.findViewById(R.id.email_field);
        EditText passwordField = (EditText) activity.findViewById(R.id.password_field);
        EditText passwordField_confirm = (EditText) activity.findViewById(R.id.password_field_confirm);

        CheckBox museumBox = (CheckBox) activity.findViewById(R.id.preference_museum);
        CheckBox artBox = (CheckBox) activity.findViewById(R.id.preference_art);
        CheckBox nightLifeBox = (CheckBox) activity.findViewById(R.id.preference_nightlife);
        CheckBox entertainmentBox = (CheckBox) activity.findViewById(R.id.preference_entertainment);
        CheckBox foodBox = (CheckBox) activity.findViewById(R.id.preference_food);
        CheckBox landmarkBox = (CheckBox) activity.findViewById(R.id.preference_landmark);
        CheckBox outdoorBox = (CheckBox) activity.findViewById(R.id.preference_outdoor);
        CheckBox shoppingBox = (CheckBox) activity.findViewById(R.id.preference_shopping);

        assertNotNull(firstNameText);
        assertNotNull(lastNameText);
        assertNotNull(emailText);
        assertNotNull(passwordText);
        assertNotNull(ageGroupText);
        assertNotNull(preferenceText);

        assertNotNull(firstNameField);
        assertNotNull(lastNameField);
        assertNotNull(emailField);
        assertNotNull(passwordField);
        assertNotNull(passwordField_confirm);

        assertNotNull(museumBox);
        assertNotNull(artBox);
        assertNotNull(nightLifeBox);
        assertNotNull(entertainmentBox);
        assertNotNull(foodBox);
        assertNotNull(landmarkBox);
        assertNotNull(outdoorBox);
        assertNotNull(shoppingBox);

    }
}
