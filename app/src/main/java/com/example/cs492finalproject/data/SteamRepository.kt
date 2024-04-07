package com.example.cs492finalproject.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SteamRepository(
    private val service: SteamService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun loadNewsForApp(appid: Long) : Result<List<NewsItemData>> =
        withContext(ioDispatcher) {
            try {
                val response = service.getNewsForApp(appid, 20, 300)
                if (response.isSuccessful) {
                    Result.success(response.body()?.appnews?.newsitems ?: listOf())
                } else {
                    Result.failure(Exception(response.errorBody()?.string()))
                }
            } catch(e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun loadPlayerSummary(api_key: String, steamid: Long): Result<PlayerSummary?> =
        withContext(ioDispatcher) {
            try {
                val response = service.getPlayerSummaries(api_key, steamid)
                if (response.isSuccessful) {
                    Result.success(response.body()?.response?.players?.get(0))
                } else {
                    Result.failure(Exception(response.errorBody()?.string()))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    suspend fun loadPlayerGames(api_key: String, steamid: Long): Result<List<Game>> =
        withContext(ioDispatcher) {
            try {
                val response = service.getOwnedGames(api_key, steamid)
                if (response.isSuccessful) {
                    Result.success(response.body()?.response?.games ?: listOf())
                } else {
                    Result.failure(Exception(response.errorBody()?.string()))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}