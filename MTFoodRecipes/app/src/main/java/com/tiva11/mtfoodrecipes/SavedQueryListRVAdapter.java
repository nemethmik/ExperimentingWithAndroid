package com.tiva11.mtfoodrecipes;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tiva11.food2fork.SavedQuery;
import com.tiva11.mtfoodrecipes.databinding.SavedQueryCardviewBinding;

public class SavedQueryListRVAdapter extends ListAdapter<SavedQuery, SavedQueryListRVAdapter.SavedQueryVH> {
    public static final DiffUtil.ItemCallback<SavedQuery> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<SavedQuery>() {
            @Override
            public boolean areItemsTheSame(@NonNull SavedQuery oldQuery, @NonNull SavedQuery newQuery) {
                // User properties may have changed if reloaded from the DB, but ID is fixed
                return oldQuery.queryString.equalsIgnoreCase(newQuery.queryString);
            }
            @Override
            public boolean areContentsTheSame(@NonNull SavedQuery oldQuery, @NonNull SavedQuery newQuery) {
                // NOTE: if you use equals, your object must properly override Object#equals()
                // Incorrectly returning false here will result in too many animations.
                return areItemsTheSame(oldQuery, newQuery);
            }
        };
    public final MutableLiveData<SavedQuery> mldSelectedSavedQuery;
    protected SavedQueryListRVAdapter(@NonNull final MutableLiveData<SavedQuery> selectedSavedQuery ) {
        super(DIFF_CALLBACK);
        mldSelectedSavedQuery = selectedSavedQuery;
    }
    @NonNull
    @Override
    public SavedQueryVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        SavedQueryCardviewBinding binding = SavedQueryCardviewBinding.inflate(
                LayoutInflater.from(viewGroup.getContext()), viewGroup,false);
        return new SavedQueryVH(binding);
//        binding.getRoot().setTag(vh); //This is not required ay more with the flexibility of binding expressions
    }
    @Override
    public void onBindViewHolder(@NonNull SavedQueryVH savedQueryVH, int i) {
        savedQueryVH.bindTo(this.getItem(i));
    }
    public class SavedQueryVH extends RecyclerView.ViewHolder {
        public final SavedQueryCardviewBinding binding;
        public SavedQuery savedQuery;
        public SavedQueryVH(@NonNull SavedQueryCardviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bindTo(SavedQuery query) {
            this.savedQuery = query;
            binding.setSavedQuery(query);
            binding.setVH(this);
            binding.executePendingBindings();
        }
        public void onClick(SavedQuery selectedSavedQuery){ //No need for the View parameter
            mldSelectedSavedQuery.setValue(selectedSavedQuery);
        }

    }
}
