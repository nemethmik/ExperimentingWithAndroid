package com.tiva11.mtfoodrecipes;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import com.tiva11.food2fork.Recipe;
import com.tiva11.food2fork.VMRecipeList;
import com.tiva11.mtfoodrecipes.databinding.ActivityRecipeDetailsBinding;
import com.tiva11.mtfoodrecipes.databinding.ActivityRecipeListBinding;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class RecipeDetailsActivity extends AppCompatActivity {
    //TODO: Save favorite list in shared preferences with Gson string [{"id","datetime favorited"},...]
    //TODO: Why progress bar is not shown when repeat querying recipes after back from recipe details?
    public static String EXTRA_RECIPEID = "recipe";
    ActivityRecipeDetailsBinding binding;
    private VMRecipeList vmRecipeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vmRecipeList = ViewModelProviders.of(this).get(VMRecipeList.class);
        vmRecipeList.favoriteRecipes = RecipeListActivity.getFavoritesFromSharedPreferences(this);
        binding = ActivityRecipeDetailsBinding.inflate(getLayoutInflater());
        binding.setLifecycleOwner(this); //THIS IS TERRIBLY IMPORTANT for Live Data Binding
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initIngredientsLV();
        subscribeVMObservers();
        initSearchViewAndSetListener();
        binding.setRecipe(vmRecipeList.recipe);
        binding.setUI(this);
        if(getIntent().hasExtra(EXTRA_RECIPEID)) {
            String recipeId = getIntent().getStringExtra(EXTRA_RECIPEID);
//            vmRecipeList.onGetDemoRecipeRequest();
            vmRecipeList.onGetRecipeRequest(recipeId);
            Toast.makeText(this, "Recipe ID " + recipeId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "NO RECIPE ID", Toast.LENGTH_SHORT).show();
        }
    }
    private void initIngredientsLV() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,new ArrayList<String>());
        binding.ingredientsListView.setAdapter(adapter);
    }
    void showErrorMessage(Throwable t) {
//        showProgressBar(false);
        Toast.makeText(this, "Oops " + t.toString() + ":" + t.getMessage(), Toast.LENGTH_LONG).show();
    }
    private void subscribeVMObservers() {
        vmRecipeList.error.observe(this, new Observer<Throwable>() {
            @Override public void onChanged(@Nullable Throwable t) { showErrorMessage(t); }
        });
        vmRecipeList.recipe.observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                try {
                    Date favoriteTime = vmRecipeList.isRecipeFavorite(recipe.recipeId);
                    if(favoriteTime != null && !recipe.isFavorite) {
                        recipe.favoriteTime = favoriteTime;
                        recipe.isFavorite = true;
                        vmRecipeList.recipe.setValue(recipe);
                    }
                } catch (ParseException e) {
                    vmRecipeList.error.setValue(e);
                }
//                loadIngredientsToLV(recipe);
//                Toast.makeText(RecipeDetailsActivity.this, "Recipe received from VM", Toast.LENGTH_SHORT).show();
            }
        });
    }
//    private void loadIngredientsToLV(Recipe recipe) {
//        ArrayAdapter<String> adapter = (ArrayAdapter<String>)binding.ingredientsListView.getAdapter();
//        adapter.addAll(recipe.ingredients);
//        adapter.notifyDataSetChanged();
//    }
    public void onFavoriteFABClicked(final Recipe recipe) {
        recipe.isFavorite = !recipe.isFavorite;
        if(recipe.isFavorite) recipe.favoriteTime = new Date(); else recipe.favoriteTime = null;
        vmRecipeList.addOrRemoveFavoriteRecipe(recipe.recipeId,recipe.favoriteTime);
        RecipeListActivity.saveFavoriteInSharedPreferences(this,recipe.recipeId,recipe.favoriteTime);
        //If this doesn't work as expected, you forgot to call binding.setLifecycleOwner(this)
        vmRecipeList.recipe.setValue(recipe);
//        binding.invalidateAll(); //This is needed when binding POJOs
        Snackbar.make(binding.fab, "Recipe " + recipe.title + " is favorited", Snackbar.LENGTH_LONG)
            .setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recipe.isFavorite = !recipe.isFavorite;
                    if(recipe.isFavorite) recipe.favoriteTime = new Date();
                    vmRecipeList.recipe.setValue(recipe);
                    vmRecipeList.addOrRemoveFavoriteRecipe(recipe.recipeId,recipe.favoriteTime);
                    RecipeListActivity.saveFavoriteInSharedPreferences( RecipeDetailsActivity.this,recipe.recipeId,recipe.favoriteTime);
//                        binding.invalidateAll();
                }
            }).show();
    }
    void initSearchViewAndSetListener(){
        if(binding.searchView != null) {
            binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    openRecipeListActivity(query);
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String newText) { return false; }
            });
        }
    }
    void openRecipeListActivity(String queryString) {
        Intent i = new Intent(this,RecipeListActivity.class);
        i.putExtra(RecipeListActivity.EXTRA_QUERY_STRING,queryString);
        startActivity(i);
    }
}
