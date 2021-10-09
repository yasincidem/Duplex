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
import com.yasincidem.duplex.feature.ui.home.HomeScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun AppNavigation(
    navController: NavHostController = rememberAnimatedNavController(),
    isOnBoardingSeen: Boolean? = true,
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

    if (isOnBoardingSeen == null) return

    val startRootDestination = when (isOnBoardingSeen) {
        false -> {
            RootScreen.Home.route
        }
        true -> {
            RootScreen.Home.route
        }
    }

    val startLeafDestination = when (isOnBoardingSeen) {
        false -> {
            LeafScreen.Home.route
        }
        true -> {
            LeafScreen.Home.route
        }
    }

    AnimatedNavHost(
        navController = navController,
        startDestination = startRootDestination
    ) {
        addMainRoot(navController)
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.addMainRoot(navController: NavController) {
    navigation(
        route = RootScreen.Home.route,
        startDestination = LeafScreen.Home.route
    ) {
        addHome(navController)
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.addHome(navController: NavController) {
    composableScreen(LeafScreen.Home) {
        HomeScreen()
    }
}
