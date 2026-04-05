package com.mit.learning_english.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mit.learning_english.data.local.entity.Lesson

// TODO: Add entities to the entities array, e.g., entities = [User::class]
@Database(entities = [Lesson::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    // TODO: Add abstract DAO functions here, e.g., abstract fun userDao(): UserDao
}
