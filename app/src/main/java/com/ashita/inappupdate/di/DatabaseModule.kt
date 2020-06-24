package com.ashita.inappupdate.di

import android.app.Application
import androidx.room.Room
import com.ashita.inappupdate.db.AppDatabase
import com.ashita.inappupdate.db.NewsDao
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "news.db")
            .build()
    }

    fun provideDao(database: AppDatabase): NewsDao {
        return database.newsDao
    }

    single { provideDatabase(androidApplication()) }
    single { provideDao(get()) }
}