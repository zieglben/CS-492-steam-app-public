package com.example.cs492finalproject

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getString
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs492finalproject.data.Game
import com.example.cs492finalproject.data.NewsItemData
import com.example.cs492finalproject.data.PlayerSummary
import com.example.cs492finalproject.data.SteamRepository
import com.example.cs492finalproject.data.SteamService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.net.URL

class SteamViewModel : ViewModel() {
    private val repository: SteamRepository = SteamRepository(SteamService.create())

    private val _steamId = MutableLiveData<Long?>(null)
    val steamId: LiveData<Long?> = _steamId

    private val _summary = MutableLiveData<PlayerSummary?>(null)
    val summary: MutableLiveData<PlayerSummary?> = _summary

    private val _games = MutableLiveData<List<Game>?>(null)
    val games: LiveData<List<Game>?> = _games

    private val _playerDataLoading = MutableLiveData<Boolean>(true)
    val playerDataLoading: LiveData<Boolean> = _playerDataLoading

    private val _newsResults = MutableLiveData<List<NewsItemData>?>(null)
    val newsResults: LiveData<List<NewsItemData>?> = _newsResults

    private val _newsLoading = MutableLiveData<Boolean>(true)
    val newsLoading: LiveData<Boolean> = _newsLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun setSteamId(id: Long, context: Context) {
        _steamId.value = id
        PreferencesManager(context).saveData("steamid", id.toString())
    }

    fun login(context: Context) {
        val prefId = PreferencesManager(context).getData("steamid", "-1")
        Log.d("SteamViewModel", "SteamID: $prefId")
        if (prefId != "-1") {
            _steamId.value = prefId.toLong()
            return
        }
        Log.d("SteamViewModel", "Starting Intent")


        // (3/17/2024) Login string adapted from: https://stackoverflow.com/a/30869787
        val openidURI = """
            https://steamcommunity.com/openid/login?
            openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select&
            openid.identity=http://specs.openid.net/auth/2.0/identifier_select&
            openid.mode=checkid_setup&
            openid.ns=http://specs.openid.net/auth/2.0&
            openid.realm=https://CS492FinalProject&
            openid.return_to=https://CS492FinalProject/login/
        """.trimIndent()

        val loginIntent = Intent(Intent.ACTION_VIEW)
        loginIntent.setData(Uri.parse(openidURI))
        startActivity(context, loginIntent, null)
    }

    fun loadPlayerData(api_key: String, preferencesManager: PreferencesManager) {
        _playerDataLoading.value = true

        viewModelScope.launch {
            val summaryResult = _steamId.value?.let { repository.loadPlayerSummary(api_key, it) }
            _summary.value = summaryResult?.getOrNull()
            _error.value = summaryResult?.exceptionOrNull()?.message

            if (!_error.value.isNullOrBlank())
                Log.e("SteamViewModel", _error.value ?: "null")

            val gamesResult = _steamId.value?.let { repository.loadPlayerGames(api_key, it) }
            _games.value = gamesResult?.getOrNull()?.sortedWith{data1, data2 -> data2.playtime - data1.playtime}
            _error.value = gamesResult?.exceptionOrNull()?.message

            val favoriteGamesStr = preferencesManager.getData("favoriteGames", "")

            if (favoriteGamesStr.isNotEmpty()) {
                val favoriteGames = favoriteGamesStr
                    .split(",")
                    .filter { it != "," }
                    .map { it.toLong() }
                _games.value = _games.value?.map { game ->
                    if (favoriteGames.contains(game.appid)) {
                        game.favorited = true
                    }
                    game
                }
            }

            if (!_error.value.isNullOrBlank())
                Log.e("SteamViewModel", _error.value ?: "null")

            _playerDataLoading.value = false
        }
    }

    fun loadNews(appids: List<Long>?) {
        if (_newsLoading.value != true) return

        _newsResults.value = listOf()

        // (3/18/2024) Use of async/awaitAll adapted from https://stackoverflow.com/a/74669974
        viewModelScope.launch {
            _newsResults.value = appids
                ?.map {
                    async {
                        val newsResult = repository.loadNewsForApp(it)
                        if (!newsResult.exceptionOrNull()?.message.isNullOrBlank())
                            Log.e("SteamViewModel", _error.value ?: "null")

                        newsResult.getOrNull() ?: listOf()
                    }
                }
                ?.awaitAll()
                ?.reduceOrNull {l1, l2 -> l1 + l2 }
                ?.distinctBy { it.gid }
                ?.sortedWith {data1, data2 -> (data2.date - data1.date).toInt() }

            _newsLoading.value = false
        }
    }

    fun favoriteGame(preferencesManager: PreferencesManager, gameIndex: Int, favorited: Boolean) {
        _games.value?.get(gameIndex)?.favorited = favorited

        val favoriteGamesStr = _games.value
            ?.filter {it.favorited}
            ?.map {it.appid.toString()}
            ?.reduceOrNull(){ str1, str2 -> "$str1,$str2" }

        preferencesManager.saveData("favoriteGames", favoriteGamesStr ?: "")
        _newsLoading.value = true
    }

    fun getFavoritedGames(): List<Long>? {
        return _games.value
            ?.filter { game -> game.favorited }
            ?.map { game -> game.appid }
    }
}