package com.yasincidem.duplex.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.yasincidem.duplex.common.composable.collectEvent
import com.yasincidem.duplex.feature.ui.login.LoginScreen
import com.yasincidem.duplex.feature.ui.main.MainScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun AppNavigation(
    navController: NavHostController = rememberAnimatedNavController(),
    isLoggedIn: Boolean?,
    navigator: Navigator = LocalNavigator.current,
) {

    collectEvent(navigator.queue) { event ->
        when (event) {
            is NavigationEvent.Destination -> navController.navigate(
                event.route,
                builder = event.navOptionsBuilder ?: {}
            )
            is NavigationEvent.Back -> navController.navigateUp()
        }
    }

    if (isLoggedIn == null) return

    val startRootDestination = when (isLoggedIn) {
        false -> {
            RootScreen.Login.route
        }
        true -> {
            RootScreen.Main.route
        }
    }

    val startLeafDestination = when (isLoggedIn) {
        false -> {
            LeafScreen.Login.route
        }
        true -> {
            LeafScreen.Main.route
        }
    }

    AnimatedNavHost(
        navController = navController,
        startDestination = startRootDestination
    ) {
        addMainRoot(
            navController,
            startRootDestination,
            startLeafDestination
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.addMainRoot(
    navController: NavController,
    startRootDestination: String,
    startLeafDestination: String,
) {
    navigation(
        route = startRootDestination,
        startDestination = startLeafDestination
    ) {
        addMain()
        addLogin()
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.addLogin() {
    composableScreen(LeafScreen.Login) {
        LoginScreen()
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.addMain() {
    composableScreen(LeafScreen.Main) {
        MainScreen()
    }
}
