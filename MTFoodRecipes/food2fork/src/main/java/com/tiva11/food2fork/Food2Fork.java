package com.tiva11.food2fork;

import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    //Originally, I thought that it is private and all API communication is completely encapsulated
    //in the singleton class Food2Fork, but the VM classes of the same package might have the right
    //and chance to do their own data retrievals. But it is not needed, the VM just sends its mutable
    // live object (MLD) here and Food2Fork will feed the data into that MLD
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
    @Deprecated
    public interface ISearchRecipesResponse {
        void onResponseSearchRecipes(SearchRecipesResponse response,String queryString,int page);
        void onFailureSearchRecipes(Throwable t,String queryString,int page);
    }
    @Deprecated
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
    @Deprecated
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
    public static void searchRecipesAsync(final String queryString, final int page,
                                          @NonNull final MutableLiveData<List<Recipe>> mldRecipeList,
                                          @NonNull final MutableLiveData<Throwable> mldError) {
        try {
            Call<SearchRecipesResponse> call = api.searchRecipes(API_KEY, queryString, page);
            call.enqueue(new Callback<SearchRecipesResponse>() {
                @Override
                public void onResponse(Call<SearchRecipesResponse> call, Response<SearchRecipesResponse> response) {
                    if(response.isSuccessful() && response.body() != null) {
                        if(page > 1) {
                            //This just adds more and more data to the list, it'' the job of the VM to clear the
                            //list when not needed.
                            List<Recipe> currentRecipes = mldRecipeList.getValue();
                            if(currentRecipes == null) currentRecipes = new ArrayList<>();
                            currentRecipes.addAll(response.body().recipes);
                            mldRecipeList.postValue(currentRecipes);
                        } else {
                            mldRecipeList.postValue(response.body().recipes);
                        }
                    } else {
                        try {
                            if(response.errorBody() != null) mldError.postValue(new Exception(response.errorBody().string()));
                            else mldError.postValue(new Exception("null errorBody in response"));
                        } catch(Exception e) {
                            mldError.postValue(e);
                        }
                    }
                }
                @Override
                public void onFailure(Call<SearchRecipesResponse> call, Throwable t) {
                    mldError.postValue(t);
                }
            });
        } catch(Throwable t) {
            mldError.postValue(t);
        }
    }
    /*
    * This version uses Retrofit's synchronous call within AsyncTask.
    * The parametrization of AsyncTask is really cumbersome
    * which cannot be a generic since then the Java compiler would complain about using a generic type in vararg.
    * The only benefit this would provide, that it doesn't crash on No internet permission error
    */
    @Deprecated
    public static void queryRecipesAsync(final String queryString, final int page, @NonNull final ISearchRecipesResponse onResponse){
        try {
            (new AsyncTask<Void,Void,SearchRecipesResponse>() {
                @Override
                protected SearchRecipesResponse doInBackground(Void... voids) {
                    try {
                        Call<SearchRecipesResponse> call = api.searchRecipes(API_KEY, queryString, page);
                        Response<SearchRecipesResponse> response = call.execute();
                        if(response.errorBody() != null) {
                            try {
                                return SearchRecipesResponse.createError(response.errorBody().string());
                            } catch (IOException ioExcaption) {
                                return SearchRecipesResponse.createError("errorBody.string IOException");
                            }
                        } else return response.body();
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
            }).execute((Void)null);
        } catch (Throwable e) {
            onResponse.onFailureSearchRecipes(e,queryString,page);
        }
    }
    private static ScheduledExecutorService executorServiceThreadPool = Executors.newScheduledThreadPool(3);
    //This is an alternative implementation using a Scheduled executor as suggested by MT in his video
    public static void searchRecipesWithExecutor(final String queryString, final int page,
                                                 @NonNull final MutableLiveData<List<Recipe>> mldRecipeList,
                                                 @NonNull final MutableLiveData<Throwable> mldError) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    Call<SearchRecipesResponse> call = api.searchRecipes(API_KEY, queryString, page);
                    Response<SearchRecipesResponse> response = call.execute();
                    if(response.errorBody() != null) {
                        try {
                            mldError.postValue(new Exception(response.errorBody().string()));
                        } catch (IOException ioException) {
                            mldError.postValue(new Exception("errorBody.string IOException",ioException));
                        }
                    } else {
                        if(response.body() != null) mldRecipeList.postValue(response.body().recipes);
                        else mldError.postValue(new Exception("Body is null in response"));
                    }
                } catch (Throwable e) {
                    mldError.postValue(e);
                }
            }
        };
        final Future scheduledJob = executorServiceThreadPool.schedule(r,0, TimeUnit.SECONDS);
        executorServiceThreadPool.schedule(new Runnable() {
            @Override public void run() { scheduledJob.cancel(true); }
        },5,TimeUnit.SECONDS); //It tries to kill the worker thread in 5 seconds
        //Actually, it really kills the job and an interrupt exception is sent to the error handler via the mldError
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
