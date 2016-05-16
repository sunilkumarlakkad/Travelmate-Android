package com.example.sunilkumarlakkad.travelmate.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.sunilkumarlakkad.travelmate.Activity.TaxiServiceInfoActivity;
import com.example.sunilkumarlakkad.travelmate.Fragment.TaxiServiceFragment;

import java.util.Locale;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {


    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return TaxiServiceFragment.newInstance(TaxiServiceInfoActivity.businessList.get(position));
    }

    @Override
    public int getCount() {
        return TaxiServiceInfoActivity.businessList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();

        String name = TaxiServiceInfoActivity.businessList.get(position).name();
        return name.toUpperCase(l);
    }
}
