package com.ashita.inappupdate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import com.ashita.inappupdate.model.NewsRepository

class ListViewModel(application: Application, newsRepository: NewsRepository) : AndroidViewModel(application) {

    val newsArticles = newsRepository.getNewsArticles().asLiveData()

}