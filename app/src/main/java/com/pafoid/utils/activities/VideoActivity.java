package com.pafoid.utils.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.VideoView;

import com.pafoid.utils.R;
import com.pafoid.utils.views.StyledMediaController;

/**
 * Abstract Activity used to play a video
 *
 * Extend this Activity and add it to your manifest
 * Override abstract methods to adapt to your specific needs
 */
public abstract class VideoActivity extends AppCompatActivity {

    private static final String POSITION_KEY = "videoPosition";

    //Views
    private VideoView videoView;
    private StyledMediaController mediacontroller;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getVideoTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getVideoTitle());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        videoView = (VideoView) findViewById(R.id.video_fragment_videoView);

        initVideo();
    }

    private void showErrorDialog() {
        new AlertDialog.Builder(VideoActivity.this)
                .setTitle(getString(R.string.error))
                .setMessage(getString(R.string.error_message))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        })
                .show();
    }

    private void initVideo() {
        Uri videoURL = getVideoUri();

        try {
            mediacontroller = new StyledMediaController(this);
            mediacontroller.setAnchorView(videoView);

            videoView.setMediaController(mediacontroller);
            videoView.setVideoURI(videoURL);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();

            showErrorDialog();
        }

        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                progressDialog.dismiss();
                videoView.start();
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                progressDialog.dismiss();

                showErrorDialog();

                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityCompat.finishAfterTransition(this);
            } else {
                finish();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        videoView.stopPlayback();
        videoView.setMediaController(null);

        finish();
    }

    @Override
    protected void onSaveInstanceState (Bundle outState){
        outState.putInt(POSITION_KEY, videoView.getCurrentPosition()); // save it here
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        int position = savedInstanceState.getInt(POSITION_KEY);
        videoView.seekTo(position);
        videoView.start();
    }

    //Abstract Methods

    /**
     * Override this method to get the title of the video
     * @return
     */
    protected abstract String getVideoTitle();

    /**
     * Override this method to get the Uri of the video to play
     * @return the Uri of the video
     */
    protected abstract Uri getVideoUri();

    /**
     * Override this method to get the Video object from the Intent
     * @return the video object
     */
    protected abstract Object getVideoObject();

}