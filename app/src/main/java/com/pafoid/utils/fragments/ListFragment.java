package com.pafoid.utils.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pafoid.utils.R;

/**
 * Abstract Fragment class used to display a RecyclerView
 */
public abstract class ListFragment extends Fragment {
    private static final String TAG = "ListFrag";

    //Views
    protected View rootView;
    protected RecyclerView recyclerView;

    //Objects
    protected RecyclerView.Adapter adapter;

    public abstract ListFragment newInstance();

    public ListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list, container, false);

        //Recycler View
        recyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = getAdapter();
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    //Abstract Methods
    protected abstract RecyclerView.Adapter getAdapter();
}
