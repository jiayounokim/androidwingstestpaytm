package com.example.wingstestpaytm;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServiceBuilder {

    private static Retrofit retrofit = null;
    static HttpLoggingInterceptor logger = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);


    static OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logger).build();

    public static ApiService getService() {

        if (retrofit == null){

        retrofit = new Retrofit.Builder()
                .baseUrl("http://46.101.192.46/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }

        return retrofit.create(ApiService.class);
    }
}
