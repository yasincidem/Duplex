package com.yasincidem.duplex.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

val LocalNavigator = staticCompositionLocalOf<Navigator> {
    error("No LocalNavigator given")
}

@Composable
fun NavigatorHost(
    viewModel: NavigatorViewModel = hiltViewModel(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalNavigator provides viewModel.navigator, content = content)
}

sealed class NavigationEvent(
    open val route: String,
    open val navOptionsBuilder: (NavOptionsBuilder.() -> Unit)? = null,
) {
    object Back : NavigationEvent("Back")
    data class Destination(
        override val route: String,
        override val navOptionsBuilder: (NavOptionsBuilder.() -> Unit)? = null,
    ) : NavigationEvent(route)
}

class Navigator {
    private val navigationQueue = Channel<NavigationEvent>(Channel.CONFLATED)

    fun navigate(route: String) {
        navigationQueue.trySend(NavigationEvent.Destination(route))
    }

    fun navigate(route: String, navOptionsBuilder: (NavOptionsBuilder.() -> Unit)) {
        navigationQueue.trySend(NavigationEvent.Destination(route, navOptionsBuilder))
    }

    fun navigate(screen: LeafScreen) {
        navigationQueue.trySend(NavigationEvent.Destination(screen.route))
    }

    fun navigate(screen: LeafScreen, navOptionsBuilder: (NavOptionsBuilder.() -> Unit)) {
        navigationQueue.trySend(NavigationEvent.Destination(screen.route, navOptionsBuilder))
    }

    fun back() {
        navigationQueue.trySend(NavigationEvent.Back)
    }

    val queue = navigationQueue.receiveAsFlow()
}
