package com.mit.learning_english.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mit.learning_english.data.local.network.NetworkMonitor
import com.mit.learning_english.data.mapper.ResultMapper
import com.mit.learning_english.data.remote.retrofit.okhttpclient.AuthInterceptor
import com.mit.learning_english.data.remote.retrofit.okhttpclient.TokenAuthenticator
import com.mit.learning_english.data.repository.NetworkRepositoryImpl
import com.mit.learning_english.di.qualifier.AuthOkHttpClient
import com.mit.learning_english.di.qualifier.AuthRetrofit
import com.mit.learning_english.domain.repository.NetworkRepository
import com.mit.learning_english.shared.Constant.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().setLenient().create()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    /**
     * Tạo OkHttpClient riêng cho AuthApiService (không có Authenticator)
     *
     * Điều này tránh circular dependency:
     * - Authenticator cần AuthApiService để refresh token
     * - AuthApiService cần Retrofit
     * - Retrofit cần OkHttpClient
     * - OkHttpClient có Authenticator -> Circular!
     *
     * Giải pháp: Tạo OkHttpClient riêng không có Authenticator cho AuthApiService
     */
    @Provides
    @Singleton
    @AuthOkHttpClient
    fun provideAuthOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor, authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor).connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).build()
    }

    /**
     * OkHttpClient chính cho các API khác (có Authenticator)
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS).build()
    }

    /**
     * Retrofit cho AuthApiService (dùng OkHttpClient không có Authenticator)
     */
    @Provides
    @Singleton
    @AuthRetrofit
    fun provideAuthRetrofit(
        @AuthOkHttpClient authOkHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL).client(authOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
    }

    /**
     * Retrofit chính cho các API khác (dùng OkHttpClient có Authenticator)
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitor {
        return NetworkMonitor(context)
    }

    @Provides
    @Singleton
    fun provideNetworkRepository(
        networkMonitor: NetworkMonitor
    ): NetworkRepository {
        return NetworkRepositoryImpl(networkMonitor = networkMonitor)
    }

    @Provides
    @Singleton
    fun provideDictionaryApiService(retrofit: Retrofit): com.mit.learning_english.data.remote.api.DictionaryApiService {
        return retrofit.create(com.mit.learning_english.data.remote.api.DictionaryApiService::class.java)
    }
}
