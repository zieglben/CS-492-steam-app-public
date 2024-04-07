package com.example.cs492finalproject

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.example.cs492finalproject.ui.theme.CS492FinalProjectTheme

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)
class MainActivity : ComponentActivity() {
    private val viewModel: SteamViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val CHANNEL_ID = "channel_id_example_01"
    private val notificationId = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        sendNotification()

        val preferencesManager = PreferencesManager(this)

        settingsViewModel.setDarkMode(preferencesManager.getData(
            "dark_theme", "false").toBoolean())
        Log.d("MainActivity", "Dark Mode set to: ${preferencesManager.getData(
            "dark_theme", "false")}")

        val ctx = this
        setContent {
            val isDarkMode by settingsViewModel.isDarkModeFlow.collectAsState(
                initial = preferencesManager.getData("dark_theme", "false").toBoolean()
            )
            val bottomBarState = remember { mutableStateOf(true) }
            CS492FinalProjectTheme(darkTheme = isDarkMode) {
                val items = listOf(
                    BottomNavigationItem(
                        title = "Profile",
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home,
                        hasNews = false,
                    ),
                    BottomNavigationItem(
                        title = "News",
                        selectedIcon = Icons.Filled.Email,
                        unselectedIcon = Icons.Outlined.Email,
                        hasNews = false,
                        badgeCount = 10
                    ),
//                    BottomNavigationItem(
//                        title = "Friends",
//                        selectedIcon = Icons.Filled.Home,
//                        unselectedIcon = Icons.Outlined.Home,
//                        hasNews = false,
//                    ),
                    BottomNavigationItem(
                        title = "Settings",
                        selectedIcon = Icons.Filled.Settings,
                        unselectedIcon = Icons.Outlined.Settings,
                        hasNews = true,
                    ),
                )
                var selectedItemIndex by rememberSaveable {
                    mutableStateOf(0)
                }
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background //Color(0xFFFF00FF)
                ) {
                    val navController = rememberNavController()
                    Scaffold(
                        bottomBar = {
                            if(bottomBarState.value){
                                NavigationBar {
                                    items.forEachIndexed { index, item ->
                                        NavigationBarItem(
                                            selected = selectedItemIndex == index,
                                            onClick = {
                                                selectedItemIndex = index
                                                navController.navigate(item.title)
                                            },
                                            icon = {
                                                Text(item.title)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    ) { padding ->
                        NavHost(
                            modifier = Modifier.padding(padding),
                            navController = navController,
                            startDestination = "Login",
                        ) {
                            composable("Profile") {
                                bottomBarState.value = true
                                LaunchedEffect(key1 = Unit){
                                    if (viewModel.playerDataLoading.value == true) {
                                        viewModel.loadPlayerData(
                                            getString(R.string.api_key),
                                            preferencesManager
                                        )
                                    }
                                }

                                ProfileScreen(viewModel)
                            }
                            composable("News") {
                                bottomBarState.value = true
                                LaunchedEffect(key1 = viewModel.getFavoritedGames().hashCode()) {
                                    viewModel.loadNews(viewModel.getFavoritedGames())
                                }
                                NewsScreen(viewModel)
                            }
                            composable("Friends") {
                                bottomBarState.value = true
                                FriendsCard()
                            }
                            composable("Settings") {
                                bottomBarState.value = true
                                SettingsScreen(
                                    preferencesManager = preferencesManager,
                                    settingsViewModel = settingsViewModel,
                                    navController = navController,
                                    intent = intent
                                )
                            }
                            composable("Login",
                                deepLinks = listOf(navDeepLink {
                                    uriPattern = "https://cs492finalproject/*"
                                })) {
                                bottomBarState.value = false

                                var id: Long =  PreferencesManager(LocalContext.current)
                                    .getData("steamid", "-1")
                                    .toLong()

                                if (id == -1L) {
                                    // openid.claimed_id should be of the form https://steamcommunity.com/openid/id/<steamid>
                                    id = intent.data
                                        ?.getQueryParameter("openid.claimed_id")
                                        ?.split("/")
                                        ?.last()
                                        ?.toLong()
                                        ?: -1L
                                }

                                Log.d("MainActivity", id.toString())

                                if (id != -1L) {
                                    viewModel.setSteamId(id, LocalContext.current)
                                    navController.navigate("Profile")
                                }

                                LoginScreen(onClick = { viewModel.login(ctx) })
                            }
                        }
                    }
                }
            }
        }
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun sendNotification(){
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("example title")
            .setContentText("example text")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val temp:Context = this
        with(NotificationManagerCompat.from(this)){
            }
            //notify(notificationId, builder.build())
    }

}