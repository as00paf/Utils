package com.pafoid.utils.viewHolders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class DefaultViewHolder<T> extends RecyclerView.ViewHolder {

    protected T data;

    public DefaultViewHolder(View itemView) {
        super(itemView);
        initViews();
    }

    public DefaultViewHolder(View itemView, T data) {
        super(itemView);
        this.data = data;
        initViews();
    }

    protected Context getContext(){
        if(itemView == null) return null;
        return itemView.getContext();
    }

    protected abstract void initViews();

    protected abstract void updateViews();

    //Getters/Setters
    public T getData() {
        return data;
    }

    public void setData(T data) {
        if(this.data == data) return;
        this.data = data;
        updateViews();
    }
}
