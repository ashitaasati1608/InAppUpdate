package com.ashita.inappupdate.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ashita.inappupdate.model.NewsArticle

/**
 * Dao interface for Profile
 * contains all the queries related to profile
 */
@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNews(article: List<NewsArticle>)

    @Query("SELECT * from NewsArticle")
    fun getNews(): List<NewsArticle>
}
