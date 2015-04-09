package org.qing.golibrary.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import org.qing.golibrary.app.database.Alarm;
import org.qing.golibrary.app.database.DayInWeek;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class CreateAlarmActivity extends ActionBarActivity {
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

    }

    private void initTextFields() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/DD/yyyy", Locale.US);
        txtTime.setText(alarm.getHour() + ":" + alarm.getMintue());
        txtRepeat.setText(getRepeatString());
        txtEndDate.setText(dateFormat.format(alarm.getEndDate()));
    }

    private String getRepeatString() {
        String retStr = "";
        for (DayInWeek day : DayInWeek.values()){
            if (alarm.isDayRepeat(day)){
                retStr += day.toString() + ". ";
            }
        }
        if (retStr.equals("")){
            retStr = "No repeats";
        }
        return retStr;
    }

    private void initDefaultAlarm() {
        Calendar c = Calendar.getInstance();
        alarm = new Alarm();
        alarm.setHour(c.get(Calendar.HOUR_OF_DAY));
        alarm.setMintue(c.get(Calendar.MINUTE));
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
