package com.tmsfalcon.device.tmsfalcon.activities.test

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object TestApiClient {
    const val BASE_URL = "https://api3.getresponse360.pl/"
    private var retrofit: Retrofit? = null

    // connect timeout
    // write timeout
    // read timeout
    val client: Retrofit?
        get() {


            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            builder.connectTimeout(5, TimeUnit.MINUTES) // connect timeout
                    .writeTimeout(5, TimeUnit.MINUTES) // write timeout
                    .readTimeout(5, TimeUnit.MINUTES) // read timeout


            builder.addInterceptor(interceptor)

            val gson: Gson = GsonBuilder()
                    .setLenient()
                    .create()
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(builder.build())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build()
            }
            return retrofit
        }
}