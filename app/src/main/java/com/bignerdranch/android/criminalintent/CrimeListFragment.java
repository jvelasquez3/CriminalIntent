package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

/**
 * Created by Jorge on 13/01/17.
 */

public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private static final int REQUEST_CRIME = 1;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView)v.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager((new LinearLayoutManager(getActivity())));

        updateUI();

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    private void updateUI(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if(mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_CRIME){
            if(data != null){
                UUID idCrimeChanged = CrimeFragment.crimeChanged(data);
                int positionChanged = mAdapter.getPosition(idCrimeChanged);
                if(positionChanged != -1) {
                    mAdapter.notifyItemChanged(positionChanged);
                }
            }
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);

            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position){
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }

        @Override
        public int getItemCount(){
            return mCrimes.size();
        }

        public int getPosition(UUID id){
            for(int i = 0; i < mCrimes.size(); i++){
                if(mCrimes.get(i).getId().equals(id)){
                    return i;
                }
            }
            return -1;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch ((item.getItemId())){
            case R.id.menu_item_new_crime:
                Crime crime = CrimeLab.get(getActivity()).addCrime();
                Intent intent = CrimePagerActivity.newIntent(getActivity(),crime.getId());
                startActivityForResult(intent, REQUEST_CRIME);
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_item_show_subtitle:
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle(){
        int crimeCount = CrimeLab.get(getActivity()).getCrimes().size();
        String subtitle = getString(R.string.subtitle_format,crimeCount);

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Crime mCrime;
        private TextView mCrimeTitleTextView;
        private TextView mCrimeDateTextView;
        private CheckBox mCrimeSolvedCheckBox;

        public CrimeHolder(View itemView){
            super(itemView);

            itemView.setOnClickListener(this);

            mCrimeTitleTextView = (TextView)itemView.findViewById(R.id.crime_title_textview);
            mCrimeDateTextView = (TextView)itemView.findViewById(R.id.crime_date_textview);
            mCrimeSolvedCheckBox = (CheckBox)itemView.findViewById(R.id.crime_solved_checkbox);
        }

        public void bindCrime(Crime crime) {
            mCrime = crime;
            mCrimeTitleTextView.setText(mCrime.getTitle());

            DateFormat df = new DateFormat();
            CharSequence formatedDate = df.format("dd/MM/yyyy hh:mm a", crime.getDate());
            mCrimeDateTextView.setText(formatedDate);

            mCrimeSolvedCheckBox.setChecked(mCrime.isSolved());
        }

        @Override
        public void onClick(View v){
            Intent i = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivityForResult(i, REQUEST_CRIME);
        }
    }
}
