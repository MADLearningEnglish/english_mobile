package com.mit.learning_english.di.qualifier

import javax.inject.Qualifier

/**
 * Qualifier để phân biệt Retrofit dùng cho AuthApiService
 * 
 * Retrofit này sử dụng OkHttpClient không có Authenticator
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofit
