package com.example.oose.routemaker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.oose.routemaker.API.RmAPI;
import com.example.oose.routemaker.Concrete.User;
import com.example.oose.routemaker.Logistics.SettingActivity;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Popup that asks for the user's password once the user clicks Setting on the drawer.
 */
public class Pop extends Activity {

    /** User's userId. */
    String userId;

    //Retrofit
    String baseURL = "https://routemaker.herokuapp.com";   //BASE URL

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow);

        //This is a popup, so we set the dimensions.
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .7), (int) (height * .5));

        final EditText password_popup_field  = (EditText) findViewById(R.id.password_popup_field);

        Bundle extras = getIntent().getExtras();
        userId = extras.getString("userId");

        //Password enter button.
        Button button = (Button) findViewById(R.id.popup_enter_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String popup_password = password_popup_field.getText().toString();
                RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(baseURL).build();
                RmAPI popUpAPI = restAdapter.create(RmAPI.class);
                popUpAPI.getUser(userId, new Callback<User>() {
                    @Override
                    public void success(User user, Response response) {
                        if (popup_password.equals(user.getPassword())) {
                            Intent intent = new Intent(Pop.this, NewsFeedActivity.class);
                            intent.putExtra("userInfo", user);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Please check your password!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }
        });
    }

    /**
     * On back button pressed, cancel the transaction.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }
}
