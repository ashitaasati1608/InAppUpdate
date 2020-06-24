package com.ashita.inappupdate.di

import com.ashita.inappupdate.viewmodel.ListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { ListViewModel(get(), get()) }
}