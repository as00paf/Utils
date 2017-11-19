package com.pafoid.utils.dialogs;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.support.v7.app.AppCompatDialogFragment;

/**
 * Created by as00p on 2017-10-12.
 */

public class LifecycleDialogFragment extends AppCompatDialogFragment implements LifecycleRegistryOwner {

    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);

    @Override
    public LifecycleRegistry getLifecycle() {
        return mRegistry;
    }
}