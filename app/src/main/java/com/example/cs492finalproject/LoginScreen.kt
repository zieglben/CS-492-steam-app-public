package com.example.cs492finalproject

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(onClick: () -> Unit){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "Steam Library News",
                modifier = Modifier.padding(bottom = 16.dp),
                style = MaterialTheme.typography.headlineLarge
            )
            Button(
                onClick = onClick,
                modifier = Modifier
                    .padding(16.dp)
                    .size(200.dp, 60.dp)
            ) {
                Text(text = "Log-in With Steam")
            }
        }
        Text(
            text = "*This app is not affiliated with Valve*",
            modifier = Modifier.align(Alignment.BottomCenter),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

