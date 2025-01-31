package com.example.whatsappp.dialogs

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CropRotate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions

@Preview(showSystemUi = true)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun storypreview(
    Uri: Uri? = null,
    hideDialog: () -> Unit = {},
    upload: (Uri?) -> Unit = {},
) {
    var croppeduri: Uri? by remember {

        mutableStateOf(Uri)
    }

    val launcher = rememberLauncherForActivityResult(CropImageContract()) {
        if (it.isSuccessful) {
            croppeduri = it.uriContent
        } else {
            var exception = it.error
        }
    }

    Dialog(
        onDismissRequest = { hideDialog.invoke() }, properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
        ) {
            var scale by remember {
                mutableStateOf(1f)
            }
            var offset by remember {
                mutableStateOf(Offset.Zero)
            }

            var brush = Brush.linearGradient(
                listOf(
                    Color(0xff238cdd),
                    Color(0xff1952c4)
                )
            )
            var brush2 = Brush.linearGradient(
                listOf(
                    Color(0xffa02424),
                    Color(0xffc43b56)
                )
            )
            BoxWithConstraints {
                var state = rememberTransformableState { zoomChange, panChange, rotationChange ->

                    scale = (scale * zoomChange).coerceIn(1f, 5f)
                    val extWidth = (scale - 1) * constraints.maxWidth
                    val exhieght = (scale - 1) * constraints.maxHeight
                    val maxX = extWidth / 2
                    val maxY = exhieght / 2
                    offset = Offset(
                        x = (offset.x + scale * panChange.x).coerceIn(-maxX, maxX),
                        y = (offset.y + scale * panChange.y).coerceIn(-maxY, maxY)
                    )

                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = if (croppeduri != null) croppeduri else Uri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize(.9f)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationX = offset.x
                                translationY = offset.y
                            }
                            .transformable(state)

                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { hideDialog.invoke() },
                            modifier = Modifier.background(brush2, CircleShape),
                            colors = ButtonDefaults.buttonColors(Color.Transparent)
                        ) {
                            Text("Cancel", color = Color.White)
                        }

                        Button(
                            onClick = { upload(croppeduri) },
                            modifier = Modifier.background(brush, CircleShape),
                            colors = ButtonDefaults.buttonColors(Color.Transparent)
                        ) {
                            Text("Upload", color = Color.White)

                        }
                    }
                }
                IconButton(
                    onClick = {
                        var cropoption = CropImageContractOptions(
                            Uri,
                            CropImageOptions(activityBackgroundColor = Color(0xff000000).toArgb())
                        )
                        launcher.launch(cropoption)
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    Icon(imageVector = Icons.Rounded.CropRotate, contentDescription = null)
                }
            }
        }
    }
}








