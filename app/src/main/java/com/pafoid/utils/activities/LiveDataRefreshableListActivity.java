package com.pafoid.utils.activities;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.pafoid.utils.R;

/**
 * Abstract Activity class used to display a RecyclerView inside a SwipeRefreshLayout
 * This Activity also uses the new Android Architecture component LiveData to populate its views
 */
public abstract class LiveDataRefreshableListActivity<T> extends LifecycleActivity implements SwipeRefreshLayout.OnRefreshListener, AppCompatCallback {
    private static final String TAG = "RefreshableListAct";

    //Views
    protected SwipeRefreshLayout swipeLayout;
    protected RecyclerView recyclerView;
    protected Toolbar toolbar;

    //Objects
    protected RecyclerView.Adapter adapter;
    protected AppCompatDelegate compatDelegate;

    //Options
    protected boolean refreshOnStart = false;

    //Data
    protected LiveData<T> listDataListener;
    protected T data;

    public LiveDataRefreshableListActivity() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        compatDelegate = AppCompatDelegate.create(this, this);
        compatDelegate.onCreate(savedInstanceState);
        compatDelegate.setContentView(R.layout.activity_refreshable_list);

        toolbar = findViewById(R.id.toolbar);
        compatDelegate.setSupportActionBar(toolbar);

        listDataListener = getDataListener();
        listDataListener.observe(this, dataObserver);

        //Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Swipe To Refresh
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setRefreshing(refreshOnStart);
    }

    protected Observer<T> dataObserver = new Observer<T>() {
        @Override
        public void onChanged(@Nullable T list) {
            data = list;
            adapter = getAdapter();
            recyclerView.setAdapter(adapter);
        }
    };

    @Override
    public void onRefresh() {
        adapter = getAdapter();
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        swipeLayout.setRefreshing(false);
    }

    //Abstract Methods
    protected abstract RecyclerView.Adapter getAdapter();

    protected abstract LiveData<T> getDataListener();

    //Support stuff
    @Override
    public void onSupportActionModeStarted(ActionMode mode) {

    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {

    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }
}
