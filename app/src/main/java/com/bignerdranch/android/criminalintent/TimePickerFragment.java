package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Jorge on 2/02/17.
 */

public class TimePickerFragment extends DialogFragment {
    private static final String ARG_TIME = "time";
    public static final String EXTRA_TIME = "com.bignerdranch.android,criminalintent.time";

    private TimePicker mTimePicker;

    public static TimePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = new Date();

        if (getArguments() != null) {
            date = (Date) getArguments().getSerializable(ARG_TIME);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);

        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_picker);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minute);
        } else {
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(minute);
        }

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Hour of crime:")
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                int hour;
                                int minute;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    hour = mTimePicker.getHour();
                                    minute = mTimePicker.getMinute();
                                }else{
                                    hour = mTimePicker.getCurrentHour();
                                    minute = mTimePicker.getCurrentMinute();
                                }

                                Date date = new GregorianCalendar(1,1,2000,hour,minute).getTime();
                                sendResult(Activity.RESULT_OK, date);
                            }
                        }
                )
                .create();
    }

    public void sendResult(int resultCode, Date date){
        if(getTargetFragment() == null){
            return;
        }

        Intent i = new Intent();
        i.putExtra(EXTRA_TIME, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,i);
    }

    public static Date getTimePicked(Intent data){
        return (Date)data.getSerializableExtra(EXTRA_TIME);
    }
}
