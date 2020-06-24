package com.ashita.inappupdate.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import io.reactivex.annotations.NonNull

@Entity(tableName = "newsArticle")
data class NewsArticle(
    @PrimaryKey
    @NonNull
    val title: String,
    val author: String? = null,
    val description: String? = null,
    val url: String? = null,
    @SerializedName("imageUrl")
    val urlToImage: String? = null,
    val publishedAt: String? = null
)