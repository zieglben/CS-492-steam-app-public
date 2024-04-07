package com.example.cs492finalproject.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.net.URL

@JsonClass(generateAdapter = true)
data class NewsItemData(
    val gid: Long,
    val title: String,
    val url: String,
    val date: Long,
    val appid: Long,
    val feedlabel: String,
)

@JsonClass(generateAdapter = true)
data class NewsItemsData(
    val newsitems: List<NewsItemData>,
)

@JsonClass(generateAdapter = true)
data class NewsResponse(
    val appnews: NewsItemsData
)