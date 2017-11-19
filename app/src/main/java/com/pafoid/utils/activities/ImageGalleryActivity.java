package com.pafoid.utils.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.pafoid.utils.R;
import com.pafoid.utils.adapters.ImageGalleryPagerAdapter;
import com.pafoid.utils.fragments.ImageGalleryFragment;
import com.pafoid.utils.utils.AppConstants;
import com.pafoid.utils.utils.DeviceUtils;

import java.util.ArrayList;

/**
 * Activity used to display images in a ViewPager in {@link ImageGalleryFragment} with {@link com.pafoid.utils.views.GestureImageView}
 *
 * Use this Activity as an Image Gallery for your application
 * The Activity takes bundle arguments to display images and display an image on start at a specified position
 * The images must be an ArrayList of {@link ImageGalleryFragment.IImageItem} and use the bundle argument key from {@link AppConstants} called IMAGES
 * The position must be an integer and use the bundle argument key from {@link AppConstants} called POSITION
 */
public class ImageGalleryActivity extends AppCompatActivity implements ImageGalleryPagerAdapter.Listener {
    private static final String TAG = "ImageGalleryActivity";

    private ArrayList<ImageGalleryFragment.IImageItem> images;

    //Views
    private ViewPager mViewPager;
    private AppCompatTextView mTextTextView, mReferenceTextView;

    //Objects
    private ImageGalleryPagerAdapter adapter;

    //Data
    private Boolean isUiShown = true;
    private Boolean isPortrait = true;
    private int loadCount = 0;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            ActivityCompat.postponeEnterTransition(this);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);

        mViewPager              = (ViewPager)findViewById(R.id.activity_image_gallery_pager);
        mTextTextView           = (AppCompatTextView)findViewById(R.id.activity_image_gallery_textTextView);
        mReferenceTextView      = (AppCompatTextView)findViewById(R.id.activity_image_gallery_referenceTextView);

        Intent intent = getIntent();
        int position = intent.getIntExtra(AppConstants.POSITION, 0);
        images = intent.getParcelableArrayListExtra(AppConstants.IMAGES);

        adapter = new ImageGalleryPagerAdapter(getSupportFragmentManager(), images, this);
        mViewPager.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mViewPager.removeOnLayoutChangeListener(this);
            }
        });
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(position);

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                ImageGalleryFragment.IImageItem image = images.get(position);
                mTextTextView.setText(image.getText());
                mReferenceTextView.setText(image.getReference());

                getSupportActionBar().setTitle(image.getTitle());
            }
        });

        if(images != null ) mViewPager.setOffscreenPageLimit(images.size());

        ImageGalleryFragment.IImageItem image = images.get(position);
        mTextTextView.setText(image.getText());
        mReferenceTextView.setText(image.getReference());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(image.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setShowHideAnimationEnabled(true);

        if(DeviceUtils.isScreenLarge(getResources())){
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.black_transparent)));
            isPortrait = false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityCompat.finishAfterTransition(this);
                }else{
                    finish();
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            if(!isPortrait && !DeviceUtils.isScreenLarge(getResources())){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }else{
                ActivityCompat.finishAfterTransition(this);
            }
        }else{
            finish();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            if(!DeviceUtils.isScreenLarge(getResources())){
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, android.R.color.black)));
            }
            isPortrait = true;
        }else{
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.black_transparent)));
            isPortrait = false;
        }
        mViewPager.invalidate();
    }

    @Override
    public void toggleUIVisibility() {
        isUiShown = !isUiShown;

        LinearLayout textContainer = (LinearLayout) findViewById(R.id.textContent);

        if(isUiShown){
            getSupportActionBar().show();
            textContainer.setVisibility(View.VISIBLE);
        }else{
            getSupportActionBar().hide();
            textContainer.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onImageLoaded() {
        loadCount++;
        Log.d(TAG, "Loaded " + loadCount + "/" + images.size() + " images");

        if(loadCount >= images.size()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ActivityCompat.startPostponedEnterTransition(ImageGalleryActivity.this);
                    }
                }, 250);
            }
        }
    }
}
