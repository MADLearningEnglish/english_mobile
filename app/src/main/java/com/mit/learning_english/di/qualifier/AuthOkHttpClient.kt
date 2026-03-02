package com.mit.learning_english.di.qualifier

import javax.inject.Qualifier

/**
 * Qualifier để phân biệt OkHttpClient dùng cho AuthApiService
 * 
 * OkHttpClient này không có Authenticator để tránh circular dependency
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthOkHttpClient
