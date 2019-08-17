package com.tiva11.navigationtut;


import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tiva11.navigationtut.databinding.FragmentChooseRecipientBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseRecipientFragment extends Fragment {


    public ChooseRecipientFragment() {
        // Required empty public constructor
    }

    private FragmentChooseRecipientBinding binding;
    private AppMainViewModel viewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(requireActivity()).get(AppMainViewModel.class);
        binding = FragmentChooseRecipientBinding.inflate(inflater);
        //binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        binding.setVM(viewModel);
        binding.setMyFrag(this);
        return binding.getRoot();
    }
    public NavDirections getDirWithRecipient() {
        String recipient = binding.inputRecipient.getText().toString();
        return ChooseRecipientFragmentDirections.actionChooseRecipientFragmentToSpecifyAmountFragment(recipient);
    }

}
