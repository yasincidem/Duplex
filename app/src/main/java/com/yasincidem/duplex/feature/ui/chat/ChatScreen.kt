package com.yasincidem.duplex.feature.ui.chat

import android.annotation.SuppressLint
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.yasincidem.duplex.common.composable.OnStop
import com.yasincidem.duplex.common.modifier.disableMultiTouch
import com.yasincidem.duplex.navigation.LocalNavigator
import com.yasincidem.duplex.navigation.Navigator
import com.yasincidem.duplex.ui.theme.MainDarkBlue
import com.yasincidem.duplex.ui.theme.MainDarkBlueContent
import com.yasincidem.duplex.ui.theme.MainOrange
import kotlinx.coroutines.launch

@SuppressLint("SimpleDateFormat")
@ExperimentalPermissionsApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel = viewModel(),
    navigator: Navigator = LocalNavigator.current,
) {

    val context = LocalContext.current

    val filePath = "${context.getExternalFilesDir(null)?.absolutePath}/record.mp4"

    val systemUiController = rememberSystemUiController()
    val isDarkMode = isSystemInDarkTheme()
    val scope = rememberCoroutineScope()
    val (uri, setUri) = remember {
        mutableStateOf("")
    }
    val player = remember {
        MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
        }
    }

    val recordAudioPermissionState =
        rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)

    val barColor = MaterialTheme.colors.background

    val otherUser by chatViewModel.otherUser.collectAsState()
    val timer by chatViewModel.timerStateFlow.collectAsState()
    val chatHistory = chatViewModel.chatHistory

    var isRecording by remember { mutableStateOf(false) }

    OnStop {
        player.stop()
        player.release()
    }

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

    LaunchedEffect(uri) {
        if (uri.isNotEmpty()) {
            player.apply {
                reset()
                setDataSource(uri)
                prepareAsync()
                setOnPreparedListener {
                    start()
                }
            }
        }
    }

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .disableMultiTouch()
    ) {
        PermissionRequired(
            permissionState = recordAudioPermissionState,
            permissionNotGrantedContent = {
                Column {
                    Text("The camera is important for this app. Please grant the permission.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Button(onClick = { recordAudioPermissionState.launchPermissionRequest() }) {
                            Text("Ok!")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                navigator.back()
                            }
                        ) {
                            Text("Nope")
                        }
                    }
                }
            },
            permissionNotAvailableContent = {
            }
        ) {
            Scaffold(
                topBar = {
                    Column {
                        Row(
                            Modifier.padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            IconButton(
                                onClick = {
                                    navigator.back()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowBack,
                                    contentDescription = "arrow back"
                                )
                            }

                            Image(
                                modifier = Modifier.size(32.dp),
                                painter = rememberImagePainter(
                                    data = otherUser?.photoUrl.toString(),
                                    builder = {
                                        transformations(CircleCropTransformation())
                                    }
                                ),
                                contentDescription = "photo url"
                            )

                            Text(
                                modifier = Modifier.padding(start = 12.dp),
                                text = otherUser?.name.toString(),
                                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Divider(thickness = 4.dp)
                    }
                },
                content = {
                    LazyColumn(
                        reverseLayout = true,
                        modifier = Modifier.padding(bottom = 72.dp)
                    ) {
                        items(chatHistory) { item ->
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val alignment =
                                    if (item.by == chatViewModel.currentUserId) Alignment.End else Alignment.Start

                                Card(
                                    modifier = Modifier
                                        .align(alignment)
                                        .padding(16.dp),
                                    shape = CircleShape,
                                    backgroundColor = if (isDarkMode) MainDarkBlue else MainOrange
                                ) {
                                    IconButton(onClick = {
                                        scope.launch {
                                            val uri = chatViewModel.getVoiceUrl(item.voiceId)
                                            setUri(uri)
                                            Log.i("uuuuu", uri)
                                        }
                                    }) {
                                        Icon(imageVector = Icons.Rounded.PlayArrow,
                                            contentDescription = "play voicemail")
                                    }
                                }
                            }

                            Divider()
                        }
                    }
                },
                bottomBar = {
                    Column {
                        Divider(thickness = 3.dp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.padding(start = 16.dp),
                                text = chatViewModel.prettifyTimer(timer)
                            )

                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onPress = {
                                                val mediaRecorder =
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                        MediaRecorder(context)
                                                    } else {
                                                        MediaRecorder()
                                                    }.apply {
                                                        setAudioSource(MediaRecorder.AudioSource.MIC)
                                                        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                                                        setAudioSamplingRate(44100)
                                                        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                                                        setAudioEncodingBitRate(96000)
                                                        setOutputFile(filePath)
                                                    }
                                                try {
                                                    isRecording = true
                                                    // Start recording here
                                                    mediaRecorder.apply {
                                                        prepare()
                                                        start()
                                                        chatViewModel.startTimer()
                                                    }
                                                    awaitRelease()
                                                } finally {
                                                    isRecording = false
                                                    mediaRecorder.apply {
                                                        try {
                                                            stop()
                                                        } catch (e: Exception) {

                                                        }
                                                    }
                                                    chatViewModel.stopTimer()
                                                    chatViewModel.sendVoice(filePath)
                                                }
                                            },
                                        )
                                    }
                                    .background(
                                        color = (if (isDarkMode) MainDarkBlueContent else MainOrange).copy(
                                            alpha = if (isRecording) 0.5f else 1f),
                                        shape = CircleShape,
                                    )
                            )
                        }
                    }
                }
            )
        }
    }
}
