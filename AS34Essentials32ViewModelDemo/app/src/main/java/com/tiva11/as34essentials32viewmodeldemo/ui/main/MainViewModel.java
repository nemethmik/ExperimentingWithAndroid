package com.tiva11.as34essentials32viewmodeldemo.ui.main;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    private static final Float usd_to_eu_rate = 0.74F;
    private String dollarText = "";
//    private Float result = 0F;
    private MutableLiveData<Float> result = new MutableLiveData<>();
    public void setAmount(String value) {
        this.dollarText = value;
//        result = Float.valueOf(dollarText) * usd_to_eu_rate;
        result.setValue(Float.valueOf(dollarText) * usd_to_eu_rate);
    }
//    public Float getResult() {
    public MutableLiveData<Float> getResult() {
        return result;
    }
}
