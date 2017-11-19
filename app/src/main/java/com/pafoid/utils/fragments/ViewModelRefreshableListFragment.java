package com.pafoid.utils.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pafoid.utils.R;
import com.pafoid.utils.data.ListViewModel;

import java.util.List;

/**
 * Abstract Fragment class used to display a RecyclerView inside a SwipeRefreshLayout
 * This Fragment also uses the new Android Architecture component LiveData to populate its views
 * @param <T> the type of item to be loaded
 */
public abstract class ViewModelRefreshableListFragment<T> extends LifecycleFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "VMRefreshLstFrgmnt";

    //Views
    protected View rootView;
    protected SwipeRefreshLayout swipeLayout;
    protected RecyclerView recyclerView;

    //Objects
    protected RecyclerView.Adapter adapter;

    //Options
    protected boolean refreshOnStart = false;

    //Data
    protected List data;
    protected ListViewModel<T> viewModel;

    public abstract ViewModelRefreshableListFragment newInstance();

    public ViewModelRefreshableListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            viewModel = ViewModelProviders.of(this).get(getViewModelClass());
            if(viewModel.getData() != null) {
                viewModel.getData().observe(this, dataObserver);
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "Could not load data. Error : " + e.getMessage());
        }
    }

    protected Observer<List<T>> dataObserver = new Observer<List<T>>() {
        @Override
        public void onChanged(@Nullable List<T> list) {
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

    protected abstract Class<ListViewModel<T>> getViewModelClass();

    @Override
    public abstract void onRefresh();
}
