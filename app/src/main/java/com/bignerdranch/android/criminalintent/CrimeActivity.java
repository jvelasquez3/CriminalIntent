package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {
    private static final String EXTRA_CRIMEID = "com.bignerdranch.android.geoquiz.crimeid";

    @Override
    protected Fragment createFragment(){
        UUID crimeId = (UUID)getIntent().getSerializableExtra(EXTRA_CRIMEID);
        return CrimeFragment.newInstance(crimeId);
    }

    public static Intent newIntent(Context packageContext, UUID crimeId){
        Intent i = new Intent(packageContext, CrimeActivity.class);
        i.putExtra(EXTRA_CRIMEID, crimeId);
        return i;
    }
}
