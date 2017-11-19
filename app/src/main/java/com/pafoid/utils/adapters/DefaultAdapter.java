package com.pafoid.utils.adapters;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pafoid.utils.viewHolders.DefaultViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * RecyclerView.Adapter class used to display items in a RecyclerView
 * @param <HolderType> the class to use to instantiate ViewHolders, must extend {@link DefaultViewHolder}
 */
public class DefaultAdapter<HolderType extends DefaultViewHolder> extends RecyclerView.Adapter<HolderType>{

    private static final String TAG = "DefaultAdapter";

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_HEADER = 1;

    //Data
    @LayoutRes
    protected int itemRes;
    protected boolean hasMultipleItemTypes = false;
    private ViewType[] itemViewTypes;
    protected List items = new ArrayList();

    protected Class<HolderType> clazz;

    //Constructors
    public DefaultAdapter(@LayoutRes int itemRes, Class<HolderType> clazz) {
        super();
        this.itemRes = itemRes;
        this.clazz = clazz;
        this.hasMultipleItemTypes = false;
    }

    public DefaultAdapter(@LayoutRes int itemRes, List items, Class<HolderType> clazz) {
        super();
        this.itemRes = itemRes;
        this.items = items;
        this.clazz = clazz;
        this.hasMultipleItemTypes = false;
    }

    public DefaultAdapter(ViewType[] itemViewTypes) {
        super();
        this.itemViewTypes = itemViewTypes;
        this.hasMultipleItemTypes = true;
    }

    public DefaultAdapter(ViewType itemViewTypes[], List items) {
        super();
        this.itemViewTypes = itemViewTypes;
        this.items = items;
        this.hasMultipleItemTypes = true;
    }

    @Override
    public HolderType onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        HolderType viewHolder;

        if(hasMultipleItemTypes){
            int resource = getLayoutResource(viewType);
            view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
            Class holderClass = getHolderClass(viewType);
            viewHolder = newHolder(view, holderClass);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(itemRes, parent, false);
            viewHolder = newHolder(view, clazz);
        }

        return viewHolder;
    }

    private Class getHolderClass(int viewType) {
        for (ViewType type : itemViewTypes) {
            if(type.getItemType() == viewType){
                return type.getClazz();
            }
        }

        return null;
    }

    private int getLayoutResource(int viewType) {
        for (ViewType type : itemViewTypes) {
            if(type.getItemType() == viewType){
                return type.getResource();
            }
        }

        return 0;
    }

    private HolderType newHolder(View view, Class clazz){
        try {
            return (HolderType) clazz.getConstructor(View.class).newInstance(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(HolderType holder, int position) {
        holder.setData(items.get(position));
    }

    @Override
    public int getItemCount() {
        if(items == null) return 0;
        return items.size();
    }

    public void setItems(List items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setItemRes(int itemRes) {
        this.itemRes = itemRes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(hasMultipleItemTypes){
            //Try to find the index in the specified view types
            for (ViewType type : itemViewTypes) {
                if(type.indexes != null && new ArrayList<Integer>(Arrays.asList(type.indexes)).contains(position)){
                    return type.getItemType();
                }
            }

            //View type was not defined for this position, returning first view type not associated with indexes
            for (ViewType type : itemViewTypes) {
                if(type.indexes == null){
                    return type.getItemType();
                }
            }
        }

        return super.getItemViewType(position);
    }

    /**
     * Static class used to represent the different view types that should be used with the {@link DefaultAdapter}
     */
    public static class ViewType {
        @LayoutRes
        private int resource;

        private int itemType;
        private Integer[] indexes;
        private Class clazz;

        /**
         * Constructor for ViewType
         * @param resource the layout resource to use when inflating the item
         * @param itemType the type of item (can be anything you choose)
         * @param clazz the class to use when instantiating the item
         * @param indexes an array of integers representing the position of this particular item
         */
        public ViewType(@LayoutRes int resource, int itemType, Class clazz, Integer[] indexes) {
            this.resource = resource;
            this.itemType = itemType;
            this.clazz = clazz;
            this.indexes = indexes;
        }

        /**
         * Constructor for ViewType
         * @param resource the layout resource to use when inflating the item
         * @param itemType the type of item (can be anything you choose)
         * @param clazz the class to use when instantiating the item
         */
        public ViewType(@LayoutRes int resource, int itemType, Class clazz) {
            this.resource = resource;
            this.itemType = itemType;
            this.clazz = clazz;
        }

        //Getters/Setters
        public int getResource() {
            return resource;
        }

        public void setResource(int resource) {
            this.resource = resource;
        }

        public int getItemType() {
            return itemType;
        }

        public void setItemType(int itemType) {
            this.itemType = itemType;
        }

        public Integer[] getIndexes() {
            return indexes;
        }

        public void setIndexes(Integer[] indexes) {
            this.indexes = indexes;
        }

        public Class getClazz() {
            return clazz;
        }

        public void setClazz(Class clazz) {
            this.clazz = clazz;
        }
    }
}
