package com.pafoid.utils.views;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.SeekBar;

import com.pafoid.utils.R;

public class StyledMediaController extends MediaController {

    public StyledMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StyledMediaController(Context context, boolean useFastForward) {
        super(context, useFastForward);
    }

    public StyledMediaController(Context context) {
        super(context);
    }

    private void initProgressBarStyle() {
        ViewGroup parent = (ViewGroup) getChildAt(0);
        ViewGroup subParent = (ViewGroup) parent.getChildAt(1);
        SeekBar seekBar = (SeekBar) subParent.getChildAt(1);
        if(seekBar != null){
            seekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY));
            Drawable thumb = ContextCompat.getDrawable(getContext(), R.drawable.thumb);
            seekBar.setThumb(thumb);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                seekBar.getLayoutParams().height = thumb.getIntrinsicHeight();
            }

        }
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);
        initProgressBarStyle();
    }

}
