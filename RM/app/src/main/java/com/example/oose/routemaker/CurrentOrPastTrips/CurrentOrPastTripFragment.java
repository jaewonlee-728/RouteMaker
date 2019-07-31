package com.example.oose.routemaker.CurrentOrPastTrips;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.oose.routemaker.Concrete.Trip;
import com.example.oose.routemaker.R;

import java.util.List;

public class CurrentOrPastTripFragment extends Fragment implements AdapterView.OnItemClickListener {

    /** list view for trip list */
    ListView list;

    /** Communicator to respond to activity when item is clicked */
    TripCommunicator comm;

    /** trip list to get attached to list view */
    public List<Trip> tripList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_trip, container, false);
        list = (ListView) view.findViewById(R.id.select_trip_list);

        setCommunicator((TripCommunicator) getActivity());
        tripList = comm.getTripList();

        if (tripList != null) {
            TripAdapter adapter = new TripAdapter(getActivity(), tripList);
            list.setAdapter(adapter);
            list.setOnItemClickListener(this);
        }

        return view;
    }

    /**
     * Set communicator for responding to action.
     * @param comm the communicator to set.
     */
    public void setCommunicator(TripCommunicator comm) {
        this.comm = comm;
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
        comm.respond(position);
    }
}
