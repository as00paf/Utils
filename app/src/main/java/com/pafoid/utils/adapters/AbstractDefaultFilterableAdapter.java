package com.pafoid.utils.adapters;

import android.support.annotation.LayoutRes;
import android.widget.Filter;

import com.pafoid.utils.viewHolders.DefaultViewHolder;

import java.util.List;

/**
 * Abstract RecyclerView.Adapter class used to display items in a RecyclerView
 * @param <HolderType> the class to use to instantiate ViewHolders, must extend {@link DefaultViewHolder}
 */
public abstract class AbstractDefaultFilterableAdapter<HolderType extends DefaultViewHolder> extends DefaultAdapter<HolderType>{

    private static final String TAG = "AbstractDefaultFilterableAdapter";

    public AbstractDefaultFilterableAdapter(@LayoutRes int itemRes, Class<HolderType> clazz) {
        super(itemRes, clazz);
    }

    public AbstractDefaultFilterableAdapter(@LayoutRes int itemRes, List items, Class<HolderType> clazz) {
        super(itemRes, items, clazz);
    }

    public AbstractDefaultFilterableAdapter(ViewType[] itemViewTypes) {
        super(itemViewTypes);
    }

    public AbstractDefaultFilterableAdapter(ViewType[] itemViewTypes, List items) {
        super(itemViewTypes, items);
    }

    public abstract Filter getFilter();
}
