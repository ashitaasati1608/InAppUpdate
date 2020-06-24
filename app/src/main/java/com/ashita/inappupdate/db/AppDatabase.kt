package com.ashita.inappupdate.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ashita.inappupdate.model.NewsArticle

/**
 * Database for App
 */
@Database(
    entities = [NewsArticle::class],
    version = AppDatabaseVersion.latestVersion
)
abstract class AppDatabase : RoomDatabase() {
    abstract val newsDao: NewsDao
}