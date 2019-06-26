package com.tiva11.as34essentials32viewmodeldemo.ui.main;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    private static final Float usd_to_eu_rate = 0.74F;
    public MutableLiveData<String> dollarText = new MutableLiveData<>();
    public MutableLiveData<Float> euroFloatValue = new MutableLiveData<>();
    public MainViewModel() {
        dollarText.setValue("");
        convertValue();
    }
    public void convertValue() {
        try {
            euroFloatValue.setValue(Float.valueOf(dollarText.getValue()) * usd_to_eu_rate);
        } catch(Exception e) {
            euroFloatValue.setValue(0F);
        }
    }
}
