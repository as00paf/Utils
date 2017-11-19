package com.pafoid.utils.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pafoid.utils.fragments.OnBoardFragment;
import com.pafoid.utils.permissions.PermissionManager;

public class OnBoardPagerAdapter extends FragmentPagerAdapter {
    public static final int INTRO = 0;
    public static final int GPS = 1;
    public static final int STORAGE = 2;

    private PermissionManager.PermissionRequestListener listener;

    public OnBoardPagerAdapter(FragmentManager fm, PermissionManager.PermissionRequestListener listener) {
        super(fm);

        this.listener = listener;
    }

    @Override
    public Fragment getItem(int i) {
        OnBoardFragment fragment = OnBoardFragment.newInstance(i);
        fragment.setListener(listener);

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
