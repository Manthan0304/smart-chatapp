package com.example.whatsappp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.whatsappp.Screens.chatscreenui
import com.example.whatsappp.Screens.chatui
import com.example.whatsappp.Screens.siginscreenui
import com.example.whatsappp.googlesignin.googleauthuiclient
import com.example.whatsappp.ui.theme.WhatsapppTheme
import com.example.whatsappp.ui.theme.chatviewmodel
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: chatviewmodel by viewModels()

    private val googleauthclient by lazy {
        googleauthuiclient(
            context = applicationContext,
            viewmodel = viewModel,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WhatsapppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
//                            .padding(innerPadding)
                    ) {
                        val state by viewModel.state.collectAsState()
                        val navController = rememberNavController()
                        NavHost(navController = navController, startDestination = startscreen) {


                            composable<startscreen> {
                                LaunchedEffect(key1 = Unit) {
                                    val userdata = googleauthclient.getsignedinuser()
                                    if (userdata != null) {
                                        viewModel.getuserData(userdata.userid)
                                        viewModel.showchats(userdata.userid)
                                        navController.navigate(chatsscreen)
                                    } else {
                                        navController.navigate(signInscreen)
                                    }
                                }
                            }

                            composable<signInscreen> {
                                val launcher =
                                    rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult(),
                                        onResult = { result ->
                                            if (result.resultCode == RESULT_OK) {
                                                lifecycleScope.launch {
                                                    val signinresult =
                                                        googleauthclient.signinwithintent(
                                                            intent = result.data ?: return@launch
                                                        )
                                                    viewModel.onsigninresult(signinresult)
                                                }

                                            }
                                        })

                                LaunchedEffect(key1 = state.issignedin) {
                                    val userData = googleauthclient.getsignedinuser()
                                    userData?.run {
                                        viewModel.adduserdatatofirebase(userData)
                                        viewModel.getuserData(userData.userid)
                                        viewModel.showchats(userData.userid)
                                        navController.navigate(chatsscreen)
                                    }

                                }
                                siginscreenui(onSigninClick = {
                                    lifecycleScope.launch {
                                        val signinintentsender = googleauthclient.signIn()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signinintentsender ?: return@launch
                                            ).build()
                                        )
                                    }
                                })
                            }
                            composable<chatsscreen> {
                                chatscreenui(
                                    viewmodel = viewModel,
                                    state = state,
                                    showsinglechat = { usr, id ->
                                        viewModel.gettp(id)
                                        viewModel.setchatuser(usr, id)
                                        navController.navigate(chatscreen)
                                    }
                                )
                            }
                            composable<chatscreen>(enterTransition = {
                                slideInHorizontally(
                                    initialOffsetX = { fullWidth ->
                                        fullWidth
                                    },
                                    animationSpec = tween(200)
                                )
                            },
                                exitTransition = {
                                    slideOutHorizontally (
                                        targetOffsetX = {fullWidth -> -fullWidth },
                                        animationSpec = tween(200)
                                    )
                                }
                                ) {
                                chatui(
                                    viewmodel = viewModel,
                                    navController = navController,
                                    messages = viewModel.messages,
                                    userData = state.user2!!,
                                    chatid = state.chatid,
                                    state = state,
                                    onback = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


