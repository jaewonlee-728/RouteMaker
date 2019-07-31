package com.example.oose.routemaker;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class NavDrawer implements ListView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        selectItem(position);
    }
    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
    }
}

