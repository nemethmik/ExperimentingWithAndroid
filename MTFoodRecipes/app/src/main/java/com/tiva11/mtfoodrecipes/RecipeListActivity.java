package com.tiva11.mtfoodrecipes;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tiva11.food2fork.Food2Fork;
import com.tiva11.food2fork.GetRecipeResponse;
import com.tiva11.food2fork.Recipe;
import com.tiva11.food2fork.SearchRecipesResponse;
import com.tiva11.food2fork.VMRecipeList;

import java.util.List;

public class RecipeListActivity extends BaseActivity implements Food2Fork.IGetRecipeResponse {
    private static final String TAG = "RecipeListActivity";
    private VMRecipeList vmRecipeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        vmRecipeList = ViewModelProviders.of(this).get(VMRecipeList.class);
        subscribeVMObservers();
//        askInternetPermission();
        findViewById(R.id.buttonShowProgressBar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                testSearchRecipesAsync();
//                testQueryRecipesAsync(); //The progressBar is not shown with this call, so just use the search.
            load1stPageViaTheVM();
            }
        });
        findViewById(R.id.buttonHideProgressBar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar(false);
            }
        });
    }
    // Make sure to check internet permission, otherwise Retrofit async calls crashes the application
    @Deprecated
    void testSearchRecipesAsync(){
        if(isInternetPermissionGranted(true)) {
            Food2Fork.searchRecipesAsync("Chicken breast", 1, new Food2Fork.ISearchRecipesResponse() {
                @Override
                public void onResponseSearchRecipes(SearchRecipesResponse response, String queryString, int page) {
                    showProgressBar(false);
                    Toast.makeText(RecipeListActivity.this, "Number of recipes on page 1 is " + response.count, Toast.LENGTH_LONG).show();
                    showProgressBar(true);
                    Food2Fork.getRecipeAsync(response.recipes.get(0).recipeId,RecipeListActivity.this);
                    //async await is badly missing in Java, this is the only reason to pick Kotlin, it has async await
                }
                @Override
                public void onFailureSearchRecipes(Throwable t, String queryString, int page) {
                    showErrorMessage(t);
                }
            });
        }
    }
    // Food2Fork.queryRecipesAsync is implemented in a way that catches missing internet permission
    // It's better to use the search function
    @Deprecated
    void testQueryRecipesAsync() {
        Food2Fork.queryRecipesAsync("Chicken breast", 1, new Food2Fork.ISearchRecipesResponse() {
            @Override
            public void onResponseSearchRecipes(SearchRecipesResponse response, String queryString, int page) {
                showProgressBar(false);
                Toast.makeText(RecipeListActivity.this, "Number of recipes on page 1 is " + response.count, Toast.LENGTH_LONG).show();
                Food2Fork.getRecipeAsync(response.recipes.get(0).recipeId,RecipeListActivity.this);
            }
            @Override
            public void onFailureSearchRecipes(Throwable t, String queryString, int page) {
                showErrorMessage(t);
            }
        });
        showProgressBar(false);
    }
    boolean isInternetPermissionGranted(boolean showToastMessage) {
        if(ContextCompat.checkSelfPermission(RecipeListActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            showProgressBar(false);
            if(showToastMessage) {
                Toast.makeText(RecipeListActivity.this, "Oops INTERNET permission not granted", Toast.LENGTH_LONG).show();
            }
            return false;
        } else return true;
    }

    @Override @Deprecated
    public void onResponseGetRecipe(GetRecipeResponse response, String recipeId) {
        showProgressBar(false);
        Toast.makeText(RecipeListActivity.this, "Recipe is " + response.recipe.title, Toast.LENGTH_LONG).show();
    }

    @Override @Deprecated
    public void onFailureGetRecipe(Throwable t, String recipeId) {
        showErrorMessage(t);
    }
    void showErrorMessage(Throwable t) {
        showProgressBar(false);
        Toast.makeText(RecipeListActivity.this, "Oops " + t.toString() + ":" + t.getMessage(), Toast.LENGTH_LONG).show();
    }
    void subscribeVMObservers(){
        vmRecipeList.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                showProgressBar(false);
                if(recipes.size() > 0) {
                    Log.d(TAG, "onChanged: recipes " + recipes.size() + " with " + recipes.get(recipes.size() - 1).title);
                    Toast.makeText(RecipeListActivity.this, "Number of recipes is " + recipes.size(), Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG, "onChanged: no recipes");
                    Toast.makeText(RecipeListActivity.this, "No recipes", Toast.LENGTH_SHORT).show();
                }
            }
        });
        vmRecipeList.getError().observe(this, new Observer<Throwable>() {
            @Override public void onChanged(@Nullable Throwable t) { showErrorMessage(t); }
        });
    }
    void load1stPageViaTheVM() {
        if(isInternetPermissionGranted(true)) {
            showProgressBar(true);
            vmRecipeList.queryString.setValue("Chicken breast");
            vmRecipeList.page.setValue(1);
            vmRecipeList.onSearchRecipesRequest(); //The recipes are pumped into a LD of the VM, which is observed by this activity
        }
    }
}
