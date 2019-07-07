package com.tiva11.mtfoodrecipes;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.tiva11.food2fork.Recipe;
import com.tiva11.food2fork.SavedQuery;
import com.tiva11.food2fork.VMRecipeList;
import com.tiva11.mtfoodrecipes.databinding.ActivityRecipeListBinding;

import java.util.List;

public class RecipeListActivity extends AppCompatActivity {
    private static final String TAG = "RecipeListActivity";
    private static final String QUERY_STRING = "QUERY_STRING";
    private static final String RECIPE_ID = "RECIPE_ID";
    private VMRecipeList vmRecipeList;
    private ActivityRecipeListBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_list);
        binding.setUIController(this);
        //This would be an alternative content view initialization, which might work with the
        //BaseActivity concept. But the Base Activity idea is quite meaningless.
        //XML inclusion and other more flexible techniques would be available for modular
        //application structuring
//        binding = ActivityRecipeListBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_recipe_list); //This is the old style, no data binding
        vmRecipeList = ViewModelProviders.of(this).get(VMRecipeList.class);
        setSupportActionBar(binding.toolbar);//This is terribly important for menus
        subscribeVMObservers();
        String savedQueryString = getSavedQueryStringFromSharedPreferences();
        if(savedQueryString != null && !savedQueryString.isEmpty()) {
            initRecipeListRV();
            loadPageOfRecipesViaTheVM(savedQueryString,1);
        } else initSavedQueryRV();
        initSearchViewAndSetListener();
    }
    void initSearchViewAndSetListener(){
        if(binding.searchView != null) {
            //There is no way to define binding expression for a search view in the layout XML
            //The only way is to define a listener here in the UI controller Java class
            binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                private static final String TAG = "RecipeListActivity";
                @Override
                public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(RecipeListActivity.this, "Query Text:" + query, Toast.LENGTH_SHORT).show();
                    loadPageOfRecipesViaTheVM(query, 1);
                    //This is important with false return, the keyboard panel is closed,
                    //otherwise it is kept open
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String newText) {
//                    Log.d(TAG, "onQueryTextChange: " + newText);
                    return false;
                }
            });
        }
    }
    public void showProgressBar(boolean show) {
        binding.progressBar.setVisibility(show ? ProgressBar.VISIBLE : ProgressBar.INVISIBLE);
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
                if(binding.recipeListRV.getAdapter() instanceof RecipeListRVAdapter){
                    RecipeListRVAdapter adapter = (RecipeListRVAdapter)binding.recipeListRV.getAdapter();
                    adapter.submitList(recipes);
                    String savedRecipeId = getSavedRecipeIdFromSharedPreferences();
                    if(savedRecipeId != null && !savedRecipeId.isEmpty()) {
                        for(int position = 0;position < recipes.size();position++) {
                            if(recipes.get(position).recipeId.equals(savedRecipeId)) {
                                binding.recipeListRV.scrollToPosition(position);
                                break;
                            }
                        }
                    }
                }
            }
        });
        vmRecipeList.error.observe(this, new Observer<Throwable>() {
            @Override public void onChanged(@Nullable Throwable t) { showErrorMessage(t); }
        });
        vmRecipeList.getSavedQueryList().observe(this, new Observer<List<SavedQuery>>() {
            @Override
            public void onChanged(@Nullable List<SavedQuery> savedQueries) {
                if(binding.recipeListRV.getAdapter() instanceof SavedQueryListRVAdapter) {
                    ((SavedQueryListRVAdapter)binding.recipeListRV.getAdapter()).submitList(savedQueries);
                    saveQueryStringInSharedPreferences(null,null); //Delete the saved query String
                }
            }
        });
        vmRecipeList.selectedSavedQuery.observe(this, new Observer<SavedQuery>() {
            @Override
            public void onChanged(@Nullable SavedQuery selectedSavedQuery) {
                Toast.makeText(RecipeListActivity.this, selectedSavedQuery.queryString + " selected", Toast.LENGTH_SHORT).show();
                // loadPageOfRecipes function calls initRecipeListRV automatically if the adapter is not for the recipe list
                loadPageOfRecipesViaTheVM(selectedSavedQuery.queryString,1);
            }
        });
    }
    //If queryString is null, the previous query, saved in VM, is called
    //If page is less than 1, the same page is queried again
    public void loadPageOfRecipesViaTheVM(String queryString, int page) {
        if(isInternetPermissionGranted(true)) {
            showProgressBar(true);
            initRecipeListRV();
            if(queryString != null && !queryString.isEmpty()) vmRecipeList.queryString.setValue(queryString);
            if(page > 0) vmRecipeList.page.setValue(page);
            vmRecipeList.onSearchRecipesRequest(); //The recipes are pumped into a LD of the VM, which is observed by this activity
        }
    }
    void initRecipeListRV() {
        if(!(binding.recipeListRV.getAdapter() instanceof RecipeListRVAdapter)) {
            binding.recipeListRV.setLayoutManager(new LinearLayoutManager(this));
            binding.recipeListRV.setHasFixedSize(true);
            final RecipeListRVAdapter adapter = new RecipeListRVAdapter(vmRecipeList.error, new RecipeListRVAdapter.IOnClickListener() {
                @Override
                public void onClick(@NonNull View v, @NonNull Recipe recipe) {
                    //This way we wouldn't need this setTag/getTag smart solution
                    //This is possible only with data binding.
                    Toast.makeText(RecipeListActivity.this, "Recipe " + recipe.title, Toast.LENGTH_SHORT).show();
                    openRecipeDetailsActivity(recipe);
                }
            });
            binding.recipeListRV.setAdapter(adapter);
        }
    }
    void openRecipeDetailsActivity(Recipe recipe) {
        saveQueryStringInSharedPreferences(vmRecipeList.queryString.getValue(),recipe.recipeId);
        Intent i = new Intent(this,RecipeDetailsActivity.class);
        i.putExtra(RecipeDetailsActivity.EXTRA_RECIPEID,recipe.recipeId);
        startActivity(i);
    }
    void initSavedQueryRV() {
        if(!(binding.recipeListRV.getAdapter() instanceof SavedQueryListRVAdapter)) {
            binding.recipeListRV.setLayoutManager(new LinearLayoutManager(this));
            binding.recipeListRV.setHasFixedSize(true);
            final SavedQueryListRVAdapter adapter = new SavedQueryListRVAdapter(vmRecipeList.selectedSavedQuery);
            binding.recipeListRV.setAdapter(adapter);
            vmRecipeList.onRetrieveSavedQueriesRequest();
        }
    }
    @Override
    public void onBackPressed() {
        //When the current page is displaying the recipe list, we should go back to the saved queries list
        //The user can only quit the application with back buttons only from the saved query list view
        if(binding.recipeListRV.getAdapter() instanceof RecipeListRVAdapter) {
//            vmRecipeList.clearRecipeList();
            initSavedQueryRV();
        } else super.onBackPressed();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.savedQueriesMenu:
//                vmRecipeList.clearRecipeList();
                initSavedQueryRV();
                break;
            case R.id.showFavoritesOnlyMenu:
                //TODO: implement showFavorite Recipes Only
                break;
            case R.id.aboutMenu:
                //TODO: implement about box
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void saveQueryStringInSharedPreferences(String queryString,String recipeId) {
        SharedPreferences.Editor editor = this.getPreferences(Context.MODE_PRIVATE).edit();
        if(queryString == null || queryString.isEmpty()) editor.remove(QUERY_STRING);
        else {
            editor.putString(QUERY_STRING,queryString);
            editor.putString(RECIPE_ID,recipeId);
        }
        editor.apply();
    }
    private String getSavedQueryStringFromSharedPreferences() {
        return getPreferences(Context.MODE_PRIVATE).getString(QUERY_STRING,null);
    }
    private String getSavedRecipeIdFromSharedPreferences() {
        return getPreferences(Context.MODE_PRIVATE).getString(RECIPE_ID,null);
    }
}
