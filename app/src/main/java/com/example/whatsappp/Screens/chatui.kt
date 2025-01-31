package com.example.whatsappp.Screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.whatsappp.ChatUserData
import com.example.whatsappp.Message
import com.example.whatsappp.R
import com.example.whatsappp.appstate
import com.example.whatsappp.ui.theme.chatviewmodel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun chatui(
    viewmodel: chatviewmodel = chatviewmodel(),
    userData: ChatUserData = ChatUserData(),
    chatid: String = "",
    messages: List<Message> = emptyList(),
    state: appstate = appstate(),
    onback: () -> Unit = {},
    context: Context = LocalContext.current,
    navController: NavController,
){
    val liststate = rememberLazyListState()
    val tp = viewmodel.tp
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(key1 = Unit) {
        viewmodel.popmessage(state.chatid)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = userData.ppurl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(40.dp)
                        )
                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = userData.username.toString(),
                                modifier = Modifier.padding(start = 16.dp)
                            )
                            if (userData.userid == tp.user1?.userid) {
                                AnimatedVisibility(tp.user1.typing) {
                                    Text(
                                        text = "Typing...",
                                        modifier = Modifier.padding(16.dp),
                                        color = MaterialTheme.colorScheme.onBackground,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                            }
                            if (userData.userid == tp.user2?.userid) {
                                AnimatedVisibility(tp.user2.typing) {
                                    Text(
                                        text = "Typing...",
                                        modifier = Modifier.padding(16.dp),
                                        color = MaterialTheme.colorScheme.onBackground,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            )

        },

        ) {

        Image(
            painter = painterResource(R.drawable.blck_blurry),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(.5f)
        )
        Image(
            painter = painterResource(R.drawable.social_media_doodle_seamless_pattern_vector_27700734),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(.5f)
        )
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()


        ) {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                reverseLayout = true,
                state = liststate
            ) {
                items(messages.size) { index ->
                    val message = messages[index]
                    val previousmessage = if (index > 0) messages[index - 1] else null
                    val nextmessage = if (index < messages.size - 1) messages[index + 1] else null

                    MessageItem(
                        message = message, 
                        index = index,
                        previd = previousmessage?.senderId.toString(),
                        nextid = nextmessage?.senderId.toString(), 
                        state = state
                    )
                }
            }

            LaunchedEffect(messages) {
                if (messages.isNotEmpty()) {
                    liststate.animateScrollToItem(0)
                }
            }

            Row(
                modifier = Modifier
                    .imePadding()
                    .padding(horizontal = 60.dp)
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.CameraAlt, contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .padding(12.dp)
                )


                Row(
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary,
                            shape = RoundedCornerShape(32.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = viewmodel.reply,
                        onValueChange = {
                            viewmodel.reply = it
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(text = "Type a message")
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                    )
                    AnimatedVisibility(visible = viewmodel.reply.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Send,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .padding(12.dp)
                                .combinedClickable(
                                    onClick = {
                                        viewmodel.sendreply(
                                            msg = viewmodel.reply,
                                            chatid = chatid,
//                                            replymessage = viewmodel.replymessage
                                        )
                                        viewmodel.reply = ""
                                    },
                                    onDoubleClick = {}
                                )
                        )
                    }
                }
            }
        }

    }

}

@Composable
fun MessageItem(message: Message, index: Int, previd: String, nextid: String, state: appstate) {
    val context = LocalContext.current
    val brush = Brush.linearGradient(
        listOf(
            Color(0xFF238CDD),
            Color(0xFF1952C4)
        )
    )
    val brush2 = Brush.linearGradient(
        listOf(
            Color(0xFF2A4783),
            Color(0xFF2F6086)
        )
    )

    val iscurrentuser = state.userData?.userid == message.senderId

    val shape = if (iscurrentuser) {
        if (previd == message.senderId && nextid == message.senderId) {
            RoundedCornerShape(16.dp, 3.dp, 3.dp, 16.dp)
        } else if (previd == message.senderId) {
            RoundedCornerShape(16.dp, 16.dp, 3.dp, 16.dp)
        } else if (nextid == message.senderId) {
            RoundedCornerShape(16.dp, 3.dp, 16.dp, 3.dp)
        } else {
            RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp)
        }
    } else {
        if (previd == message.senderId && nextid == message.senderId) {
            RoundedCornerShape(3.dp, 16.dp, 16.dp, 3.dp)
        } else if (previd == message.senderId) {
            RoundedCornerShape(16.dp, 16.dp, 16.dp, 3.dp)
        } else if (nextid == message.senderId) {
            RoundedCornerShape(3.dp, 16.dp, 16.dp, 16.dp)
        } else {
            RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp)
        }

    }
    val color = if (iscurrentuser) brush else brush2

    val alignment = if (iscurrentuser) Alignment.CenterEnd else Alignment.CenterStart

    val formatter = remember {
        SimpleDateFormat(("hh:mm a"), Locale.getDefault())
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val indication = rememberRipple(
        bounded = true,
        color = Color(0xFFFFFFFFF)
    )
    val clkcolor = Color.Transparent
    Box(
        modifier = Modifier
            .indication(interactionSource, indication)
            .background(
                clkcolor
            )
            .fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Column(verticalArrangement = Arrangement.Bottom) {
            Column(
                modifier = Modifier
                    .shadow(2.dp, shape = shape)
                    .widthIn(max = 270.dp)
                    .fillMaxHeight()
                    .background(
                        color, shape
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (message.content != "") {
                    Text(
                        text = message.content.toString(),
                        modifier = Modifier.padding(top = 5.dp, start = 10.dp, end = 10.dp),
                        color = Color.White
                    )
                }

            }
        }
    }

}
