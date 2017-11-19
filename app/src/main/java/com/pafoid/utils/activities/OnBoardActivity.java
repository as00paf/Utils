package com.pafoid.utils.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.pafoid.utils.R;
import com.pafoid.utils.adapters.OnBoardPagerAdapter;
import com.pafoid.utils.permissions.PermissionManager;
import com.pafoid.utils.utils.AppConstants;
import com.pafoid.utils.utils.DeviceUtils;
import com.viewpagerindicator.CirclePageIndicator;


/**
 * Abstract Activity used to provide an on-boarding process and grant GPS and Storage permissions for the application
 *
 * Extend this Activity and add it to your manifest with the main intent filter so it shows on first startup
 * Override abstract methods to adapt to your specific needs
 */
public abstract class OnBoardActivity extends AppCompatActivity implements View.OnClickListener, PermissionManager.PermissionRequestListener {
    public final String TAG = "OnBoardActivity";

    private ViewPager viewPager;
    private CirclePageIndicator indicator;
    private Button skipButton, nextButton, prevButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Orientation
        if(DeviceUtils.isScreenLarge(getResources())) {// width > height, better to use Landscape
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            showHomeActivity();
            return;
        }

        SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstLaunch = preferences.getBoolean(AppConstants.FIRST_LAUNCH, true);

        if(isFirstLaunch) {//Show On-Boarding
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(AppConstants.FIRST_LAUNCH, false);
            editor.apply();

            setContentView(R.layout.activity_on_board);

            viewPager = (ViewPager) findViewById(R.id.viewpager);
            indicator = (CirclePageIndicator) findViewById(R.id.indicator);

            viewPager.setAdapter(new OnBoardPagerAdapter(getSupportFragmentManager(), this));

            //Bind the title indicator to the adapter
            indicator = (CirclePageIndicator)findViewById(R.id.indicator);
            indicator.setViewPager(viewPager);

            indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    prevButton.setVisibility(position > 0 ? View.VISIBLE : View.INVISIBLE);

                    nextButton.setText(position == 2 ? getString(R.string.finish) : getString(R.string.next));

                    skipButton.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
                }

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

            skipButton = (Button) findViewById(R.id.skip_button);
            skipButton.setOnClickListener(this);
            skipButton.setVisibility(View.INVISIBLE);

            nextButton = (Button) findViewById(R.id.next_button);
            nextButton.setOnClickListener(this);

            prevButton = (Button) findViewById(R.id.prev_button);
            prevButton.setOnClickListener(this);
            prevButton.setVisibility(View.INVISIBLE);

        }else{
            showHomeActivity();
        }
    }

    @Override
    public void onClick(View v) {
        if(v == skipButton){
           showPermissionWarning();
        }else if(v == nextButton){
            if(viewPager.getCurrentItem() < 2){
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }else{//Finish
                showPermissionWarning();
            }
        }else if(v == prevButton){
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void onPermissionAccepted(String permission) {
        switch (permission){
            case Manifest.permission.ACCESS_FINE_LOCATION:
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                break;
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                showHomeActivity();
                break;
        }
    }

    /**
     * Used to show your main Activity when on-boarding process is done
     */
    protected abstract void showHomeActivity();

    @Override
    public void onPermissionDenied(String permission) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        switch (permission){
            case Manifest.permission.ACCESS_FINE_LOCATION:
                alertDialogBuilder.setTitle(getString(R.string.location_permission_denied));
                alertDialogBuilder
                        .setMessage(getString(R.string.location_permission_denied_msg))
                        .setCancelable(true)
                        .setPositiveButton(getString(android.R.string.ok),new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,int id)
                            {
                                dialog.dismiss();
                                PermissionManager.getInstance(OnBoardActivity.this).requestPermission(OnBoardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                            }
                        })
                        .setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                            }
                        });
                break;
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                alertDialogBuilder.setTitle(getString(R.string.storage_permission_denied));
                alertDialogBuilder
                        .setMessage(getString(R.string.storage_permission_denied_msg))
                        .setCancelable(true)
                        .setPositiveButton(getString(android.R.string.ok),new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,int id)
                            {
                                dialog.dismiss();
                                PermissionManager.getInstance(OnBoardActivity.this).requestPermission(OnBoardActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            }
                        })
                        .setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                break;
        }

        alertDialogBuilder.create().show();
    }

    private void showPermissionWarning() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(getString(R.string.warning));
        alertDialogBuilder
                .setMessage(getString(R.string.permission_warning))
                .setCancelable(true)
                .setPositiveButton(getString(android.R.string.ok),new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog,int id)
                    {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showHomeActivity();
                        dialog.dismiss();
                    }
                }).create().show();
    }
}
