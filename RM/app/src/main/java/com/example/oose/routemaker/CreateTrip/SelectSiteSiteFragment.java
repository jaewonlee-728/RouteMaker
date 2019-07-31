package com.example.oose.routemaker.CreateTrip;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.oose.routemaker.Concrete.Event;
import com.example.oose.routemaker.Concrete.Site;
import com.example.oose.routemaker.R;

import java.util.ArrayList;
import java.util.List;

public class SelectSiteSiteFragment extends Fragment implements AdapterView.OnItemClickListener {

    /** parent activity */
    private SelectSiteActivity parentActivity;

    /** list view for site list */
    private ListView list;

    /** site list to get attached to list view */
    public List<Site> siteList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_site_site, container, false);
        list = (ListView) view.findViewById(R.id.select_site_site_list);

        siteList = SelectSiteInfo.SITES;

        if (siteList != null) {
            SiteAdapter adapter = new SiteAdapter(getActivity(), siteList);

            list.setAdapter(adapter);
            list.setOnItemClickListener(this);
            list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            list.setItemsCanFocus(false);
        } else {
            siteList = new ArrayList<>();
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        parentActivity = (SelectSiteActivity) getActivity();
        if (SelectSiteInfo.SITES == null) {
            SelectSiteInfo.SITES = new ArrayList<>();
        }
        restoreItems();
    }

    /**
     * Restore the checked state for previously selected items.
     */
    public void restoreItems() {
        ArrayList<Site> currSites = new ArrayList<>();
        if (parentActivity.selectedSites != null) {
            currSites = parentActivity.selectedSites;
        }
        for (int i = 0; i < siteList.size(); i++) {
            for (int j = 0; j < currSites.size(); j++) {
                if (siteList.get(i).equals(currSites.get(j))) {
                    list.setItemChecked(i, true);
                }
            }
        }
    }

    /**
     * Action when item in list view is selected.
     * @param adapterView adapter view where the click happened
     * @param view the view that was selected
     * @param position the position of the view in the list
     * @param id the id of the item
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        boolean result = ((SelectSiteActivity) getActivity()).setMarker(position);
        if (result) {
            ((SelectSiteActivity) getActivity()).addSite(position);
        } else {
            list.setItemChecked(position, false);
            Toast.makeText(getActivity().getApplicationContext(), "The location you selected is not open that day!", Toast.LENGTH_LONG).show();
        }
    }
}
