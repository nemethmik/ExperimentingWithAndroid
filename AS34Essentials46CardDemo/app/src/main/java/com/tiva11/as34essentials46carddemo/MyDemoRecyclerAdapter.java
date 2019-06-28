package com.tiva11.as34essentials46carddemo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tiva11.as34essentials46carddemo.databinding.CardLayoutBinding;

public class MyDemoRecyclerAdapter extends RecyclerView.Adapter<MyDemoRecyclerAdapter.ViewHolder> {
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardLayoutBinding binding = CardLayoutBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup,false);
        return new ViewHolder(binding.getRoot(),binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.binding.itemTitle.setText("Element " + i);
        viewHolder.binding.itemDescription.setText("Description for item " + i);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardLayoutBinding binding;
        public ViewHolder(@NonNull View itemView,@NonNull CardLayoutBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }
}
