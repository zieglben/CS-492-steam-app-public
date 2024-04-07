package com.example.cs492finalproject

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.FlowRowScopeInstance.align
//import androidx.compose.foundation.layout.FlowRowScopeInstance.align
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.cs492finalproject.data.NewsItemData
import java.util.Date
import coil.compose.AsyncImage

@Composable
fun NewsScreen(viewModel: SteamViewModel)
{
    val newsResults = viewModel.newsResults.observeAsState()
    val newsLoading = viewModel.newsLoading.observeAsState()
    val games = viewModel.games.observeAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(10.dp),
            text = "News",
            style = MaterialTheme.typography.titleLarge
        )

        if (newsLoading.value != true) {
            if (newsResults.value.isNullOrEmpty()) {
                Text("No News Found", modifier = Modifier.align(Alignment.CenterHorizontally))
                Text("Favorite Some Games On Your Profile!", modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn() {
                    items(
                        items = newsResults.value ?: listOf(),
                        key = { data -> data.gid }) { item ->
                        NewsItem(item)
                    }
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
fun NewsItem(data: NewsItemData)
{
    Surface(
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 2.dp
    ) {
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp)
                .clickable(onClick = {
                    val browseNewsIntent = Intent(Intent.ACTION_VIEW)
                    browseNewsIntent.setData(Uri.parse(data.url))
                    ContextCompat.startActivity(context, browseNewsIntent, null)
                }),
        ) {
/*            Icon(
                imageVector = Icons.Filled.ExitToApp,
                contentDescription = "",
                modifier = Modifier
                    .padding(10.dp)
            )*/
            Text(data.title, fontWeight = FontWeight.Bold)

            Row() {
                AsyncImage(
                    model = "https://steamcdn-a.akamaihd.net/steam/apps/"+ data.appid + "/header.jpg",
                    contentDescription = "",
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp)),
                )
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ){
                    Text(
                        data.feedlabel,
                        modifier = Modifier
                            .align(Alignment.End)
                    )
                    Text(
                        Date(data.date * 1000).toString().substring(4,11)+ Date(data.date * 1000).toString().substring(24,28),
                        modifier = Modifier
                            .align(Alignment.End)

                    )
                }

            }
        }
    }
}