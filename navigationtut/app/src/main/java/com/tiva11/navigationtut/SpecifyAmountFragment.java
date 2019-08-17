package com.tiva11.navigationtut;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tiva11.navigationtut.databinding.FragmentSpecifyAmountBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class SpecifyAmountFragment extends Fragment {
    private static final String TAG = "SpecifyAmountFragment";

    public SpecifyAmountFragment() {
        // Required empty public constructor
    }

    AppMainViewModel viewModel;
    FragmentSpecifyAmountBinding binding;
    String recipientName;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle dontUse) {
        viewModel = ViewModelProviders.of(requireActivity()).get(AppMainViewModel.class);
        binding = FragmentSpecifyAmountBinding.inflate(inflater);
        //binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        binding.setVM(viewModel);
        binding.setMyFrag(this);
        recipientName = SpecifyAmountFragmentArgs.fromBundle(getArguments()).getName();
        Log.i(TAG, "onCreateView: " + recipientName);
        return binding.getRoot();
    }
    public NavDirections getDirWithAmount() {
        try {
            float amount = Float.parseFloat(binding.inputAmount.getText().toString());
            return SpecifyAmountFragmentDirections.actionSpecifyAmountFragmentToConfirmationFragment(recipientName, amount);
        } finally {}
    }

}
