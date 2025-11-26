package com.example.juicy.network;

import com.example.juicy.Interface.DambJuiceApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;

    private RetrofitClient() { }

    public static DambJuiceApi getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(DambJuiceApi.class);
    }

    public static String getBaseUrl() {
        return ApiConfig.BASE_URL;
    }
}
