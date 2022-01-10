package com.example.spaceexplorer.remote;

import com.example.spaceexplorer.util.LiveDataCallAdapterFactory
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceFactory {

    fun makePhotoService(isDebug: Boolean): PhotoService {
        val okHttpClient = makeOkHttpClient(
            makeLoggingInterceptor(isDebug)
        )
        return makePhotoService(okHttpClient, makeGson())
    }

    fun makeFavouriteService(isDebug: Boolean): DjangoService {
        val okHttpClient = makeOkHttpClient(
            makeLoggingInterceptor(isDebug)
        )
        return makeFavouriteService(okHttpClient, makeGson())
    }

    fun makeLoginService(isDebug: Boolean): LoginService {
        val okHttpClient = makeOkHttpClient(
            makeLoggingInterceptor(isDebug)
        )
        return makeLoginService(okHttpClient, makeGson())
    }

    fun makeRegisterService(isDebug: Boolean): RegisterService {
        val okHttpClient = makeOkHttpClient(
            makeLoggingInterceptor(isDebug)
        )
        return makeRegisterService(okHttpClient, makeGson())
    }

    private fun makePhotoService(okHttpClient: OkHttpClient, gson: Gson): PhotoService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/")//10.0.51.200
            .client(okHttpClient)
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(PhotoService::class.java)
    }

    private fun makeFavouriteService(okHttpClient: OkHttpClient, gson: Gson): DjangoService {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000")//10.0.51.200
            .client(okHttpClient)
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(DjangoService::class.java)
    }

    private fun makeLoginService(okHttpClient: OkHttpClient, gson: Gson): LoginService {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000")//10.0.51.200
            .client(okHttpClient)
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(LoginService::class.java)
    }

    private fun makeRegisterService(okHttpClient: OkHttpClient, gson: Gson): RegisterService {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000")//10.0.51.200
            .client(okHttpClient)
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(RegisterService::class.java)
    }


    private fun makeOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    private fun makeGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }

    private fun makeLoggingInterceptor(isDebug: Boolean): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = if (isDebug)
            HttpLoggingInterceptor.Level.BODY
        else
            HttpLoggingInterceptor.Level.NONE
        return logging
    }
}