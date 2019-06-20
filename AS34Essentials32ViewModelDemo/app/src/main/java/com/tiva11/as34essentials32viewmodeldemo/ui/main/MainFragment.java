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
        // When databinding is enabled and a layout tag is added to wrap the fragment XML
        // a special inflating is required.
        // It's not clear why not only one function call?
//        return inflater.inflate(R.layout.main_fragment, container, false);
        mMainFragmentBinding = MainFragmentBinding.inflate(inflater,container,false);
        mMainFragmentBinding.setLifecycleOwner(this);
        return mMainFragmentBinding.getRoot();
    }
//    private EditText dollarText;
//    private TextView resultText; //Not needed any more when using data binding
//    private Button convertButton;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // The next line is just to experiment what would happen with the result text, when the phone is rotated
        // mViewModel = new MainViewModel();
//        dollarText = getView().findViewById(R.id.dollarText);
//        resultText = getView().findViewById(R.id.resultText);
//        convertButton = getView().findViewById(R.id.convertButton);
//        resultText.setText(mViewModel.getResult().toString());
        mViewModel.getResult().observe(this, new Observer<Float>() {
            @Override
            public void onChanged(@Nullable Float aFloat) {
                //With data binding, the binding class has fields for each views from the layout XML
//                resultText.setText(aFloat.toString());
                mMainFragmentBinding.resultText.setText(aFloat.toString());
            }
        });
//        convertButton.setOnClickListener(new View.OnClickListener() {
        mMainFragmentBinding.convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!dollarText.getText().toString().equals("")) {
//                    mViewModel.setAmount(dollarText.getText().toString());
                if (!mMainFragmentBinding.dollarText.getText().toString().equals("")) {
                    mViewModel.setAmount(mMainFragmentBinding.dollarText.getText().toString());
//                    resultText.setText(mViewModel.getResult().toString());
                } else {
//                    resultText.setText("No Value");
                    mMainFragmentBinding.resultText.setText("No value");
                }
            }
        });
    }

}
