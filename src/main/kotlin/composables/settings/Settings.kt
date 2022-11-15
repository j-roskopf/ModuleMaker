package composables.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import ui.AppTheme
import ui.PlatformTheme
import java.util.prefs.Preferences

const val SETTINGS_KEY = "settings"
const val ANDROID_KEY = "android"
const val KOTLIN_KEY = "kotlin"

private val preferences = Preferences.userRoot().node(SETTINGS_KEY)

@Composable
fun Settings(onOpenSettingsChange: (Boolean) -> Unit) {
    val selectedTab = remember { mutableStateOf(SettingsTab.TEMPLATES) }
    Window(
        onCloseRequest = {
            onOpenSettingsChange.invoke(false)
        },
        title = "Settings",
        state = WindowState(width = 1280.dp, height = 1080.dp)
    ) {
        MaterialTheme(
            colors = AppTheme.colors.material
        ) {
            PlatformTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column {
                        Scaffold(
                            topBar = { },
                            content = {
                                val tabs = SettingsTab.values()
                                Column {
                                    TabRow(selectedTabIndex = tabs.indexOf(selectedTab.value)) {
                                        tabs.forEach { settingsTab ->
                                            Tab(
                                                text = { Text(settingsTab.name) },
                                                selected = settingsTab == selectedTab.value,
                                                onClick = {
                                                    selectedTab.value = settingsTab
                                                }
                                            )
                                        }
                                    }

                                    when (selectedTab.value) {
                                        SettingsTab.TEMPLATES -> Templates(onOpenSettingsChange)
                                        SettingsTab.GENERAL -> General()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Templates(onOpenSettingsChange: (Boolean) -> Unit) {
    // load text from file
    val kotlinText = remember { mutableStateOf(preferences.get(KOTLIN_KEY, "")) }
    val androidText = remember { mutableStateOf(preferences.get(ANDROID_KEY, "")) }

    Column {

        Button(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
            onClick = {
                preferences.put(KOTLIN_KEY, kotlinText.value)
                preferences.put(ANDROID_KEY, androidText.value)
                onOpenSettingsChange.invoke(false)
            }
        ) {
            Text("Save")
        }

        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
            text = "Kotlin module template file"
        )

        TextField(
            modifier = Modifier.fillMaxWidth().weight(1f, true).padding(16.dp),
            value = kotlinText.value,
            onValueChange = {
                kotlinText.value = it
            },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )

        )

        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
            text = "Android module template file"
        )

        TextField(
            modifier = Modifier.fillMaxWidth().weight(1f, true).padding(16.dp),
            value = androidText.value,
            onValueChange = {
                androidText.value = it
            },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )

        )
    }
}

@Composable
private fun General() {
    Button(
        modifier = Modifier.padding(16.dp),
        onClick = {
            preferences.clear()
        },
    ) {
        Text("Clear preferences")
    }
}
