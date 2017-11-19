package com.pafoid.utils.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pafoid.utils.fragments.ImageGalleryFragment;

import java.util.ArrayList;

public class ImageGalleryPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<ImageGalleryFragment.IImageItem> images;
    private Listener listener;

    public ImageGalleryPagerAdapter(FragmentManager fm, ArrayList<ImageGalleryFragment.IImageItem> images, Listener listener)
    {
        super(fm);

        this.images = images;
        this.listener = listener;
    }

    @Override
    public int getCount()
    {
        return this.images.size();
    }

    @Override
    public Fragment getItem(int position) {
        ImageGalleryFragment fragment = ImageGalleryFragment.newInstance(this.images.get(position));
        fragment.setListener(listener);
        return fragment;
    }

    public interface Listener{
        void toggleUIVisibility();
        void onImageLoaded();
    }
}
