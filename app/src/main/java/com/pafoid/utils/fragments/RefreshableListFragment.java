package com.pafoid.utils.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pafoid.utils.R;

/**
 * Abstract Fragment class used to display a RecyclerView inside a SwipeRefreshLayout
 */
public abstract class RefreshableListFragment extends LifecycleFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "RefreshableListFrag";

    //Views
    protected View rootView;
    protected SwipeRefreshLayout swipeLayout;
    protected RecyclerView recyclerView;

    //Objects
    protected RecyclerView.Adapter adapter;

    //Options
    protected boolean refreshOnStart = false;

    public abstract RefreshableListFragment newInstance();

    public RefreshableListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_refreshable_list, container, false);

        //Recycler View
        recyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = getAdapter();
        recyclerView.setAdapter(adapter);

        //Swipe To Refresh
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setRefreshing(refreshOnStart);

        return rootView;
    }

    //Abstract Methods
    protected abstract RecyclerView.Adapter getAdapter();

    @Override
    public abstract void onRefresh();
}
