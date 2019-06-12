package com.tiva11.pagingtutorial;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IStackExchangeAPI {
    @GET("answers")
    Call<StackExchangeAnswers> getAnswers(
            @Query("page") int page,
            @Query("pagesize") int pagesize,
            @Query("site") String site
    );
}

