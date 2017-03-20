package com.bignerdranch.android.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Jorge on 12/01/17.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;

    private CrimeLab(Context context){
        mCrimes = new ArrayList<>();
    }

    public Crime addCrime()
    {
        Crime c = new Crime("Crime " + mCrimes.size());
        mCrimes.add(c);
        return c;
    }

    public static CrimeLab get(Context context){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public List<Crime> getCrimes(){
        return mCrimes;
    }

    public Crime getCrime(UUID id){
        for(Crime crime : mCrimes){
            if(crime.getId().equals(id)){
                return crime;
            }
        }
        return null;
    }
}
