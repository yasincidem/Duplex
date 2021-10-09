package com.yasincidem.duplex.feature.ui.home

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun HomeScreen() {

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
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Home Screen")
    }
}
