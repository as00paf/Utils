package com.pafoid.utils.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.pafoid.utils.R;
import com.pafoid.utils.transform.GradientOverlayTransform;
import com.pafoid.utils.transform.PaletteGeneratorTransform;
import com.pafoid.utils.utils.DeviceUtils;
import com.pafoid.utils.utils.ToolbarUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Abstract Activity that contains a collapsible toolbar with an image at the top, a tab bar at the
 * bottom that controls the ViewPager.
 *
 * Extend this Activity and use it in your project paired with {@link com.pafoid.utils.adapters.DefaultStatePagerAdapter}
 */
public abstract class CollapsingCoverPagerActivity extends AppCompatActivity {
    protected static final String TAG = "ClpsgCoverPgrAct";

    //Views
    protected CollapsingToolbarLayout collapsingToolbar;
    protected Toolbar toolbar;
    protected TabLayout tabs;
    protected ViewPager viewPager;
    protected ImageView coverImageView;

    //Objects
    protected Handler handler = new Handler();
    protected Bitmap bitmap;
    protected int mutedColor;
    protected int mutedDarkColor;
    protected PagerAdapter pagerAdapter;

    //Options
    protected int offscreenPageLimit = 2;
    protected boolean animate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(animate) ActivityCompat.postponeEnterTransition(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collapsing_cover_pager);
        init(savedInstanceState);
    }

    //Init
    protected void init(Bundle savedInstanceState) {
        //Toolbar
        initToolbar();

        //Cover
        initCover(savedInstanceState);
    }

    protected void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitleEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && animate) {
            ToolbarUtils.setToolbarTitleTransitionName(toolbar, "title_text_" + getTitleTransitionName());
        }
    }

    protected void initCover(final Bundle savedInstanceState) {
        coverImageView = (ImageView) findViewById(R.id.cover_image);
        String coverImageURL = getCoverImageURL();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String transitionName = "cover_image_" + getCoverImageTransitionName();
            coverImageView.setTransitionName(transitionName);
        }

        PaletteGeneratorTransform paletteTransform = PaletteGeneratorTransform.instance();
        Picasso.with(this)
                .load(coverImageURL)
                .transform(new GradientOverlayTransform(this))
                .transform(paletteTransform)
                .resize(0, 300)
                .into(coverImageView, new Callback.EmptyCallback() {
                    @Override
                    public void onSuccess() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onCoverLoaded(savedInstanceState);
                            }
                        });
                    }
                });
    }

    private void onCoverLoaded(Bundle savedInstanceState) {
        if(animate)  ActivityCompat.startPostponedEnterTransition(CollapsingCoverPagerActivity.this);
        bitmap = ((BitmapDrawable) coverImageView.getDrawable()).getBitmap();

        calculateMutedColors();
        collapsingToolbar.setContentScrimColor(mutedColor);
        collapsingToolbar.setStatusBarScrimColor(mutedDarkColor);

        if (savedInstanceState == null) {
            if(!DeviceUtils.isScreenLarge(getResources())) {
                pagerAdapter = getPagerAdapter();
                viewPager = (ViewPager) findViewById(R.id.viewpager);
                viewPager.setOffscreenPageLimit(offscreenPageLimit);
                viewPager.setAdapter(pagerAdapter);

                //Animate ListView
                if(animate) viewPager.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_up));

                initTabLayout();
            }else{
                loadData();
            }
        }
    }

    protected void initTabLayout() {
        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setTabMode(TabLayout.MODE_FIXED);
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        tabs.setupWithViewPager(viewPager);

        tabs.requestFocus();
    }

    //TODO : move to utils
    private void calculateMutedColors() {
        Palette palette = PaletteGeneratorTransform.getPalette(bitmap);
        if(DeviceUtils.isScreenLarge(getResources())) {
            mutedColor = ContextCompat.getColor(CollapsingCoverPagerActivity.this, R.color.colorPrimary);
        }else{
            mutedColor = palette.getMutedColor(ContextCompat.getColor(CollapsingCoverPagerActivity.this, R.color.colorPrimary));
        }

        mutedDarkColor = palette.getDarkMutedColor(ContextCompat.getColor(CollapsingCoverPagerActivity.this, R.color.colorPrimaryDark));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && animate) {
                ActivityCompat.finishAfterTransition(CollapsingCoverPagerActivity.this);
            } else {
                finish();
            }
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && animate) {
            //Animate
            if(viewPager !=null){
                viewPager.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_down));
            }

            ActivityCompat.finishAfterTransition(this);
        }else{
            finish();
        }
    }

    //Abstract Methods
    protected abstract String getTitleTransitionName();

    protected abstract String getCoverImageTransitionName();

    protected abstract String getCoverImageURL();

    protected abstract void loadData();

    protected abstract PagerAdapter getPagerAdapter();
}
