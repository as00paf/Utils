package com.pafoid.utils.adapters;

import android.view.View;

/**
 * Interface used for ViewPagers that require icons instead of titles
 */
public interface ITabbedPagerAdapter {
    View getTabView(int position);
}
