package com.yasincidem.duplex.feature.ui.main

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.yasincidem.duplex.MainActivity
import com.yasincidem.duplex.R
import com.yasincidem.duplex.common.modifier.disableMultiTouch
import com.yasincidem.duplex.navigation.LeafScreen
import com.yasincidem.duplex.navigation.LocalNavigator
import com.yasincidem.duplex.navigation.Navigator
import com.yasincidem.duplex.ui.theme.MainDarkBlueContent
import com.yasincidem.duplex.ui.theme.MainOrange
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

enum class Menu {
    Settings,
    Logout
}

@ExperimentalMaterialApi
@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun MainScreen(
    navigator: Navigator = LocalNavigator.current,
    mainViewModel: MainViewModel = viewModel(),
) {

    val systemUiController = rememberSystemUiController()
    val isDarkMode = isSystemInDarkTheme()
    val barColor = MaterialTheme.colors.background

    val chats by mainViewModel.chats.collectAsState()
    val loadingState by mainViewModel.loadingState.observeAsState()

    val searchState = rememberSaveable { mutableStateOf("") }
    val dropdownState = rememberSaveable { mutableStateOf(false) }

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

    Log.i("qwewqe", chats.toString())

    Scaffold(
        Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .disableMultiTouch(),
        topBar = {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .clickable {
                        navigator.navigate(LeafScreen.Search)
                    },
                value = searchState.value,
                onValueChange = {},
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    autoCorrect = false
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "search icon"
                    )
                },
                enabled = false,
                readOnly = true,
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
                                                mainViewModel.apply {
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
                    backgroundColor = if (isDarkMode) {
                        MainDarkBlueContent
                    } else {
                        MainOrange.copy(alpha = 0.2f)
                    },
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navigator.navigate(LeafScreen.Search)
                },
                backgroundColor = if (isDarkMode) MainDarkBlueContent else MainOrange
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "new ")
            }
        },
        content = {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Crossfade(targetState = chats.list.isEmpty() && loadingState == false) {
                        if (it) {
                            Column(
                                Modifier.alpha(0.8f)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.vector_empty_main_screen),
                                    contentDescription = null
                                )
                                Text(
                                    text = "Your chats will appear here",
                                    style = MaterialTheme.typography.subtitle2
                                )
                            }
                        } else {
                            LazyColumn(
                                contentPadding= PaddingValues(top = 16.dp)
                            ) {
                                items(chats.list) {
                                    ListItem(
                                        modifier = Modifier
                                            .clickable {

                                            }
                                            .padding(horizontal = 12.dp, vertical = 4.dp),
                                        icon = {
                                            Image(
                                                modifier = Modifier.size(48.dp),
                                                painter = rememberImagePainter(
                                                    data = it?.photoUrl,
                                                    builder = {
                                                        transformations(CircleCropTransformation())
                                                    }
                                                ),
                                                contentDescription = "photo url"
                                            )
                                        },
                                        text = {
                                            Text(text = it?.username.toString())
                                        },
                                        secondaryText = {
                                            Text(text = it?.name.toString())
                                        }
                                    )
                                    Divider()
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
