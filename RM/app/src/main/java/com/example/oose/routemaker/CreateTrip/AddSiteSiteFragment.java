package com.example.oose.routemaker.CreateTrip;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.oose.routemaker.R;

import java.util.ArrayList;

public class AddSiteSiteFragment extends ListFragment {

    /** parent activity */
    AddSiteActivity parentActivity;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        parentActivity = (AddSiteActivity) getActivity();
        ArrayList<String> siteNames = new ArrayList<>();
        for (int i = 0; i < SelectSiteInfo.SITES.size(); i++) {
            siteNames.add(SelectSiteInfo.SITES.get(i).getSiteName());
        }

        ArrayAdapter<String> connectArrayToListView = new
                ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, siteNames);

        setListAdapter(connectArrayToListView);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        View SiteFrame = getActivity().findViewById(R.id.add_site_map_fragment);
    }

    /**
     * Action when item in list view is selected.
     * @param l listview where the click happened
     * @param v the view that was selected
     * @param position the position of the view in the list
     * @param id the id of the item
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        boolean result = ((AddSiteActivity) getActivity()).setMarker(position);;
        if (result) {
            ((AddSiteActivity) getActivity()).setSite(position);
        } else {
            getListView().setItemChecked(position, false);
            Toast.makeText(getActivity().getApplicationContext(), "The location you selected is not open that day!", Toast.LENGTH_LONG).show();
        }
    }
}

