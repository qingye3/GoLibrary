package org.qing.golibrary.app.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Date picker
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    /**
     * Modified from the example on the google developer website
     * @param savedInstanceState savedInstanceState
     * @return the dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        mListener.onDatePicked(year, month, day);
    }

    /**
     * Set the listener to the calling activity
     * @param activity the activity to attach to
     */
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mListener = (OnDatePickedListener) activity;
    }

    private OnDatePickedListener mListener;

    /**
     * Task to do after picking the date
     */
    public interface OnDatePickedListener{
        void onDatePicked(int year, int month, int date);
    }
}
