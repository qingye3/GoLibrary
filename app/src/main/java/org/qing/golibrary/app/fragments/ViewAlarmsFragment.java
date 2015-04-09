package org.qing.golibrary.app.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import org.qing.golibrary.app.CreateAlarmActivity;
import org.qing.golibrary.app.R;
import org.qing.golibrary.app.adapters.AlarmListAdapter;
import org.qing.golibrary.app.database.Alarm;
import org.qing.golibrary.app.database.AlarmDataSource;

import java.sql.SQLException;
import java.util.ArrayList;

public class ViewAlarmsFragment extends Fragment {
    private static final String TAG = "AlarmFragment";
    private AlarmListAdapter adapter = null;
    private AlarmDataSource datasource;


    public ViewAlarmsFragment() {
        super();
    }

    @Override
    public void onResume(){
        super.onResume();
        update();
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
                startActivityForResult(intent, 0);
            }
        });
        datasource = new AlarmDataSource(this.getActivity().getApplicationContext());
        adapter = new AlarmListAdapter(this.getActivity(), R.layout.alarm_item_row, new ArrayList<Alarm>());
        ListView listView = (ListView) rootView.findViewById(R.id.alarm_list);
        listView.setAdapter(adapter);
        update();
        return rootView;
    }

    private void update() {
        try{
            datasource.open();
        } catch (SQLException e){
            Log.d(TAG, e.getMessage());
        }
        ArrayList<Alarm> alarms = datasource.getAlarms();
        adapter.clear();
        for (Alarm alarm : alarms){
            adapter.add(alarm);
        }
        datasource.close();
        adapter.notifyDataSetChanged();
    }
}
