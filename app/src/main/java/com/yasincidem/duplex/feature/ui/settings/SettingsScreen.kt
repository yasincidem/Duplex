package com.yasincidem.duplex.feature.ui.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.yasincidem.duplex.common.modifier.disableMultiTouch

@Composable
fun SettingsScreen() {

    val systemUiController = rememberSystemUiController()
    val isDarkMode = isSystemInDarkTheme()
    val barColor = MaterialTheme.colors.background

    SideEffect {
        systemUiController.apply {
            setSystemBarsColor(
                color = barColor,
                darkIcons = isDarkMode.not()
            )
            setNavigationBarColor(
                color = barColor,
                darkIcons = isDarkMode.not()
            )
        }
    }

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .disableMultiTouch()
    ) {
        Scaffold(
            topBar = {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "Settings",
                    style = MaterialTheme.typography.h3.copy(fontWeight = FontWeight.SemiBold)
                )
            },
            content = {
            }
        )
    }
}
