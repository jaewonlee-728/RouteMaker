package com.example.oose.routemaker.CreateTrip;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * activity for setting up fragment for site list
 */
public class AddSiteSiteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            AddSiteSiteFragment sites = new AddSiteSiteFragment();

            sites.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(android.R.id.content, sites).commit();
        }
    }
}
