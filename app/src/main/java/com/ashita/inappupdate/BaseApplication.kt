package com.ashita.inappupdate

import android.app.Application
import android.content.Context
import com.ashita.inappupdate.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BaseApplication : Application() {
    init {
        INSTANCE = this
    }

    companion object {
        private lateinit var INSTANCE: BaseApplication

        val applicationContext: Context
            get() {
                return INSTANCE.applicationContext
            }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // declare Android context
            androidContext(this@BaseApplication)
            // declare modules to use
            modules(
                listOf(
                    apiModule, databaseModule, repositoryModule, viewModelModule,
                    networkModule
                )
            )
        }
    }
}