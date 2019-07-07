package com.tiva11.mtfoodrecipes;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.tiva11.food2fork.Recipe;
import com.tiva11.mtfoodrecipes.databinding.ActivityRecipeDetailsBinding;
import com.tiva11.mtfoodrecipes.databinding.ActivityRecipeListBinding;

import java.util.Date;

public class RecipeDetailsActivity extends AppCompatActivity {
    ActivityRecipeDetailsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeDetailsBinding.inflate(getLayoutInflater());
//        setContentView(R.layout.activity_recipe_details);
        setContentView(binding.getRoot());
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(binding.toolbar);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        binding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Recipe is favorited", Snackbar.LENGTH_LONG)
//                        .setAction("UNDO", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                //TODO: Implement favorite undo
//                            }
//                        }).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Recipe r = new Recipe();
        r.title = "Cajun Spices";
        r.publisher = "Gourmet Central";
        r.favoriteTime = new Date();
        r.isFavorite = true;
        r.socialRank = 97.8;
        r.imageUrl = "https://assets3.thrillist.com/v1/image/1774322/size/tmg-article_default_mobile.jpg";
        binding.setRecipe(r);
        binding.setUI(this);
    }
    public void onFavoriteFABClicked(final Recipe recipe) {
        recipe.isFavorite = !recipe.isFavorite;
        binding.invalidateAll();
        Snackbar.make(binding.fab, "Recipe is favorited", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recipe.isFavorite = !recipe.isFavorite;
                        binding.invalidateAll();
                    }
                }).show();
    }
}
