package com.example.cs492finalproject.data

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface SteamService {
    @GET("ISteamNews/GetNewsForApp/v0002")
    suspend fun getNewsForApp(
        @Query("appid") appid: Long,
        @Query("count") count: Int,
        @Query("maxLength") maxLength: Int,
    ): Response<NewsResponse>

    @GET("ISteamUser/GetPlayerSummaries/v0002")
    suspend fun getPlayerSummaries(
        @Query("key") key: String,
        @Query("steamids") steamids: Long
    ): Response<PlayerSummariesResponse>

    @GET("IPlayerService/GetOwnedGames/v0001")
    suspend fun getOwnedGames(
        @Query("key") key: String,
        @Query("steamid") steamid: Long,
        @Query("include_appinfo") includeAppInfo: Boolean = true,
        @Query("include_played_free_games") includePlayedFreeGames: Boolean = true
    ): Response<GamesResponse>

    companion object {
        private const val BASE_URL = "https://api.steampowered.com/"
        fun create(): SteamService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(SteamService::class.java)
        }
    }
}