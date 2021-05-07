package com.tmsfalcon.device.tmsfalcon.customtools;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zabius on 9/8/17.
 */

public class RestClient {

    /*public static String BASE_URL = "https://tmsfalcon.com/api-v1/driver/login/";*/
    public static String BASE_URL = UrlController.BASE+"login/";
    private static Retrofit retrofit = null;
    private static final String TAG = "RestClient";
    private static AppServices REST_CLIENT = null;

    public static AppServices get() {
        REST_CLIENT = getClient().create(AppServices.class);
        return REST_CLIENT;
    }

    static Gson gson = new GsonBuilder()
            .setLenient()
            .serializeNulls()
            .disableHtmlEscaping()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public static Retrofit getClient() {

//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                                                .connectTimeout(5, TimeUnit.MINUTES)
                                                .readTimeout(5, TimeUnit.MINUTES)
                                                .writeTimeout(5, TimeUnit.MINUTES)
                                               // .addInterceptor(new ChuckInterceptor(AppController.getInstance()))
                                                .retryOnConnectionFailure(false);
//         httpClient.addInterceptor(logging);

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                   // .addConverterFactory(ScalarsConverterFactory.create()) //to prevent adding scaping characters to multipart string
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
}
