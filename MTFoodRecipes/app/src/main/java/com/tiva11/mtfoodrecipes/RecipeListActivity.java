package com.tiva11.mtfoodrecipes;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.tiva11.food2fork.Food2Fork;
import com.tiva11.food2fork.GetRecipeResponse;
import com.tiva11.food2fork.SearchRecipesResponse;

public class RecipeListActivity extends BaseActivity implements Food2Fork.IGetRecipeResponse {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
//        askInternetPermission();
        findViewById(R.id.buttonShowProgressBar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar(true);
                testSearchRecipesAsync();
//                testQueryRecipesAsync(); //The progressBar is not shown with this call, so just use the search.
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
    boolean isInternetPermissionGranted(boolean showToasMessage) {
        if(ContextCompat.checkSelfPermission(RecipeListActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            showProgressBar(false);
            if(showToasMessage) {
                Toast.makeText(RecipeListActivity.this, "Oops INTERNET permission not granted", Toast.LENGTH_LONG).show();
            }
//            askInternetPermission();
            return false;
        } else return true;
    }

    @Override
    public void onResponseGetRecipe(GetRecipeResponse response, String recipeId) {
        showProgressBar(false);
        Toast.makeText(RecipeListActivity.this, "Recipe is " + response.recipe.title, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailureGetRecipe(Throwable t, String recipeId) {
        showErrorMessage(t);
    }
    void showErrorMessage(Throwable t) {
        showProgressBar(false);
        Toast.makeText(RecipeListActivity.this, "Oops " + t.toString() + ":" + t.getMessage(), Toast.LENGTH_LONG).show();
    }
    //The permission request box never opens for Internet access, weird
//    final static int PERMISSION_REQUEST_CODE = 1960;
//    void askInternetPermission() {
//        if (ContextCompat.checkSelfPermission(RecipeListActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.INTERNET)) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("Request Permission")
//                        .setMessage("Internet access is needed to access Food2Fork")
//                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                ActivityCompat.requestPermissions(RecipeListActivity.this, new String[]{Manifest.permission.INTERNET}, PERMISSION_REQUEST_CODE);
//                            }
//                        });
//                builder.create().show();
//
//            } else {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PERMISSION_REQUEST_CODE);
//            }
//        }
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if(requestCode == PERMISSION_REQUEST_CODE) {
//            String m = "Oh oh, Internet permission denied \uD83E\uDD14";
//            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                m = "Cool, Internet permission denied \uD83D\uDE0A";
//            }
//            Toast.makeText(RecipeListActivity.this,m,Toast.LENGTH_SHORT).show();
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
}
