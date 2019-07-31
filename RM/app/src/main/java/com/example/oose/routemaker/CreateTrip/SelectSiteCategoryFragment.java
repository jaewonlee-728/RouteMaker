package com.example.oose.routemaker.CreateTrip;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.oose.routemaker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for category buttons in SelectSiteActivity.
 */
public class SelectSiteCategoryFragment extends Fragment implements View.OnClickListener {

    /** Buttons for 7 different categories */
    Button button_1;
    Button button_2;
    Button button_3;
    Button button_4;
    Button button_5;
    Button button_6;
    Button button_7;

    /** list containing buttons */
    List<Button> category_buttons = new ArrayList<>();

    /** the selected button */
    Button selected_button;

    /** Communicator to send request to SelectSiteActivity */
    Communicator comm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_site_category, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        comm = (Communicator) getActivity();
        button_1 = (Button) getActivity().findViewById(R.id.select_category_1_button);
        button_1.setOnClickListener(this);
        button_2 = (Button) getActivity().findViewById(R.id.select_category_2_button);
        button_2.setOnClickListener(this);
        button_3 = (Button) getActivity().findViewById(R.id.select_category_3_button);
        button_3.setOnClickListener(this);
        button_4 = (Button) getActivity().findViewById(R.id.select_category_4_button);
        button_4.setOnClickListener(this);
        button_5 = (Button) getActivity().findViewById(R.id.select_category_5_button);
        button_5.setOnClickListener(this);
        button_6 = (Button) getActivity().findViewById(R.id.select_category_6_button);
        button_6.setOnClickListener(this);
        button_7 = (Button) getActivity().findViewById(R.id.select_category_7_button);
        button_7.setOnClickListener(this);

        selected_button = button_1;

        category_buttons.add(button_1);
        category_buttons.add(button_2);
        category_buttons.add(button_3);
        category_buttons.add(button_4);
        category_buttons.add(button_5);
        category_buttons.add(button_6);
        category_buttons.add(button_7);
    }

    /**
     * Action when button is clicked.
     * @param v the view of the button
     */
    @Override
    public void onClick(View v) {
        int category_id = 1;
        if (!(v.isSelected())) {
            setAllOthersFalse();
            v.setSelected(true);
            category_id = category_buttons.indexOf(v) + 1;
            comm.respond(0, category_id);
        }
    }

    /**
     * Set all other category buttons as not selected
     */
    private void setAllOthersFalse() {
        for (Button b : category_buttons) {
            b.setSelected(false);
        }
    }
}
