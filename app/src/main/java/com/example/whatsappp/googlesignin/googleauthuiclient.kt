package com.example.whatsappp.googlesignin

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.whatsappp.Signinresult
import com.example.whatsappp.UserData
import com.example.whatsappp.ui.theme.chatviewmodel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException


class googleauthuiclient(
    private val context: Context,
    private val oneTapClient: SignInClient,
    val viewmodel: chatviewmodel,

    ) {

    private val auth = Firebase.auth


    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildsignInRequest()
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    private fun buildsignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder().setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder().setSupported(true)
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("423241164781-h0emrsptbukfq2jsnjn97dng4don52c4.apps.googleusercontent.com")
                .build()
        ).setAutoSelectEnabled(true).build()
    }

    suspend fun signinwithintent(intent: Intent): Signinresult {
        viewmodel.resetState()
        val cred = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleidtoken = cred.googleIdToken
        val googlecred = GoogleAuthProvider.getCredential(googleidtoken, null)
        return try {
            val user = auth.signInWithCredential(googlecred).await().user
            Signinresult(
                errorMessage = null,
                data = user?.run {
                    UserData(
                        email = email.toString(),
                        userid = uid,
                        username = displayName,
                        ppurl = photoUrl.toString().substring(0, photoUrl.toString().length - 6)
                    )
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            Signinresult(
                errorMessage = e.message,
                data = null
            )
        }
    }

    fun getsignedinuser(): UserData? = auth.currentUser?.run {
        UserData(
            email = email.toString(),
            userid = uid,
            username = displayName,
            ppurl = photoUrl.toString()
        )
    }
}
