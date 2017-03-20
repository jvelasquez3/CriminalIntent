package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Jorge on 10/01/17.
 */

public class CrimeFragment extends Fragment {
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCeckbox;
    private static final String ARG_CRIMEID = "crimeid";//argumento recibido para obtener el click al crimen
    private static final String EXTRA_POSITION_CHANGED = "com.bignerdranch.android.geoquiz.position_changed";//extra enviado para saber si cambio una posicion
    private static final String DIALOG_DATE = "DialogDate";//constante que identificara al dialogo de fecha
    private static final int REQUEST_DATE = 0;//Constante que identifica a la fecha que espero del dialogo
    private static final String DIALOG_TIME = "DialogTime";//constante que identificara al dialogo de fecha
    private static final int REQUEST_TIME = 1;//Constante que identifica a la fecha que espero del dialogo

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIMEID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        UUID crimeId;

        if(getArguments() != null) {//Si hay argumentos se usan si no se inicializa el crimen como nuevo
            crimeId = (UUID)getArguments().getSerializable(ARG_CRIMEID);
            mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());

        mTitleField.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){
                mCrime.setTitle(s.toString());
                putExtraPositionChanged();
            }

            @Override
            public void afterTextChanged(Editable s){

            }
        });

        mDateButton = (Button)v.findViewById(R.id.crime_date);
        mTimeButton = (Button)v.findViewById(R.id.crime_time);
        updateDate(mCrime.getDate());
        updateTime(mCrime.getDate());

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se crea una nueva instancia del DatePickerFragment con la fecha del crimen como argumento
                //se setea este fragment como el target del nuevo, enviando un codigo que identificara lo que este regrese
                //y se le envía el fragment manager y una constante que identificara al widget para poder ser mostrado
                FragmentManager fm = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se crea una nueva instancia del TimePickerFragment con la hora del crimen como argumento
                //se setea este fragment como el target del nuevo, enviando un codigo que identificara lo que este regrese
                //y se le envía el fragment manager y una constante que identificara al widget para poder ser mostrado
                FragmentManager fm = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_TIME);
                dialog.show(fm, DIALOG_TIME);
            }
        });

        mSolvedCeckbox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCeckbox.setChecked(mCrime.isSolved());
        mSolvedCeckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                putExtraPositionChanged();
            }
        });

        return v;
    }

    //Metodo que se utiliza para esperar el request del datePicker y obtener la fecha que viene en data
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_DATE){
            Date date = DatePickerFragment.getDatePicked(data);

            Calendar calendarOld = Calendar.getInstance();
            calendarOld.setTime(mCrime.getDate());
            int hours = calendarOld.get(Calendar.HOUR_OF_DAY);
            int minutes = calendarOld.get(Calendar.MINUTE);

            Calendar calendarNew = Calendar.getInstance();
            calendarNew.setTime(date);
            calendarNew.set(Calendar.HOUR_OF_DAY, hours);
            calendarNew.set(Calendar.MINUTE, minutes);

            date = calendarNew.getTime();

            mCrime.setDate(date);

            updateDate(date);
            putExtraPositionChanged();
        }else if(requestCode == REQUEST_TIME){
            Date date = TimePickerFragment.getTimePicked(data);

            Calendar calendarOld = Calendar.getInstance();
            calendarOld.setTime(mCrime.getDate());
            int year = calendarOld.get(Calendar.YEAR);
            int month = calendarOld.get(Calendar.MONTH);
            int day = calendarOld.get(Calendar.DAY_OF_MONTH);

            Calendar calendarNew = Calendar.getInstance();
            calendarNew.setTime(date);
            calendarNew.set(year,month,day);

            date = calendarNew.getTime();

            mCrime.setDate(date);

            updateDate(date);
            updateTime(date);
            putExtraPositionChanged();
        }
    }

    private void updateDate(Date date) {
        DateFormat df = new DateFormat();
        CharSequence formatedDate = df.format("dd/MM/yyyy", date);
        mDateButton.setText(formatedDate);
    }

    //Metodo que setea el extra de la posicion cuando cambia algun dato del crimen
    private void putExtraPositionChanged() {
        Intent i = new Intent();
        i.putExtra(EXTRA_POSITION_CHANGED, mCrime.getId());
        getActivity().setResult(RESULT_OK, i);
    }

    private void updateTime(Date date) {
        DateFormat df = new DateFormat();
        CharSequence formatedDate = df.format("hh:mm a", date);
        mTimeButton.setText(formatedDate);
    }

    //Metodo que extrae el ID de los extras de un intent para ver si este ha cambiado
    public static UUID crimeChanged(Intent i){
        return (UUID)i.getSerializableExtra(EXTRA_POSITION_CHANGED);
    }
}
