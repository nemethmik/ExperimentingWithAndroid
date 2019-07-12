package com.tiva11.food2fork;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class VMRecipeList extends ViewModel {
    private final MutableLiveData<List<Recipe>> recipeList = new MutableLiveData<>();
    public LiveData<List<Recipe>> getRecipes() {
        if(recipeList.getValue() == null) recipeList.setValue(new ArrayList<Recipe>());
        return recipeList;
    } //Read-only for UI
//    public void clearRecipeList() { recipeList.setValue(new ArrayList<Recipe>()); }
    public final MutableLiveData<String> queryString = new MutableLiveData<>();//editText @={vm.queryString}
    final public MutableLiveData<Integer> page = new MutableLiveData<>();
    public final MutableLiveData<Throwable> error = new MutableLiveData<>();
    //queryString and page should be set before calling/triggering this event/function
    public void onSearchRecipesRequest() {
        //The parameters are already expected in the queryString and page MLDs
//        Food2Fork.searchRecipesAsync(queryString.getValue(),page.getValue(),recipeList,error);
        Food2Fork.searchRecipesWithExecutor(queryString.getValue(),page.getValue(),recipeList,error);
    }
    public final MutableLiveData<Recipe> recipe = new MutableLiveData<>();
//    public void onGetDemoRecipeRequest(){
//        Recipe r = new Recipe();
//        r.title = "Cajun Spices";
//        r.publisher = "Gourmet Central";
//        r.favoriteTime = new Date();
//        r.isFavorite = true;
//        r.socialRank = 97.8;
//        r.imageUrl = "https://assets3.thrillist.com/v1/image/1774322/size/tmg-article_default_mobile.jpg";
//        r.ingredients = new ArrayList<>();
//        String[] il = new String[]{
//            "2 jalapeno peppers, cut in half lengthwise and seeded",
//            "2 slices sour dough bread",
//            "1 tablespoon butter, room temperature",
//            "2 tablespoons cream cheese, room temperature",
//            "1/2 cup jack and cheddar cheese, shredded",
//            "1 tablespoon tortilla chips, crumbled\n"};
//        for(String i:il) {
//            r.ingredients.add(i);
//        }
//        recipe.setValue(r);
//    }
    public void onGetRecipeRequest(String recipeId) {
        Food2Fork.getRecipeAsync(recipeId,recipe,error);
    }
    @NonNull
    public Map<String,Date> favoriteRecipes = new Hashtable<>();
    public Date isRecipeFavorite(String recipeId) throws ParseException{
        if(favoriteRecipes.containsKey(recipeId)) {
            return favoriteRecipes.get(recipeId);
        } else return null;
    }
    public void addOrRemoveFavoriteRecipe(String recipeId, Date favoriteTime) {
        if(favoriteTime == null) favoriteRecipes.remove(recipeId);
        else favoriteRecipes.put(recipeId, favoriteTime);
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
