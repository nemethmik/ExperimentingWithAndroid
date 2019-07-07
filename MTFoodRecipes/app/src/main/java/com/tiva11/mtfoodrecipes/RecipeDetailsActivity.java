package com.tiva11.mtfoodrecipes;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.tiva11.food2fork.Recipe;
import com.tiva11.food2fork.VMRecipeList;
import com.tiva11.mtfoodrecipes.databinding.ActivityRecipeDetailsBinding;
import com.tiva11.mtfoodrecipes.databinding.ActivityRecipeListBinding;

import java.util.Date;

public class RecipeDetailsActivity extends AppCompatActivity {
    public static String EXTRA_RECIPEID = "recipe";
    ActivityRecipeDetailsBinding binding;
    private VMRecipeList vmRecipeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vmRecipeList = ViewModelProviders.of(this).get(VMRecipeList.class);
        binding = ActivityRecipeDetailsBinding.inflate(getLayoutInflater());
        binding.setLifecycleOwner(this); //THIS IS TERRIBLY IMPORTANT for Live Data Binding
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.setRecipe(vmRecipeList.recipe);
        binding.setUI(this);
        if(getIntent().hasExtra(EXTRA_RECIPEID)) {
            String recipeId = getIntent().getStringExtra(EXTRA_RECIPEID);
            vmRecipeList.onGetDemoRecipeRequest();
            Toast.makeText(this, "Recipe ID " + recipeId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "NO RECIPE ID", Toast.LENGTH_SHORT).show();
        }
    }
    public void onFavoriteFABClicked(final Recipe recipe) {
        recipe.isFavorite = !recipe.isFavorite;
        //If this doesn't work as expected, you forgot to call binding.setLifecycleOwner(this)
        vmRecipeList.recipe.setValue(recipe);
//        binding.invalidateAll(); //This is needed when binding POJOs
        Snackbar.make(binding.fab, "Recipe " + recipe.title + " is favorited", Snackbar.LENGTH_LONG)
            .setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recipe.isFavorite = !recipe.isFavorite;
                    vmRecipeList.recipe.setValue(recipe);
//                        binding.invalidateAll();
                }
            }).show();
    }
}
