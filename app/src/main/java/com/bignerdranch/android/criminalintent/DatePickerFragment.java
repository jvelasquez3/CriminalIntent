package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Jorge on 27/01/17.
 */

public class DatePickerFragment extends DialogFragment {
    private static final String ARG_DATE = "date";//argumento recibido con la fecha a setear en el dp
    public static final String EXTRA_DATE = "com.bignerdranch.android,criminalintent.date";//extra enviado para saber la fecha elegida


    private DatePicker mDatePicker;

    public static DatePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Date date = new Date();

        if(getArguments() != null) {//Si hay argumentos se usan si no se inicializa la fecha como nueva
            date = (Date) getArguments().getSerializable(ARG_DATE);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        mDatePicker = (DatePicker)v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year,month,day,null);


        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Date of crime:")
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                int year = mDatePicker.getYear();
                                int month = mDatePicker.getMonth();
                                int day = mDatePicker.getDayOfMonth();
                                Date date = new GregorianCalendar(year, month, day).getTime();
                                sendResult(Activity.RESULT_OK, date);
                            }
                        }
                )
                .create();
    }

    //Metodo que setea el extra con la fecha seleccionada para ser enviada al fragment target
    public void sendResult(int resultCode, Date date){
        if(getTargetFragment() == null){
            return;
        }

        Intent i = new Intent();
        i.putExtra(EXTRA_DATE, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,i);
    }

    //Extrae y devuelve la fecha que se encuentra en el intent enviado al fragment target
    public static Date getDatePicked(Intent data){
        return (Date)data.getSerializableExtra(EXTRA_DATE);
    }
}
