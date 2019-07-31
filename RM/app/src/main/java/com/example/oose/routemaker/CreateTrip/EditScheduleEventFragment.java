package com.example.oose.routemaker.CreateTrip;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.oose.routemaker.Concrete.Event;
import com.example.oose.routemaker.R;

import java.util.List;

/**
 * Fragment for event list in EditScheduleActivity.
 */
public class EditScheduleEventFragment extends Fragment implements AdapterView.OnItemClickListener {

    /** list view for event list */
    ListView list;

    /** Communicator to respond to activity when item is clicked */
    EditCommunicator comm;

    /** event list to get attached to list view */
    public List<Event> eventList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_schedule_site, container, false);
        list = (ListView) view.findViewById(R.id.edit_schedule_event_list);

        eventList = EditScheduleActivity.currEventList;
        comm = (EditCommunicator) getActivity();

        if (eventList != null) {
            EventAdapter adapter = new EventAdapter(getActivity(), eventList);

            list.setAdapter(adapter);
            list.setOnItemClickListener(this);
        }
        return view;
    }

    /**
     * Set communicator for responding to action.
     * @param comm the communicator to set.
     */
    public void setCommunicator(EditCommunicator comm) {
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

    /**
     * Clears data in event list and notifies adapter of the change.
     */
    public void clearData() {
        EventAdapter adapter = (EventAdapter) list.getAdapter();
        if (eventList != null) {
            eventList.clear();
            adapter.notifyDataSetChanged();
        }
    }

}
