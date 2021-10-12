package com.yasincidem.duplex.feature.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.yasincidem.duplex.common.modifier.disableMultiTouch
import com.yasincidem.duplex.feature.ui.main.Menu
import com.yasincidem.duplex.navigation.LeafScreen
import com.yasincidem.duplex.navigation.LocalNavigator
import com.yasincidem.duplex.navigation.Navigator
import com.yasincidem.duplex.navigation.RootScreen
import com.yasincidem.duplex.ui.theme.MainDarkBlueContent
import com.yasincidem.duplex.ui.theme.MainOrange
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(
    navigator: Navigator = LocalNavigator.current,
    searchViewModel: SearchViewModel = viewModel(),
) {

    val systemUiController = rememberSystemUiController()
    val isDarkMode = isSystemInDarkTheme()
    val backgroundColor = if (isDarkMode) {
        MainDarkBlueContent
    } else {
        MainOrange.copy(alpha = 0.2f)
    }

    val searchState by searchViewModel.query.collectAsState()
    val users by searchViewModel.getUsers().collectAsState(listOf())

    val dropdownState = rememberSaveable { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    SideEffect {
        systemUiController.apply {
            setSystemBarsColor(
                color = backgroundColor,
                darkIcons = isDarkMode.not()
            )
            setNavigationBarColor(
                color = backgroundColor,
                darkIcons = isDarkMode.not()
            )
        }
    }

    Scaffold(
        Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .disableMultiTouch(),
        backgroundColor = backgroundColor,
        topBar = {
            TextField(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                value = searchState,
                onValueChange = { input ->
                    searchViewModel.updateSearchQuery(input)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    autoCorrect = false
                ),
                leadingIcon = {
                    IconButton(
                        onClick = {
                            navigator.back()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "search icon"
                        )
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            dropdownState.value = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = "menu icon"
                        )

                        DropdownMenu(
                            expanded = dropdownState.value,
                            onDismissRequest = { dropdownState.value = false },
                        ) {
                            Menu.values().forEach { item ->
                                DropdownMenuItem(
                                    onClick = {
                                        when (item) {
                                            Menu.Settings -> {
                                                navigator.navigate(LeafScreen.Settings)
                                            }
                                            Menu.Logout -> {
                                                searchViewModel.apply {
                                                    logout()
                                                    if (!isLoggedIn())
                                                        navigator.navigate(LeafScreen.Login) {
                                                            launchSingleTop = true
                                                            popUpTo(0)
                                                        }
                                                }
                                            }
                                        }
                                        dropdownState.value = false
                                    }
                                ) {
                                    Text(text = item.name)
                                }
                            }
                        }
                    }
                },
                placeholder = {
                    Text(text = "Search contacts")
                },
                singleLine = true,
                shape = CircleShape,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        },
        content = {
            Surface(
                color = Color.Transparent,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                LazyColumn {
                    if (users.isNotEmpty()) {
                        items(users) {
                            ListItem(
                                modifier = Modifier
                                    .clickable {
                                        searchViewModel.createChat(it)
                                        navigator.navigate(LeafScreen.Chat) {
                                            popUpTo(LeafScreen.Main.route)
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                icon = {
                                    Image(
                                        modifier = Modifier.size(48.dp),
                                        painter = rememberImagePainter(
                                            data = it.photoUrl,
                                            builder = {
                                                transformations(CircleCropTransformation())
                                            }
                                        ),
                                        contentDescription = "photo url"
                                    )
                                },
                                text = {
                                    Text(text = it.username)
                                },
                                secondaryText = {
                                    Text(text = it.name)
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}
