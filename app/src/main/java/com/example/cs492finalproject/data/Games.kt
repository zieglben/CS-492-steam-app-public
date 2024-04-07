package com.example.cs492finalproject.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Game(
    val appid: Long,
    val name: String,
    @Json(name="img_icon_url") val iconUrl: String,
    @Json(name="playtime_forever") val playtime: Int,
    @Json(ignore=true) var favorited: Boolean = false
)
@JsonClass(generateAdapter = true)
data class Games(
    @Json(name = "game_count") val gameCount: Int,
    val games: List<Game>
)

@JsonClass(generateAdapter = true)
data class GamesResponse(
    val response: Games
)