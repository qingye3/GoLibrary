package org.qing.golibrary.app.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import org.qing.golibrary.app.CreateAlarmActivity;
import org.qing.golibrary.app.R;

public class ViewAlarmsFragment extends Fragment {
    public ViewAlarmsFragment() {
        super();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alarms, container, false);

        ImageButton createAlarmButton = (ImageButton) rootView.findViewById(R.id.add_button);
        createAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateAlarmActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
}
