package org.qing.golibrary.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import org.qing.golibrary.app.R;
import org.qing.golibrary.app.database.Alarm;

import java.util.ArrayList;

/**
 * Created by Qing on 4/9/2015.
 */
public class AlarmListAdapter extends ArrayAdapter<Alarm>{
    Context context;
    int layoutResourceId;

    public AlarmListAdapter(Context context, int layoutResourceId, ArrayList<Alarm> alarms){
        super(context, layoutResourceId, alarms);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
    }


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

        Alarm alarm = getItem(position);
        holder.txtTime.setText(String.format("%02d:%02d", alarm.getHour(), alarm.getMinute()));
        holder.txtDays.setText(alarm.getRepeatString());
        return row;
    }

    static class AlarmHolder{
        TextView txtTime;
        TextView txtDays;
        ImageButton btnRemove;
    }


}
