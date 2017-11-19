package com.pafoid.utils.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

/**
 * Abstract Android ViewModel class that uses a List of type T as its {@link MutableLiveData}
 * Extend this class and override the methods to use it in your project
 * @param <T> the type of items in the List
 */
public abstract class ListViewModel<T> extends ViewModel {

    protected MutableLiveData<List<T>> data;

    public ListViewModel() {
        super();
    }

    public ListViewModel(MutableLiveData<List<T>> data) {
        this.data = data;
    }

    public LiveData<List<T>> getData(){
        if(data == null){
            data = loadData();
        }

        return data;
    }

    protected abstract MutableLiveData<List<T>> loadData();
}
