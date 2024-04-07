package com.example.cs492finalproject

import android.content.Context
import android.media.Image
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.View
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.util.DebugLogger
import com.example.cs492finalproject.data.Game


@Composable
fun ProfileScreen(viewModel: SteamViewModel) {
    val playerSummary = viewModel.summary.observeAsState()
    val games = viewModel.games.observeAsState()
    val playerDataLoading = viewModel.playerDataLoading.observeAsState()

    Column {
        if (playerDataLoading.value != true) {
            Surface (
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 2.dp
            ){
                //User Profile
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ){
                    //User avatar
                    AsyncImage(
                        model = playerSummary.value?.avatarFull,
                        contentDescription = "",
                        modifier = Modifier.width(100.dp),
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    //User name
                    Text(
                        text = playerSummary.value?.personaname ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(alignment = Alignment.CenterVertically)
                            .padding(horizontal = 25.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(30.dp))
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp),
                text = "Games",
                style = MaterialTheme.typography.titleLarge
            )

            val preferencesManager = PreferencesManager(LocalContext.current)
            //Game lists
            LazyColumn() {
                itemsIndexed(
                    items = games.value ?: listOf(),
                    key = { _, game -> game.appid }) { index, game ->
                    GameCard(
                        game,
                        onToggle = { viewModel.favoriteGame(preferencesManager, index, it) })
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

}

@Composable
fun GameCard(game: Game, onToggle: (Boolean) -> Unit) {
    // (3/18/2024) Recomposition solution (the next line) adapted from https://stackoverflow.com/a/76680816
    val checked = remember { mutableStateOf(game.favorited) }

    Surface (
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 2.dp
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconToggleButton(checked = checked.value, onCheckedChange = {
                checked.value = checked.value.not()
                onToggle(checked.value)
                Log.d("ProfileScreen", game.favorited.toString())
            }) {
                Icon(
                    imageVector = if (game.favorited) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = ""
                )
            }

            //Game cover
            AsyncImage(
                model = "https://steamcdn-a.akamaihd.net/steam/apps/"+ game.appid + "/header.jpg",
                contentDescription = "",
                modifier = Modifier.width(100.dp),
            )

            Spacer(modifier = Modifier.width(5.dp))

            //Game title and playtime
            Column {
                Text(text = game.name, fontWeight = FontWeight.Bold, lineHeight = 16.sp)
                Text(text = (game.playtime / 60).toString() + " hours")
            }
        }
    }
}