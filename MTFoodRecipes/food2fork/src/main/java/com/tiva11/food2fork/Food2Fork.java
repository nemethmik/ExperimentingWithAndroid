package com.tiva11.food2fork;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public abstract class Food2Fork {
    static class NotFoundException extends Exception {
        public NotFoundException(String message) {
            super(message);
        }
    }
    private interface IFood2ForkAPI {
        @GET("search")
        Call<SearchRecipesResponse> searchRecipes(
                @Query("key") String apiKey,
                @Query("q") String queryString,
                @Query("page") int page
        );
        @GET("get")
        Call<GetRecipeResponse> getRecipe(
                @Query("key") String apiKey,
                @Query("rId") String recipeId
        );
    }
    public interface ISearchRecipesResponse {
        void onResponseSearchRecipes(SearchRecipesResponse response,String queryString,int page);
        void onFailureSearchRecipes(Throwable t,String queryString,int page);
    }
    public interface IGetRecipeResponse {
        void onResponseGetRecipe(GetRecipeResponse response,String recipeId);
        void onFailureGetRecipe(Throwable t,String recipeId);
    }
    private final static String BASE_URL = "https://www.food2fork.com/api/";
    private final static String API_KEY = "ab2a9a71b650f8081ed259c7c2a4e13c";
    private static Retrofit httpClient = new Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build();
    private static IFood2ForkAPI api = httpClient.create(IFood2ForkAPI.class);
    /*
    Warning! Make sure to add INTERNET permission, otherwise this call crashes the addon without any message.
     */
    public static void searchRecipesAsync(final String queryString, final int page, @NonNull final ISearchRecipesResponse onResponse) {
        try {
            Call<SearchRecipesResponse> call = api.searchRecipes(API_KEY, queryString, page);
            call.enqueue(new Callback<SearchRecipesResponse>() {
                @Override
                public void onResponse(Call<SearchRecipesResponse> call, Response<SearchRecipesResponse> response) {
                    onResponse.onResponseSearchRecipes(response.body(), queryString, page);
                }
                @Override
                public void onFailure(Call<SearchRecipesResponse> call, Throwable t) {
                    onResponse.onFailureSearchRecipes(t, queryString, page);
                }
            });
        } catch(Throwable t) {
          onResponse.onFailureSearchRecipes(t,queryString,page);
        }
    }
    /*
    * This version uses Retrofit's synchronous call within AsyncTask.
    * The parametrization of AsyncTask is really cumbersome
    * which cannot be a generic since then the Java compiler would complain about using a generic type in vararg.
    * The only benefit this would provide, that it doesn't crash on No internet permission error
     */
    public static void queryRecipesAsync(final String queryString, final int page, @NonNull final ISearchRecipesResponse onResponse){
        try {
            (new AsyncTask<SearchRecipesResponse,Integer,SearchRecipesResponse>() {
                @Override
                protected SearchRecipesResponse doInBackground(SearchRecipesResponse... searchRecipesResponses) {
                    try {
                        Call<SearchRecipesResponse> call = api.searchRecipes(API_KEY, queryString, page);
                        Response<SearchRecipesResponse> response = call.execute();
                        if(response != null) {
                            if(response.errorBody() != null) {
                                try {
                                    return SearchRecipesResponse.createError(response.errorBody().string());
                                } catch (IOException ioExcaption) {
                                    return SearchRecipesResponse.createError("errorBody.string IOException");
                                }
                            } else return response.body();
                        } else {
                            return SearchRecipesResponse.createError("Response body is null in queryRecipesAsync");
                        }
                    } catch (Throwable e) {
                        return SearchRecipesResponse.createError(e.toString());
                    }
                }
                @Override
                protected void onPostExecute(SearchRecipesResponse response) {
                    if(response != null) {
                        if (response.error == null) {
                            onResponse.onResponseSearchRecipes(response, queryString, page);
                        } else {
                            onResponse.onFailureSearchRecipes(new Exception(response.error), queryString, page);
                        }
                    } else {
                        onResponse.onFailureSearchRecipes(new Exception("Null response in onPostExecute"), queryString, page);
                    }
                }
            }).execute((SearchRecipesResponse)null);
        } catch (Throwable e) {
            onResponse.onFailureSearchRecipes(e,queryString,page);
        }
    }
    /*
    Warning! Make sure to add INTERNET permission, otherwise this call crashes the addon without any message.
     */
    public static void getRecipeAsync(final String recipeId, @NonNull final IGetRecipeResponse onResponse) {
        try {
            Call<GetRecipeResponse> call = api.getRecipe(API_KEY, recipeId);
            call.enqueue(new Callback<GetRecipeResponse>() {
                @Override
                public void onResponse(Call<GetRecipeResponse> call, Response<GetRecipeResponse> response) {
                    onResponse.onResponseGetRecipe(response.body(), recipeId);
                }

                @Override
                public void onFailure(Call<GetRecipeResponse> call, Throwable t) {
                    onResponse.onFailureGetRecipe(t, recipeId);
                }
            });
        } catch(Throwable t) {
            onResponse.onFailureGetRecipe(t,recipeId);
        }
    }
}
