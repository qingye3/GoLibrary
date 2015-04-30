package org.qing.golibrary.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import org.qing.golibrary.app.AlarmReceiver;
import org.qing.golibrary.app.CreateAlarmActivity;
import org.qing.golibrary.app.PunisherService;
import org.qing.golibrary.app.R;
import org.qing.golibrary.app.database.Alarm;
import org.qing.golibrary.app.database.AlarmDataSource;

import java.sql.SQLException;
import java.util.ArrayList;

public class ViewAlarmsFragment extends Fragment {
    private static final String TAG = "AlarmFragment";
    private AlarmListAdapter adapter = null;
    private AlarmDataSource datasource;
    private TextView quotaText;
    private BroadcastReceiver receiver;

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

        quotaText = (TextView) rootView.findViewById(R.id.cancellation_text);
        update();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(PunisherService.PUNISHER_UPDATE)){
                    update();
                }
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver),
                new IntentFilter(PunisherService.PUNISHER_UPDATE));

        return rootView;
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        super.onStop();
    }

    /**
     * updates the adapter using the datasource. Update the quota text as well. Also modify the view.
     */
    private void update() {
        quotaText.setText("Quotas Left: " + getNumOfQuotas());
        updateAlarmList();
    }

    private int getNumOfQuotas() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        return sharedPref.getInt(getString(R.string.NUM_OF_QUOTAS), 1);
    }

    private void saveNumOfQuotas(int numOfQuotas) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.NUM_OF_QUOTAS), numOfQuotas);
        editor.apply();
    }

    private void updateAlarmList() {
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

    private void unscheduleAlarm(Alarm alarm) {
        AlarmReceiver alarmReceiver = new AlarmReceiver();
        alarmReceiver.cancelAlarm(getActivity().getApplicationContext(), alarm);
    }

    /**
     * remove an alarm from the database
     * @param id id of the alarm
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
                    showCancelDialog(alarm);
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

    private void showCancelDialog(final Alarm alarm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alarm Cancellation");
        if (canCancel()){
            builder.setMessage("Cancelling an alarm will cost one quota. Continue?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    unscheduleAlarm(alarm);
                    removeAlarm(alarm.getId());
                    saveNumOfQuotas(getNumOfQuotas() - 1);
                    update();
                }
            });
            builder.setNegativeButton("Cancel", null);
        } else {
            builder.setMessage("You don't have enough quota to cancel the alarm");
            builder.setPositiveButton("OK", null);
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean canCancel(){
        return getNumOfQuotas() > 0;
    }
}
