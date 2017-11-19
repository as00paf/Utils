package com.pafoid.utils.activities;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pafoid.utils.R;

/**
 * Abstract Activity class used to display a RecyclerView
 * This Activity also uses the new Android Architecture component LiveData to populate its views
 */
public abstract class LiveDataListActivity<T> extends LifecycleActivity implements AppCompatCallback, View.OnClickListener {
    private static final String TAG = "LiveDataListActivity";

    //Views
    protected RecyclerView recyclerView;
    protected Toolbar toolbar;
    protected FloatingActionButton fab;

    //Objects
    protected RecyclerView.Adapter adapter;
    protected AppCompatDelegate compatDelegate;

    //Data
    protected LiveData<T> listDataListener;
    protected T data;

    public LiveDataListActivity() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        compatDelegate = AppCompatDelegate.create(this, this);
        compatDelegate.onCreate(savedInstanceState);
        compatDelegate.setContentView(R.layout.activity_fab_list);

        toolbar = findViewById(R.id.toolbar);
        compatDelegate.setSupportActionBar(toolbar);

        listDataListener = getDataListener();
        listDataListener.observe(this, dataObserver);

        //Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Fab
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    protected Observer<T> dataObserver = new Observer<T>() {
        @Override
        public void onChanged(@Nullable T list) {
            data = list;
            adapter = getAdapter();
            recyclerView.setAdapter(adapter);
        }
    };

    //Abstract Methods
    protected abstract RecyclerView.Adapter getAdapter();

    protected abstract LiveData<T> getDataListener();

    @Override
    public abstract void onClick(View view);
}
