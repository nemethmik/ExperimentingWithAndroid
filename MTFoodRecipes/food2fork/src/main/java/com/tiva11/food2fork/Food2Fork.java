package com.tiva11.food2fork;

import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.gson.GsonBuilder;

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
    public static final int TIMEOUTINSECONDS = 10;
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
    private final static String BASE_URL = "https://www.food2fork.com/api/";
    private final static String API_KEY = "ab2a9a71b650f8081ed259c7c2a4e13c";
    private static Retrofit httpClient = new Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(
                    new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
            )).build();
    private static IFood2ForkAPI api = httpClient.create(IFood2ForkAPI.class);
    /*
    Warning! Make sure to add INTERNET permission, otherwise this call crashes the addon without any message.
     */
    public static void searchRecipesAsync(final String queryString, final int page,
                                          @NonNull final MutableLiveData<List<Recipe>> mldRecipeList,
                                          @NonNull final MutableLiveData<Throwable> mldError) {
        try {
            Call<SearchRecipesResponse> call = api.searchRecipes(API_KEY, queryString, page);
            call.enqueue(new Callback<SearchRecipesResponse>() {
                @Override
                public void onResponse(Call<SearchRecipesResponse> call, Response<SearchRecipesResponse> response) {
                    if(ok(response,mldError)) {
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
                    if(ok(response,mldError)) mldRecipeList.postValue(response.body().recipes);
                } catch (Throwable e) {
                    mldError.postValue(e);
                }
            }
        };
        final Future scheduledJob = executorServiceThreadPool.schedule(r,0, TimeUnit.SECONDS);
        executorServiceThreadPool.schedule(new Runnable() {
            @Override public void run() { scheduledJob.cancel(true); }
        },TIMEOUTINSECONDS,TimeUnit.SECONDS);
        //It tries to kill the worker thread in a couple of seconds
        //Actually, it really kills the job and an interrupt exception is sent to the error handler via the mldError
    }
    /*
    Warning! Make sure to add INTERNET permission, otherwise this call crashes the addon without any message.
     */
    public static void getRecipeAsync(final String recipeId,
                                      @NonNull final MutableLiveData<Recipe> mldRecipe,
                                      @NonNull final MutableLiveData<Throwable> mldError) {
        try {
            final Call<GetRecipeResponse> call = api.getRecipe(API_KEY, recipeId);
            call.enqueue(new Callback<GetRecipeResponse>() {
                @Override
                public void onResponse(Call<GetRecipeResponse> call, Response<GetRecipeResponse> response) {
                    if(ok(response,mldError)) mldRecipe.postValue(response.body().recipe);
                }
                @Override
                public void onFailure(Call<GetRecipeResponse> call, Throwable t) {
                    mldError.postValue(t);
                }
            });
            executorServiceThreadPool.schedule(new Runnable() {
                @Override public void run() { call.cancel(); }
            },TIMEOUTINSECONDS,TimeUnit.SECONDS);
            //It tries to kill the worker thread in a certain number of seconds

        } catch(Throwable t) {
            mldError.postValue(t);
        }
    }
    // Returns true when no error was found
    private static <T> boolean ok(@NonNull Response<T> response,@NonNull final MutableLiveData<Throwable> mldError) {
        if (response.isSuccessful() && response.body() != null) {
            return true;
        } else {
            if (response.errorBody() != null) {
                try { //It's interesting that errorBody().string is declared with throws IOException, funny
                    mldError.postValue(new Exception(response.errorBody().string()));
                } catch (IOException e) {
                    mldError.postValue(e);
                }
            } else mldError.postValue(new Exception("No error body in failed response"));
            return false;
        }
    }
}
