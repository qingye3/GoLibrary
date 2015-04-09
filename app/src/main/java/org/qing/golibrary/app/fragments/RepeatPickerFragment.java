package org.qing.golibrary.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import org.qing.golibrary.app.R;
import org.qing.golibrary.app.database.DayInWeek;

import java.util.HashMap;

public class RepeatPickerFragment extends DialogFragment {

    /**
     * creating the dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_repeat_picker, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HashMap<DayInWeek, Boolean> repeat = getRepeat(view);
                mListener.onRepeatPicked(repeat);
            }
        }).setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

    /**
     * Get the repetition hashmap as in the Alarm model
     * @param view the view containing the checkboxes
     * @return the HashMap
     */
    private HashMap<DayInWeek,Boolean> getRepeat(View view) {
        HashMap<DayInWeek, Boolean> repeat = new HashMap<DayInWeek, Boolean>();
        CheckBox sunday = (CheckBox) view.findViewById(R.id.sunday);
        CheckBox monday = (CheckBox) view.findViewById(R.id.monday);
        CheckBox tuesday = (CheckBox) view.findViewById(R.id.tuesday);
        CheckBox wednesday = (CheckBox) view.findViewById(R.id.wednesday);
        CheckBox thursday = (CheckBox) view.findViewById(R.id.thursday);
        CheckBox friday = (CheckBox) view.findViewById(R.id.friday);
        CheckBox saturday = (CheckBox) view.findViewById(R.id.saturday);
        repeat.put(DayInWeek.SUNDAY, sunday.isChecked());
        repeat.put(DayInWeek.MONDAY, monday.isChecked());
        repeat.put(DayInWeek.TUESDAY, tuesday.isChecked());
        repeat.put(DayInWeek.WEDNESDAY, wednesday.isChecked());
        repeat.put(DayInWeek.THURSDAY, thursday.isChecked());
        repeat.put(DayInWeek.FRIDAY, friday.isChecked());
        repeat.put(DayInWeek.SATURDAY, saturday.isChecked());
        return repeat;
    }

    /**
     * On attach set mListener to the calling activity
     */
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mListener = (OnRepeatPickedListener) activity;
    }

    OnRepeatPickedListener mListener;

    /**
     * Task to do after picking the date
     */
    public interface OnRepeatPickedListener{
        void onRepeatPicked(HashMap<DayInWeek, Boolean> repeat);
    }

}
