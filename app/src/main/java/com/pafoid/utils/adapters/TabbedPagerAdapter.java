package com.pafoid.utils.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by as00p on 2017-07-27.
 */

public abstract class TabbedPagerAdapter extends FragmentStatePagerAdapter implements ITabbedPagerAdapter{
    public TabbedPagerAdapter(FragmentManager fm) {
        super(fm);
    }
}
