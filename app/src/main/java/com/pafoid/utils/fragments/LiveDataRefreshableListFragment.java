package com.pafoid.utils.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pafoid.utils.R;

import java.util.List;

/**
 * Abstract Fragment class used to display a RecyclerView inside a SwipeRefreshLayout
 * This Fragment also uses the new Android Architecture component LiveData to populate its views
 */
public abstract class LiveDataRefreshableListFragment extends LifecycleFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "RefreshableListFrag";

    //Views
    protected View rootView;
    protected SwipeRefreshLayout swipeLayout;
    protected RecyclerView recyclerView;

    //Objects
    protected RecyclerView.Adapter adapter;

    //Options
    protected boolean refreshOnStart = false;

    //Data
    protected LiveData<List> listDataListener;
    protected List data;

    public abstract LiveDataRefreshableListFragment newInstance();

    public LiveDataRefreshableListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listDataListener = getDataListener();
        listDataListener.observe(this, dataObserver);
    }

    protected Observer<List> dataObserver = new Observer<List>() {
        @Override
        public void onChanged(@Nullable List list) {
            data = list;
            adapter = getAdapter();
            recyclerView.setAdapter(adapter);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_refreshable_list, container, false);

        //Recycler View
        recyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        //Swipe To Refresh
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setRefreshing(refreshOnStart);

        return rootView;
    }

    //Abstract Methods
    protected abstract RecyclerView.Adapter getAdapter();

    protected abstract LiveData<List> getDataListener();

    @Override
    public abstract void onRefresh();
}
