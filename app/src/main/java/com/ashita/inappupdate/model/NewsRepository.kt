package com.ashita.inappupdate.model

import com.ashita.inappupdate.core.Resource
import com.ashita.inappupdate.db.NewsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class NewsRepository(private var api: NewsService, private var db: NewsDao) {

    fun getNewsArticles(): Flow<Resource<List<NewsArticle>>> {
        return flow {
            emit(Resource.loading())

            emit(Resource.success(db.getNews()))

            // 2. Try fetching new news -> save + emit
            val profileSource = api.getNews()

            profileSource.body()?.let { db.insertNews(it) }

            //emit(Resource.success(profileSource.body()))
            emit(Resource.success(db.getNews()))
        }.flowOn(Dispatchers.IO)
    }
}