package com.mit.learning_english.di

import com.mit.learning_english.data.security.EncryptionService
import com.mit.learning_english.data.security.KeystoreEncryptionService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module cung cấp các dependencies liên quan đến bảo mật
 * 
 * Sử dụng @Binds thay vì @Provides vì:
 * - KeystoreEncryptionService đã có @Inject constructor
 * - @Binds hiệu quả hơn khi chỉ cần bind interface với implementation
 * - Không cần tạo instance thủ công
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {

    /**
     * Bind EncryptionService interface với KeystoreEncryptionService implementation
     * 
     * Khi một class cần EncryptionService, Hilt sẽ tự động inject KeystoreEncryptionService
     */
    @Binds
    @Singleton
    abstract fun bindEncryptionService(
        keystoreEncryptionService: KeystoreEncryptionService
    ): EncryptionService
}
