package com.ashita.inappupdate.model

import retrofit2.Response
import retrofit2.http.GET

interface NewsService {
    @GET("news.json")
    suspend fun getNews(): Response<List<NewsArticle>>
}