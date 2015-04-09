package org.qing.golibrary.app;

import android.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import org.qing.golibrary.app.database.Alarm;
import org.qing.golibrary.app.database.AlarmDataSource;
import org.qing.golibrary.app.database.DayInWeek;
import org.qing.golibrary.app.fragments.DatePickerFragment;
import org.qing.golibrary.app.fragments.RepeatPickerFragment;
import org.qing.golibrary.app.fragments.TimePickerFragment;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class CreateAlarmActivity extends ActionBarActivity implements
        TimePickerFragment.OnTimePickedListener,
        DatePickerFragment.OnDatePickedListener ,
        RepeatPickerFragment.OnRepeatPickedListener{
    private static final String TAG = "CreateAlarm";
    private Alarm alarm;
    TextView txtTime;
    TextView txtRepeat;
    TextView txtEndDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alarm);

        txtTime = (TextView) findViewById(R.id.time);
        txtRepeat = (TextView) findViewById(R.id.repeat);
        txtEndDate = (TextView) findViewById(R.id.endDate);
        initDefaultAlarm();
        initTextFields();
        boundLabelActions();
    }

    private void boundLabelActions() {
        TextView timeLabel = (TextView) findViewById(R.id.timeLabel);
        timeLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });

        TextView repeatLabel = (TextView) findViewById(R.id.repeatLabel);
        repeatLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new RepeatPickerFragment();
                newFragment.show(getFragmentManager(), "repeatPicker");
            }
        });

        TextView endDateLabel = (TextView) findViewById(R.id.endDateLabel);
        endDateLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

    }

    private void initTextFields() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        txtTime.setText(String.format("%02d:%02d",alarm.getHour() ,alarm.getMinute()));
        txtRepeat.setText(alarm.getRepeatString());
        txtEndDate.setText(dateFormat.format(alarm.getEndDate()));
    }


    private void initDefaultAlarm() {
        Calendar c = Calendar.getInstance();
        alarm = new Alarm();
        alarm.setHour(c.get(Calendar.HOUR_OF_DAY));
        alarm.setMinute(c.get(Calendar.MINUTE));
        alarm.setEndDate(c.getTime());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_create_alarm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_accept:
                EditText editText = (EditText) findViewById(R.id.description);
                alarm.setDescription(editText.getText().toString());
                saveAlarm();
                finish();
                break;
            case R.id.action_cancel:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAlarm() {
        AlarmDataSource alarmDataSource = new AlarmDataSource(CreateAlarmActivity.this);
        try{
            alarmDataSource.open();
        } catch (SQLException e){
            Log.d(TAG, e.getMessage());
        }
        alarmDataSource.createAlarm(alarm);
        alarmDataSource.close();
    }

    @Override
    public void onTimePicked(int hour, int minute) {
        alarm.setHour(hour);
        alarm.setMinute(minute);
        initTextFields();
    }


    @Override
    public void onDatePicked(int year, int month, int date) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, date);
        alarm.setEndDate(c.getTime());
        initTextFields();
    }

    @Override
    public void onRepeatPicked(HashMap<DayInWeek, Boolean> repeat) {
        for (DayInWeek day : DayInWeek.values()){
            alarm.setDayRepeat(day, repeat.get(day));
        }
        initTextFields();
    }
}

