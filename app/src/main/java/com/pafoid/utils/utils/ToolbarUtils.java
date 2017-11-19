package com.pafoid.utils.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

/**
 * Helper class used to deal with Toolbars
 */
public class ToolbarUtils {

    public static TextView getTitleTextView(Toolbar toolbar){
        TextView textViewTitle = null;
        for(int i = 0; i<toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView) {
                textViewTitle = (TextView) view;
                break;
            }
        }
        return textViewTitle;
    }

    /**
     * Helper method used to set the transition name of the title TextView
     * @param toolbar the toolbar
     * @param transitionName the name to be set on the item
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setToolbarTitleTransitionName(Toolbar toolbar, String transitionName){
        TextView toolbarTitle = getTitleTextView(toolbar);
        if(toolbarTitle != null) toolbarTitle.setTransitionName(transitionName);
    }
}
