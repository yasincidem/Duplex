package com.yasincidem.duplex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.yasincidem.duplex.navigation.AppNavigation
import com.yasincidem.duplex.navigation.NavigatorHost
import com.yasincidem.duplex.ui.theme.DuplexTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen()
        initializeUI()
    }
}

fun ComponentActivity.initializeUI() {
    setContent {
        DuplexTheme {
            NavigatorHost {
                ProvideWindowInsets {
                    AppNavigation()
                }
            }
        }
    }
}
