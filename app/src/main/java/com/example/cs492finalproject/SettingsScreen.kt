package com.example.cs492finalproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.cs492finalproject.ui.theme.CS492FinalProjectTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun saveData(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getData(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
}

class SettingsViewModel : ViewModel(){
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkModeFlow: Flow<Boolean> = _isDarkMode

    fun setDarkMode(isDarkMode: Boolean) {
        _isDarkMode.value = isDarkMode
    }
}

@Composable
fun SettingsScreen(
    preferencesManager: PreferencesManager,
    settingsViewModel: SettingsViewModel,
    navController: NavHostController,
    intent: Intent
    ){

    val steamID: Long = preferencesManager.getData("steamid", "-1").toLong()
    val darkTheme = preferencesManager.getData("dark_theme", "false").toBoolean()

    Scaffold () {
        ApplyTheme(isDarkMode = darkTheme) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(it)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                SwitchSetting(
                    label = "Dark Theme",
                    isChecked = preferencesManager.getData(
                        "dark_theme", "false").toBoolean(),
                    onCheckedChange = {isChecked ->
                        settingsViewModel.setDarkMode(isChecked)
                        preferencesManager.saveData("dark_theme", isChecked.toString())
                    }
                )
                Spacer(modifier = Modifier.height(6.dp))
                ImmutableTextPreference(
                    title = "SteamID",
                    value = steamID,
                    preferencesManager = preferencesManager
                )
                Spacer(modifier = Modifier.height(50.dp))
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Button(
                        onClick = { logout(preferencesManager, navController, intent) },
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Text(text = "Logout")
                    }
                }
            }
        }
    }
}


fun logout(preferencesManager: PreferencesManager, navController: NavHostController, intent: Intent){
    preferencesManager.saveData("steamid", "-1")
    intent.data = null
    navController.navigate("Login")
}

@Composable
fun ApplyTheme(isDarkMode: Boolean, content: @Composable () -> Unit){
    if (isDarkMode){
       CS492FinalProjectTheme (darkTheme = true) {
           content()
       }
    }
    else{
        CS492FinalProjectTheme (darkTheme = false) {
            content()
        }
    }
}

@Composable
fun ImmutableTextPreference(
    title: String,
    value: Long,
    preferencesManager: PreferencesManager
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ){
        Text(text = title, modifier = Modifier.weight(1f))
        val idValue: String = if (value == -1L){
            "Not Set"
        } else{
            value.toString()
        }
        Text(text = idValue, modifier = Modifier.weight(1f))
        preferencesManager.saveData("steamid", value.toString())
    }
}

@Composable
fun SwitchSetting(label: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ){
        Text(text = label, modifier = Modifier.weight(1f))
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(end = 16.dp)
        )

    }
}

//KEEP THIS, USING FOR COMPARISONS FOR GOOD UI DESIGN
//@Composable
//fun SettingsTextComp() {
//    Surface(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        onClick = {
//            // clicking on the preference, will show the dialog
//        },
//    ) {
//        Column {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.Start
//            ) {
//                Spacer(modifier = Modifier.width(16.dp))
//                Column(modifier = Modifier.padding(8.dp)) {
//                    // setting text title
//                    Text(
//                        text = "Text setting",
//                        style = MaterialTheme.typography.bodyMedium,
//                        textAlign = TextAlign.Start,
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//                    // current value shown
//                    Text(
//                        text = "Placeholder value2",
//                        style = MaterialTheme.typography.bodySmall,
//                        textAlign = TextAlign.Start,
//                    )
//                }
//            }
//            Divider()
//        }
//    }
//}
