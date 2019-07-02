package com.tiva11.food2fork;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

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
}
