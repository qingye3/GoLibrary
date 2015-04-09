package org.qing.golibrary.app.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import org.qing.golibrary.app.CreateAlarmActivity;
import org.qing.golibrary.app.R;
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

        //creating the adapter and attach the listView to it
        adapter = new AlarmListAdapter(this.getActivity(), R.layout.alarm_item_row, new ArrayList<Alarm>());
        ListView listView = (ListView) rootView.findViewById(R.id.alarm_list);
        listView.setAdapter(adapter);
        update();
        return rootView;
    }

    /**
     * updates the adapter using the datasource. Also modify the view.
     */
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

    /**
     * remove an alarm from the database
     * @param id
     */
    public void removeAlarm(int id){
        try{
            datasource.open();
        } catch (SQLException e){
            Log.d(TAG, e.getMessage());
        }
        datasource.removeAlarm(id);
        update();
    }

    /**
     * The ListAdapter. By convention, adapters are inner classes.
     * This is not a static class because we need to call the removeAlarm function
     */
    public class AlarmListAdapter extends ArrayAdapter<Alarm> {
        Context context;
        int layoutResourceId;

        public AlarmListAdapter(Context context, int layoutResourceId, ArrayList<Alarm> alarms){
            super(context, layoutResourceId, alarms);
            this.context = context;
            this.layoutResourceId = layoutResourceId;
        }


        /**
         * Returns a the view representing the row inside the listView
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            AlarmHolder holder = null;

            if(row == null)
            {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new AlarmHolder();
                holder.txtDays = (TextView)row.findViewById(R.id.days_text);
                holder.txtTime = (TextView)row.findViewById(R.id.time_text);
                holder.btnRemove = (ImageButton) row.findViewById(R.id.remove_button);
                row.setTag(holder);
            }
            else
            {
                holder = (AlarmHolder)row.getTag();
            }

            final Alarm alarm = getItem(position);
            holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeAlarm(alarm.getId());
                }
            });
            holder.txtTime.setText(String.format("%02d:%02d", alarm.getHour(), alarm.getMinute()));
            holder.txtDays.setText(alarm.getRepeatString());
            return row;
        }

        class AlarmHolder{
            TextView txtTime;
            TextView txtDays;
            ImageButton btnRemove;
        }

    }
}
