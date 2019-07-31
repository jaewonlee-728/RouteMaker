package com.example.oose.routemaker;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.oose.routemaker.Logistics.MainActivity;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity activity;

    public MainActivityTest() {
        super(MainActivity.class);
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
        TextView welcomeMessage = (TextView) activity.findViewById(R.id.textView);
        TextView signupMessage = (TextView) activity.findViewById(R.id.textView2);
        EditText emailField = (EditText) activity.findViewById(R.id.email_login_field);
        EditText passwordField = (EditText) activity.findViewById(R.id.password_login_field);
        Button loginButton = (Button) activity.findViewById(R.id.button_login);
        Button signupButton = (Button) activity.findViewById(R.id.button_signup);

        assertNotNull(welcomeMessage);
        assertNotNull(signupMessage);
        assertNotNull(emailField);
        assertNotNull(passwordField);
        assertNotNull(loginButton);
        assertNotNull(signupButton);
    }
}
