package com.example.oose.routemaker.Logistics;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.oose.routemaker.API.RmAPI;
import com.example.oose.routemaker.Concrete.User;
import com.example.oose.routemaker.Constants;
import com.example.oose.routemaker.NewsFeedActivity;
import com.example.oose.routemaker.R;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    //Retrofit
    String baseURL = "https://routemaker.herokuapp.com";   //BASE URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!isOnline()) {
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
                alertDialog.setTitle("Info");
                alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again");
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.show();
            }
            catch(Exception e)
            {
                Log.d(Constants.TAG, "Show Dialog: " + e.getMessage());
            }
        }

        // Attach an listener to read the data at our posts reference
        final Button button = (Button) findViewById(R.id.button_signup);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        final EditText login_email_field = (EditText) findViewById(R.id.email_login_field);
        final EditText login_password_field = (EditText) findViewById(R.id.password_login_field);
        final Button button2 = (Button) findViewById(R.id.button_login);

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String login_email = login_email_field.getText().toString();
                final String login_password = login_password_field.getText().toString();

                RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(baseURL).build();
                RmAPI loginAPI = restAdapter.create(RmAPI.class);
                loginAPI.getUser(login_email, new Callback<User>() {
                    @Override
                    public void success(User user, Response response) {
                        if (user == null) {
                            Toast.makeText(getApplicationContext(), "Login Failed! Please check your email and/or password!", Toast.LENGTH_LONG).show();
                        } else {
                            if (user.getPassword().equals(login_password)) {
                                Intent intent = new Intent(MainActivity.this, NewsFeedActivity.class);
                                intent.putExtra("userId", user.getEmail());
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Login Failed! Please check your password!", Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                    @Override
                    public void failure(RetrofitError error) {
                        System.out.println(error);
                    }
                });
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(getApplicationContext(), "Valid internet connection needed for RouteMaker", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
