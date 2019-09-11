package com.example.bakingapp.Network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String ROOT_URL="https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";
    /**
     * Get Retrofit Instance
     */

    private static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .addConverterFactory( GsonConverterFactory.create())
                .build();
    }

    /**
     * Get API Service
     *
     * @return API Service
     */
    public static apiInterface getApiService() {
        return getRetrofitInstance().create(apiInterface.class);
    }}
