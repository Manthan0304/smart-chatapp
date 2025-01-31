package com.example.whatsappp.Screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.whatsappp.ChatUserData
import com.example.whatsappp.R
import com.example.whatsappp.appstate
import com.example.whatsappp.chatdata
import com.example.whatsappp.dialogs.customdialogbox
import com.example.whatsappp.dialogs.storypreview
import com.example.whatsappp.ui.theme.chatviewmodel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun chatscreenui(
    viewmodel: chatviewmodel = chatviewmodel(),
    state: appstate = appstate(),
    showsinglechat: (ChatUserData, String) -> Unit,
) {
    val selecteditem = remember {
        mutableListOf<String>()
    }
    val padding by animateDpAsState(targetValue = 10.dp, label = "")
    val chats = viewmodel.chats
    val filterchats = chats
    val border = Brush.sweepGradient(
        listOf(
            Color(0xFFA7e6FF),
            Color(0xFFA7e6FF)
        )
    )
    var imguri by remember { mutableStateOf<Uri?>(null) }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            imguri = it
        }

    var bitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }
    var isuploading by remember {
        mutableStateOf(false)
    }
    val lottieanimationfile by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.upload))
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewmodel.showdialog() },
                shape = RoundedCornerShape(50.dp),
                containerColor = colorScheme.inversePrimary
            ) {
                Icon(
                    imageVector = Icons.Filled.AddComment,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    ) {
        it
        Image(
            painter = painterResource(id = R.drawable.blck_blurry),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        AnimatedVisibility(visible = state.showdialog) {
            customdialogbox(
                state = state,
                hidedialog = { viewmodel.hidedialog() },
                addChat = {
                    viewmodel.addchat(state.srEmail)
                    viewmodel.hidedialog()
                    viewmodel.setsrEmail("")
                },
                setEmail = {
                    viewmodel.setsrEmail(it)
                }
            )
        }
        imguri?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                var src = ImageDecoder.createSource(LocalContext.current.contentResolver, it)
                bitmap = ImageDecoder.decodeBitmap(src)

            }
            storypreview(
                Uri = imguri,
                hideDialog = { imguri = null },
                upload = {
                    isuploading = true
                    viewmodel.uploadimage(it ?: imguri!!) {
                        viewmodel.uploadstory(it)
                        isuploading = false
                    }
                    imguri = null
                }
            )
        }
        Column(
            modifier = Modifier.padding(top = 36.dp)
        ) {
            Box() {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(0.98f)
                ) {
                    Column() {
                        Text(
                            text = "Hello", modifier = Modifier
                                .padding(start = 16.dp)
                                .offset(y = 5.dp), style = MaterialTheme.typography.titleSmall
                        )

                        Text(
                            text = state.userData?.username.toString(),
                            modifier = Modifier.padding(start = 16.dp),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                    }
                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = {}, modifier = Modifier
                            .background(
                                colorScheme.background.copy(alpha = 0.2f),
                                CircleShape
                            )
                            .border(0.05.dp, color = Color(0xFF35567A), CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable._666693_search_icon),
                            contentDescription = null,
                            modifier = Modifier.scale(0.7f)
                        )
                    }
                    Spacer(modifier = Modifier.padding(start = 5.dp))
                    Column {
                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .background(
                                    colorScheme.background.copy(alpha = .2f),
                                    CircleShape
                                )
                                .border(0.05.dp, Color(0xFF35567A), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = null,
                                modifier = Modifier.scale(1.3f)
                            )

                        }
                    }
                }
            }
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    if (isuploading) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(77.dp)
                                    .padding(3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(state.userData?.ppurl)
                                        .crossfade(true)
                                        .allowHardware(false)
                                        .build(),
                                    placeholder = painterResource(id = R.drawable.person_placeholder_4),
                                    error = painterResource(id = R.drawable.person_placeholder_4),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                                LottieAnimation(
                                    composition = lottieanimationfile,
                                    iterations = LottieConstants.IterateForever,
                                    modifier = Modifier.background(
                                        Color.Black.copy(alpha = 0.5f),
                                        CircleShape
                                    )
                                )
                            }
                        }
                    }
                    if (!isuploading) {
                        Box(
                            modifier = Modifier
                                .padding(bottom = 20.dp, start = 5.dp, end = 6.dp)
                                .size(70.dp)
                                .drawWithCache {
                                    onDrawBehind {
                                        drawCircle(
                                            brush = border,
                                            style = Stroke(
                                                width = 8f,
                                                pathEffect = PathEffect.dashPathEffect(
                                                    floatArrayOf(
                                                        (35.dp.toPx() * 2 * Math.PI.toFloat() / 5) - 15f,
                                                        15f
                                                    ),
                                                    0f
                                                )
                                            )
                                        )
                                    }
                                }
                                .padding(5.dp)
                                .background(
                                    colorScheme.background.copy(
                                        alpha = .4f
                                    ), CircleShape
                                )
                                .clickable {
                                    launcher.launch("image/*")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null,
                                tint = colorScheme.onBackground.copy(alpha = .8f),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }
            }
            LazyColumn(
                modifier = Modifier
                    .padding(top = padding)
                    .fillMaxSize()
                    .background(
                        color = colorScheme.background.copy(
                            alpha = 0.2f
                        ),
                        shape = RoundedCornerShape(30.dp, 30.dp)
                    )
                    .border(
                        0.05.dp,
                        color = Color(0xFF35567A),
                        shape = RoundedCornerShape(30.dp, 30.dp)
                    )
            ) {
                item {
                    Text(
                        "Chats",
                        modifier = Modifier.padding(16.dp, 16.dp, 16.dp),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Normal
                    )
                }
                items(filterchats) {
                    val chatuser = if (
                        it.user1?.userid != state.userData?.userid
                    ) {
                        it.user1
                    } else {
                        it.user2
                    }
                    Chatitem(
                        state = state,
                        chatuser!!,
                        chat = it,
                        isselected = selecteditem.contains(it.chatid),
                        showsinglechat = { user, id -> showsinglechat(user, id) }
                    )

                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("InvalidColorHexValue")
@Composable
fun Chatitem(
    state: appstate,
    userdata: ChatUserData,
    chat: chatdata,
    isselected: Boolean,
    showsinglechat: (ChatUserData, String) -> Unit,
) {
    val formatter = remember {
        SimpleDateFormat(("hh:mm a"), Locale.getDefault())
    }

    val color = if (!isselected) Color.Transparent else colorScheme.onPrimary

    Row(
        modifier = Modifier
            .background(
                color = color
            )
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable {
                showsinglechat(
                    userdata, chat.chatid
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(userdata.ppurl)
                .crossfade(true)
                .allowHardware(false)
                .build(),
            placeholder = painterResource(id = R.drawable.person_placeholder_4),
            error = painterResource(id = R.drawable.person_placeholder_4),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)

        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(.95f)
            ) {
                Text(
                    text = if (userdata.userid == state.userData?.userid)
                        userdata.username.orEmpty() + "(You)" else userdata.username.orEmpty(),
                    modifier = Modifier.width(150.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    if (chat.last?.time != null) formatter.format(chat.last.time.toDate())
                    else "",
                    color = Color.Gray,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Light
                    )
                )
            }

            AnimatedVisibility(chat.last?.time != null && userdata.typing) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (chat.last?.senderId == state.userData?.userid) {
                        Icon(
                            painter = painterResource(R.drawable.check_mark),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 5.dp)
                                .size(10.dp),
                            tint = if (chat.last?.read ?: false) Color(0xFF137C70D) else Color.White

                        )
                    }


                }
            }

        }
    }

}
