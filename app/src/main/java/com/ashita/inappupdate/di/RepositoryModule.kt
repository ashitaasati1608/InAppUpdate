package com.ashita.inappupdate.di

import com.ashita.inappupdate.db.NewsDao
import com.ashita.inappupdate.model.NewsRepository
import com.ashita.inappupdate.model.NewsService
import org.koin.dsl.module

val repositoryModule = module {

    fun provideNewsRepository(api: NewsService, dao: NewsDao): NewsRepository {
        return NewsRepository(api, dao)
    }
    single { provideNewsRepository(get(), get()) }
}