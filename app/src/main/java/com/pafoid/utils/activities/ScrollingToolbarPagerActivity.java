package com.pafoid.utils.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.pafoid.utils.R;
import com.pafoid.utils.adapters.TabbedPagerAdapter;
import com.pafoid.utils.utils.DeviceUtils;

/**
 * Abstract Activity that contains a collapsible toolbar with a tab bar at the bottom that controls
 * the ViewPager.
 *
 * Extend this Activity and use it in your project paired with {@link com.pafoid.utils.adapters.DefaultStatePagerAdapter}
 */
public abstract class ScrollingToolbarPagerActivity extends AppCompatActivity {
    protected static final String TAG = "ClpsgTbarPgrAct";

    //Views
    protected Toolbar toolbar;
    protected TabLayout tabs;
    protected ViewPager viewPager;

    //Objects
    protected TabbedPagerAdapter pagerAdapter;

    //Options
    protected int offscreenPageLimit = 2;
    @DrawableRes
    protected int logoRes = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_toolbar_pager);
        init(savedInstanceState);
    }

    //Init
    protected void init(Bundle savedInstanceState) {
        //Toolbar
        initToolbar();

        //ViewPager
        initViewPager();
    }

    protected void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    private void initViewPager() {
        pagerAdapter = getPagerAdapter();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(offscreenPageLimit);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setTitle(pagerAdapter.getPageTitle(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        getSupportActionBar().setTitle(pagerAdapter.getPageTitle(0));

        initTabLayout();

        if(DeviceUtils.isScreenLarge(getResources())) {
            CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitleEnabled(false);
            collapsingToolbar.setContentScrimColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        if(logoRes != -1) toolbar.setLogo(logoRes);
    }

    protected void initTabLayout() {
        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setTabMode(TabLayout.MODE_FIXED);
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        tabs.setupWithViewPager(viewPager);
        for (int i = 0; i < tabs.getTabCount(); i++) {
            TabLayout.Tab tab = tabs.getTabAt(i);
            tab.setCustomView(pagerAdapter.getTabView(i));
        }
        tabs.requestFocus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityCompat.finishAfterTransition(ScrollingToolbarPagerActivity.this);
            } else {
                finish();
            }
            return true;
        }

        return false;
    }


    //Abstract Methods
    protected abstract TabbedPagerAdapter getPagerAdapter();

}
