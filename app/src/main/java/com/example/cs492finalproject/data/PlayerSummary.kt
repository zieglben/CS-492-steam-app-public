package com.example.cs492finalproject.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlayerSummary(
    val personaname: String,
    @Json(name = "avatarfull") val avatarFull: String,
)

@JsonClass(generateAdapter = true)
data class PlayerSummaries(
    val players: List<PlayerSummary>
)

@JsonClass(generateAdapter = true)
data class PlayerSummariesResponse(
    val response: PlayerSummaries
)