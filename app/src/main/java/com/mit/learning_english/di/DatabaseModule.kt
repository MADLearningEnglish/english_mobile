package com.mit.learning_english.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.mit.learning_english.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Extension property để tạo DataStore instance
// Tên "settings" là tên file preferences sẽ được lưu trong device
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module // Đây là annotation để báo với Hilt đây là nơi cung cấp đối tượng (dependency). Hilt sẽ tìm trong này khi cần tạo một instance nào đó
@InstallIn(SingletonComponent::class) // xác định phạm vi của các đối tượng trong module. SingletonComponent có nghĩa là phạm vi là toàn cục sống cung với Application.
object DatabaseModule {

    @Provides // Đánh dấy provideAppDatabase là 1 function cung cấp, khi cần instance của AppDatabase thì sẽ gọi function này.
    @Singleton // AppDatabase chỉ được tạo một lần duy nhất trong suốt vòng đời ứng dụng. Các lần gọi sau sẽ trả về instance đã tạo trước đó.
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "learning_english.db"
        )
        .fallbackToDestructiveMigration() // Khi version database thay đổi mà chưa viết migration, room sẽ xóa toàn bộ dữ liệu cũ.
        // Trong khi lên môi trường production không nên dùng.
        .build()
    }

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}
