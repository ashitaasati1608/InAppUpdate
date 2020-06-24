package com.ashita.inappupdate.di

import com.ashita.inappupdate.model.NewsService
import org.koin.dsl.module
import retrofit2.Retrofit


val apiModule = module {

    fun provideNewsApi(retrofit: Retrofit): NewsService {
        return retrofit.create(NewsService::class.java)
    }
    single { provideNewsApi(get()) }

}