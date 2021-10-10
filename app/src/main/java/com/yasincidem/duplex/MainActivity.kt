package com.yasincidem.duplex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.yasincidem.duplex.feature.ui.login.LoginViewModel
import com.yasincidem.duplex.navigation.AppNavigation
import com.yasincidem.duplex.navigation.NavigatorHost
import com.yasincidem.duplex.ui.theme.DuplexTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val loginViewModel by viewModels<LoginViewModel>()

        installSplashScreen()
        initializeUI(loginViewModel)
    }
}

fun ComponentActivity.initializeUI(loginViewModel: LoginViewModel) {
    setContent {
        DuplexTheme {
            NavigatorHost {
                ProvideWindowInsets {
                    AppNavigation(isLoggedIn = loginViewModel.isLoggedIn())
                }
            }
        }
    }
}
