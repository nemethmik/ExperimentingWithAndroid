package com.tiva11.navigationtut;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavDirections;

public class AppMainViewModel extends ViewModel {
    private static final String TAG = "AppMainViewModel";
    private final MutableLiveData<NavDirections> newDestination = new MutableLiveData<>();
    public LiveData<NavDirections> getNewDestination() {
        return newDestination;
    }
    public void setNewDestination(NavDirections destination) {
        Log.i(TAG, "setNewDestination: " + destination);
        newDestination.setValue(destination);
    }
}
