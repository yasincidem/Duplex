package com.yasincidem.duplex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.accompanist.insets.ProvideWindowInsets
import com.yasincidem.duplex.navigation.AppNavigation
import com.yasincidem.duplex.navigation.NavigatorHost
import com.yasincidem.duplex.ui.theme.DuplexTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        initializeUI()
    }
}

fun ComponentActivity.initializeUI() {
    setContent {
        DuplexTheme {
            ProvideWindowInsets {
                NavigatorHost {
                    AppNavigation()
                }
            }
        }
    }
}
