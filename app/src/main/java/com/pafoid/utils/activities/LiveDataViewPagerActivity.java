package com.pafoid.utils.activities;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pafoid.utils.R;

/**
 * Abstract Activity class used to display a RecyclerView
 * This Activity also uses the new Android Architecture component LiveData to populate its views
 */
public abstract class LiveDataViewPagerActivity<T> extends LifecycleActivity implements AppCompatCallback, View.OnClickListener {
    private static final String TAG = "LiveDataViewPagerActivity";

    //Views
    protected ViewPager viewPager;
    protected Toolbar toolbar;
    protected FloatingActionButton fab;

    //Objects
    protected AppCompatDelegate compatDelegate;
    protected PagerAdapter pagerAdapter;

    //Options
    protected int offscreenPageLimit = 2;
    protected boolean animate = true;
    protected boolean useFab = true;
    protected boolean useToolbar = true;

    //Data
    protected LiveData<T> listDataListener;
    protected T data;

    public LiveDataViewPagerActivity() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        compatDelegate = AppCompatDelegate.create(this, this);
        compatDelegate.onCreate(savedInstanceState);
        compatDelegate.setContentView(getLayoutResId());

        if(useToolbar){
            initToolbar();
        }

        listDataListener = getDataListener();
        listDataListener.observe(this, dataObserver);

        //ViewPager
        pagerAdapter = getPagerAdapter();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(offscreenPageLimit);
        viewPager.setAdapter(pagerAdapter);

        //Fab
        fab = findViewById(R.id.fab);
        initFab();
    }

    protected void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        compatDelegate.setSupportActionBar(toolbar);
    }

    protected int getLayoutResId() {
        return R.layout.activity_fab_viewpager;
    }

    protected void initFab() {
        if(useFab){
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(this);
        }else{
            fab.setVisibility(View.GONE);
        }
    }

    protected Observer<T> dataObserver = new Observer<T>() {
        @Override
        public void onChanged(@Nullable T list) {
            data = list;
            pagerAdapter = getPagerAdapter();
            viewPager.setAdapter(pagerAdapter);
        }
    };

    //Getters/Setters
    public boolean isAnimated() {
        return animate;
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public boolean isUsingFab() {
        return useFab;
    }

    public void setUseFab(boolean useFab) {
        this.useFab = useFab;
        initFab();
    }

    public boolean isUsingToolbar() {
        return useToolbar;
    }

    public void setUseToolbar(boolean useToolbar) {
        this.useToolbar = useToolbar;
        initToolbar();
    }

    //Listener
    @Override
    public void onClick(View view){
        throw new Error("You should override this method");
    }

    //Abstract Methods
    protected abstract LiveData<T> getDataListener();

    protected abstract PagerAdapter getPagerAdapter();
}
