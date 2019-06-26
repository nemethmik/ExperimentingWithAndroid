package com.tiva11.as34essentials32viewmodeldemo.ui.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tiva11.as34essentials32viewmodeldemo.BR;
import com.tiva11.as34essentials32viewmodeldemo.R;
import com.tiva11.as34essentials32viewmodeldemo.databinding.MainFragmentBinding;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;
    private MainFragmentBinding mMainFragmentBinding;
    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mMainFragmentBinding = MainFragmentBinding.inflate(inflater,container,false);
        mMainFragmentBinding.setLifecycleOwner(this);
        return mMainFragmentBinding.getRoot();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        //Never use this, use the typesafe generated variable setter
//        mMainFragmentBinding.setVariable(BR.mainFragmentViewModel,mViewModel);
        mMainFragmentBinding.setMainFragmentViewModel(mViewModel);
        mMainFragmentBinding.setMainFragment(this);
        //No need for this observer, since the data binding expression via variable definitions does it automatically
//        mViewModel.getResult().observe(this, new Observer<Float>() {
//            @Override
//            public void onChanged(@Nullable Float aFloat) {
//                mMainFragmentBinding.resultText.setText(aFloat.toString());
//            }
//        });
        //This is not needed since the lambda binding expression for the onClick in the XML does this
//        mMainFragmentBinding.convertButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!mMainFragmentBinding.dollarText.getText().toString().equals("")) {
//                    mViewModel.convertValue();
//                } else {
//                    mMainFragmentBinding.resultText.setText("No value");
//                }
//            }
//        });
    }

}
