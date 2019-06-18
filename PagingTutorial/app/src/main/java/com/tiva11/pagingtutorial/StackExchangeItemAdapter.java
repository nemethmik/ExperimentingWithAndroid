package com.tiva11.pagingtutorial;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

public class StackExchangeItemAdapter extends android.arch.paging.PagedListAdapter<StackExchangeAnswers.Item, StackExchangeItemAdapter.StackExchangeItemViewHolder> {
    protected StackExchangeItemAdapter(/* @NonNull DiffUtil.ItemCallback<StackExchangeAnswers.Item> diffCallback */) {
        super(new DiffUtil.ItemCallback<StackExchangeAnswers.Item>() {
            @Override
            public boolean areItemsTheSame(@NonNull StackExchangeAnswers.Item item, @NonNull StackExchangeAnswers.Item t1) {
                return item.answer_id == t1.answer_id;
            }
            @Override
            public boolean areContentsTheSame(@NonNull StackExchangeAnswers.Item item, @NonNull StackExchangeAnswers.Item t1) {
                return item.equals(t1); //If equals not implemented in the class, we get lint error (see https://issuetracker.google.com/issues/116789824)
            }
        });

    }

    @NonNull
    @Override
    public StackExchangeItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stackexchangeitemlayout,viewGroup,false);
        return new StackExchangeItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StackExchangeItemViewHolder stackExchangeItemViewHolder, int i) {
        StackExchangeAnswers.Item sei = getItem(i);
        if(sei != null) {
            Glide.with(stackExchangeItemViewHolder.itemView.getContext())
                    .load(sei.owner.profile_image)
                    .into(stackExchangeItemViewHolder.imageView);
            stackExchangeItemViewHolder.textView.setText(sei.owner.display_name);
        }
    }

    class StackExchangeItemViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textView;
        public StackExchangeItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
        }

    }
}
