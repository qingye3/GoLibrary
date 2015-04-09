package org.qing.golibrary.app.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Modified from the google example
 * http://developer.android.com/guide/topics/ui/controls/pickers.html
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }


    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mListener.onTimePicked(hourOfDay, minute);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mListener = (OnTimePickedListener) activity;
    }

    private OnTimePickedListener mListener;

    public interface OnTimePickedListener {
        void onTimePicked(int hour, int minute);
    }
}
