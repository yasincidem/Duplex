package com.yasincidem.duplex.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NamedNavArgument
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
typealias EnterTransitionAnimation = (AnimatedContentScope<String>.(initial: NavBackStackEntry, target: NavBackStackEntry) -> EnterTransition?)?
@OptIn(ExperimentalAnimationApi::class)
typealias ExitTransitionAnimation = (AnimatedContentScope<String>.(initial: NavBackStackEntry, target: NavBackStackEntry) -> ExitTransition?)?
@OptIn(ExperimentalAnimationApi::class)
typealias PopEnterTransitionAnimation = (AnimatedContentScope<String>.(initial: NavBackStackEntry, target: NavBackStackEntry) -> EnterTransition?)?
@OptIn(ExperimentalAnimationApi::class)
typealias PopExitTransitionAnimation = (AnimatedContentScope<String>.(initial: NavBackStackEntry, target: NavBackStackEntry) -> ExitTransition?)?

interface Screen {
    val route: String
}

@OptIn(ExperimentalAnimationApi::class)
sealed class RootScreen constructor(
    override val route: String,
    val startScreen: LeafScreen,
    val arguments: List<NamedNavArgument> = emptyList(),
    val deepLinks: List<NavDeepLink> = emptyList(),
    val enterTransition: EnterTransitionAnimation = null,
    val exitTransition: ExitTransitionAnimation = null,
    val popEnterTransition: PopEnterTransitionAnimation = enterTransition,
    val popExitTransition: PopExitTransitionAnimation = exitTransition,
) : Screen {
    object Home : RootScreen("home_root", LeafScreen.Home)
}

@OptIn(ExperimentalAnimationApi::class)
sealed class LeafScreen(
    override val route: String,
    val arguments: List<NamedNavArgument> = emptyList(),
    val deepLinks: List<NavDeepLink> = emptyList(),
    val enterTransition: EnterTransitionAnimation = null,
    val exitTransition: ExitTransitionAnimation = null,
    val popEnterTransition: PopEnterTransitionAnimation = enterTransition,
    val popExitTransition: PopExitTransitionAnimation = exitTransition,
) : Screen {
    object Home : LeafScreen(
        "home",
        enterTransition = { initial, target ->
            when (initial.destination.route) {
                else -> {
                    null
                }
            }
        }
    )
}

@ExperimentalAnimationApi
fun NavGraphBuilder.composableScreen(
    screen: LeafScreen,
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
) =
    composable(
        screen.route,
        screen.arguments,
        screen.deepLinks,
        screen.enterTransition,
        screen.exitTransition,
        screen.popEnterTransition,
        screen.popExitTransition,
        content
    )