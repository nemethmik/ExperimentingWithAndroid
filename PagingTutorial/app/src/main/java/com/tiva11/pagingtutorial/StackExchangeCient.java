package com.tiva11.pagingtutorial;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StackExchangeCient {
    private static final String BASE_URL = "https://api.stackexchange.com/2.2/";
    private static Retrofit httpClient = new Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build();
    public static final String STACKOVERFLOW = "stackoverflow";
    public static IStackExchangeAPI getStackExchangeAPI() {
        return httpClient.create(IStackExchangeAPI.class);
    }
}
