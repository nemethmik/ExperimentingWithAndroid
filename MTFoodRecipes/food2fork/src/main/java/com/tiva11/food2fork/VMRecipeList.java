package com.tiva11.food2fork;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VMRecipeList extends ViewModel {
    private final MutableLiveData<List<Recipe>> recipeList = new MutableLiveData<>();
    public LiveData<List<Recipe>> getRecipes() {return recipeList;} //Read-only for UI
    public final MutableLiveData<String> queryString = new MutableLiveData<>();//editText @={vm.queryString}
    final public MutableLiveData<Integer> page = new MutableLiveData<>();
    public final MutableLiveData<Throwable> error = new MutableLiveData<>();
    //queryString and page should be set before calling/triggering this event/function
    public void onSearchRecipesRequest() {
        //The parameters are already expected in the queryString and page MLDs
//        Food2Fork.searchRecipesAsync(queryString.getValue(),page.getValue(),recipeList,error);
        Food2Fork.searchRecipesWithExecutor(queryString.getValue(),page.getValue(),recipeList,error);
    }
    //----------------------------------
    // Saved Queries/Categories - Later this may be factored out into a separate VM
    public static final String[] DEFAULT_SEARCH_CATEGORY_IMAGES = {
            "barbeque", "breakfast", "chicken", "beef", "brunch", "dinner", "wine", "italian"};
    /*
     Uri path = Uri.parse("android.resource://com.codingwithmitch.foodrecipes/drawable/" + mRecipes.get(i).getImage_url());
            Glide.with(((CategoryViewHolder)viewHolder).itemView)
                    .setDefaultRequestOptions(options)
                    .load(path)
                    .into(((CategoryViewHolder)viewHolder).categoryImage);
     */
    public List<SavedQuery> createSystemSearchQueries(){
        List<SavedQuery> queries = new ArrayList<>();
        for(String c: DEFAULT_SEARCH_CATEGORY_IMAGES){
            SavedQuery query = new SavedQuery();
            query.queryString = c;
            query.isSystem = true;
            query.imageUrl = "android.resource://com.tiva11.mtfoodrecipes/drawable/" + c;
            query.saveTime = new Date();
            queries.add(query);
        }
        return queries;
    }
    private final MutableLiveData<List<SavedQuery>> savedQueryList = new MutableLiveData<>();
    public final LiveData<List<SavedQuery>> getSavedQueryList() {return savedQueryList;}
    public final MutableLiveData<SavedQuery> selectedSavedQuery = new MutableLiveData<>();
    public void onRetrieveSavedQueriesRequest() {
        savedQueryList.setValue(createSystemSearchQueries());
    }
}
