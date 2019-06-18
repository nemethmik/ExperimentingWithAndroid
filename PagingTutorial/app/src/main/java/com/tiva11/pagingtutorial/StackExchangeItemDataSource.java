package com.tiva11.pagingtutorial;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PageKeyedDataSource;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StackExchangeItemDataSource extends PageKeyedDataSource<Integer,StackExchangeAnswers.Item> {
    public static final int PAGE_SIZE = 10;
    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, StackExchangeAnswers.Item> callback) {
        StackExchangeCient.getStackExchangeAPI().getAnswers(1,PAGE_SIZE,StackExchangeCient.STACKOVERFLOW)
                .enqueue(new Callback<StackExchangeAnswers>() {
                    @Override
                    public void onResponse(Call<StackExchangeAnswers> call, Response<StackExchangeAnswers> response) {
                        if(response.body() != null) {
                            Log.d("MIKI","Load initial " + response.body().items.size());
                            callback.onResult(response.body().items,null,2);
                        }
                    }
                    @Override
                    public void onFailure(Call<StackExchangeAnswers> call, Throwable t) {

                    }
                });
    }

    @Override
    public void loadBefore(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, StackExchangeAnswers.Item> callback) {
        StackExchangeCient.getStackExchangeAPI().getAnswers(params.key,PAGE_SIZE,StackExchangeCient.STACKOVERFLOW)
                .enqueue(new Callback<StackExchangeAnswers>() {
                    @Override
                    public void onResponse(Call<StackExchangeAnswers> call, Response<StackExchangeAnswers> response) {
                        if(response.body() != null) {
                            Integer key = params.key > 1 ? params.key - 1 : null;
                            Log.d("MIKI","Load before " + params.key);
                            callback.onResult(response.body().items,key);
                        }
                    }
                    @Override
                    public void onFailure(Call<StackExchangeAnswers> call, Throwable t) {

                    }
                });

    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, StackExchangeAnswers.Item> callback) {
        StackExchangeCient.getStackExchangeAPI().getAnswers(params.key,PAGE_SIZE,StackExchangeCient.STACKOVERFLOW)
                .enqueue(new Callback<StackExchangeAnswers>() {
                    @Override
                    public void onResponse(Call<StackExchangeAnswers> call, Response<StackExchangeAnswers> response) {
                        if(response.body() != null) {
                            Integer key = response.body().has_more ? params.key + 1 : null;
                            Log.d("MIKI","Load after " + params.key);
                            callback.onResult(response.body().items,key);
                        }
                    }
                    @Override
                    public void onFailure(Call<StackExchangeAnswers> call, Throwable t) {

                    }
                });

    }
    public static class Factory extends DataSource.Factory {
        private MutableLiveData<PageKeyedDataSource<Integer,StackExchangeAnswers.Item>> mLiveData = new MutableLiveData<>();
        @Override
        public DataSource create() {
            StackExchangeItemDataSource ds = new StackExchangeItemDataSource();
            mLiveData.postValue(ds);
            return ds;
        }

        public MutableLiveData<PageKeyedDataSource<Integer, StackExchangeAnswers.Item>> getLiveData() {
            return mLiveData;
        }
    }
    public static class ViewModel extends android.arch.lifecycle.ViewModel {
        LiveData<PagedList<StackExchangeAnswers.Item>> mPagedList;
        LiveData<PageKeyedDataSource<Integer,StackExchangeAnswers.Item>> mDataSource;

        public ViewModel() {
            Factory f = new Factory();
            mDataSource = f.getLiveData();
            PagedList.Config cfg = (new PagedList.Config.Builder())
                    .setEnablePlaceholders(false)
                    .setPageSize(StackExchangeItemDataSource.PAGE_SIZE)
                    .build();
            mPagedList = (new LivePagedListBuilder(f,cfg)).build();
        }
    }
}
