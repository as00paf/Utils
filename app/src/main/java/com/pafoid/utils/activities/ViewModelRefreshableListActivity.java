package com.pafoid.utils.activities;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.pafoid.utils.R;
import com.pafoid.utils.data.ListViewModel;

import java.util.List;

/**
 * Abstract Activity class used to display a RecyclerView inside a SwipeRefreshLayout
 * This Activity also uses the new Android Architecture component LiveData to populate its views
 * @param <T> the type of item to be loaded
 */
public abstract class ViewModelRefreshableListActivity<T> extends LifecycleActivity implements SwipeRefreshLayout.OnRefreshListener, AppCompatCallback, Observer<List<T>> {
    private static final String TAG = "VMRefreshLstAct";

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
    protected List data;
    protected ListViewModel<T> viewModel;

    public ViewModelRefreshableListActivity() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            viewModel = ViewModelProviders.of(this).get(getViewModelClass());
            if(viewModel.getData() != null) {
                viewModel.getData().observe(this, this);
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "Could not load data. Error : " + e.getMessage());
        }

        //Toolbar
        compatDelegate = AppCompatDelegate.create(this, this);
        compatDelegate.onCreate(savedInstanceState);
        compatDelegate.setContentView(getLayoutResource());

        toolbar = findViewById(R.id.toolbar);
        compatDelegate.setSupportActionBar(toolbar);

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

    protected int getLayoutResource() {
        return R.layout.activity_refreshable_list;
    }

    @Override
    public void onRefresh() {
        adapter = getAdapter();
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void onChanged(@Nullable List<T> list) {
        data = list;
        adapter = getAdapter();
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    //Compat
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

    //Abstract Methods
    protected abstract RecyclerView.Adapter getAdapter();

    protected abstract Class<ListViewModel<T>> getViewModelClass();
}
