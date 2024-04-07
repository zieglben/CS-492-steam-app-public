package com.example.cs492finalproject

import android.media.Image
import android.os.Bundle
import android.text.Layout
import android.view.View
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment



class FriendsScreen : Fragment(){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}

@Preview
@Composable
fun FriendsCard() {
    Column {
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
                Image(
                    painter = painterResource(R.drawable.sprigatito),
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(100.dp)
                        .border(2.dp, MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(5.dp))
                //User name
                Text(
                    text = "Sprigatito",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
        Spacer(modifier = Modifier.width(30.dp))
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(10.dp),
            text = "Online",
            style = MaterialTheme.typography.titleLarge
        )
        showFriendsList()
        Spacer(modifier = Modifier.width(30.dp))
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(10.dp),
            text = "Offline",
            style = MaterialTheme.typography.titleLarge
        )
        showFriendsList()
    }
}
@Preview
@Composable
fun showFriendsList(){
    val names = listOf("friend 1", "friend 2", "friend 3")
    LazyColumn {
        items(names){name ->
            Text(name, modifier = Modifier.padding(16.dp))
            Divider()
        }
    }
}