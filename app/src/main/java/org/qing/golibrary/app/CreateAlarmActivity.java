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

    /**
     * Set the OnClickListeners of the labels. Bounding the actions to the labels because the labels
     * are not in a listView.
     */
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

    /**
     * Updating the text fields using the alarm
     */
    private void initTextFields() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        txtTime.setText(String.format("%02d:%02d", alarm.getHour(), alarm.getMinute()));
        txtRepeat.setText(alarm.getRepeatString());
        txtEndDate.setText(dateFormat.format(alarm.getEndDate()));
    }


    /**
     * Creating a default alarm. The time is default to the current time.
     */
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
        int id = item.getItemId();
        switch (id){
            case R.id.action_accept:
                setAlarmDescription();
                setAlarmStartDate();
                saveAlarm();
                scheduleAlarm();
                finish();
                break;
            case R.id.action_cancel:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAlarmDescription() {
        EditText editText = (EditText) findViewById(R.id.description);
        alarm.setDescription(editText.getText().toString());
    }

    /**
     * Setting start date using the alarm
     */
    private void setAlarmStartDate() {
        Calendar startDate = Calendar.getInstance();
        startDate.setTimeInMillis(System.currentTimeMillis());
        startDate.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        startDate.set(Calendar.MINUTE, alarm.getMinute());

        if (startDate.getTimeInMillis() < System.currentTimeMillis()){
            if (!alarm.isRepeat()){
                startDate.add(Calendar.DATE, 1);
            } else {
                for (int i = 0; i < 7 ; i++){
                    startDate.add(Calendar.DATE, 1);
                    if (alarm.isDayRepeat(calendarDayToMyDay(startDate.get(Calendar.DAY_OF_WEEK)))){
                        break;
                    }
                }
            }
        }
        alarm.setStartDate(startDate.getTime());
    }

    private DayInWeek calendarDayToMyDay(int calendarDay){
        switch (calendarDay){
            case Calendar.MONDAY:
                return DayInWeek.MONDAY;
            case Calendar.TUESDAY:
                return DayInWeek.TUESDAY;
            case Calendar.WEDNESDAY:
                return DayInWeek.WEDNESDAY;
            case Calendar.THURSDAY:
                return DayInWeek.THURSDAY;
            case Calendar.FRIDAY:
                return DayInWeek.FRIDAY;
            case Calendar.SATURDAY:
                return DayInWeek.SATURDAY;
            case Calendar.SUNDAY:
                return DayInWeek.SUNDAY;
        }
        return null;
    }

    private void scheduleAlarm() {
        AlarmReceiver.setAlarm(this, alarm);
    }

    /**
     * Save the alarm to the database
     */
    private void saveAlarm() {
        AlarmDataSource alarmDataSource = new AlarmDataSource(CreateAlarmActivity.this);
        try{
            alarmDataSource.open();
        } catch (SQLException e){
            Log.d(TAG, e.getMessage());
        }
        int id = alarmDataSource.createAlarm(alarm);
        alarm.setId(id);
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

