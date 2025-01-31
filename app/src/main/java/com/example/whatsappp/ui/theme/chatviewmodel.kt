package com.example.whatsappp.ui.theme

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.whatsappp.CHAT_COLLECTION
import com.example.whatsappp.ChatUserData
import com.example.whatsappp.IMAGES_COLLECTION
import com.example.whatsappp.MESSAGES_COLLECTION
import com.example.whatsappp.Message
import com.example.whatsappp.STORIES_COLLECTION
import com.example.whatsappp.Signinresult
import com.example.whatsappp.USERS_COLLECTION
import com.example.whatsappp.UserData
import com.example.whatsappp.appstate
import com.example.whatsappp.chatdata
import com.example.whatsappp.image
import com.example.whatsappp.story
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class chatviewmodel : ViewModel() {
    private val _state = MutableStateFlow(appstate())
    val state = _state.asStateFlow()
    private val usercollection = Firebase.firestore.collection(USERS_COLLECTION)
    var userDataListener: ListenerRegistration? = null
    var ChatListener: ListenerRegistration? = null
    var tp by mutableStateOf(chatdata())
    var tpListener: ListenerRegistration? = null

    var chats by mutableStateOf<List<chatdata>>(emptyList())

    var reply by mutableStateOf("")
    private val firestore = FirebaseFirestore.getInstance()
    var msglistener : ListenerRegistration? = null
    var messages by mutableStateOf<List<Message>>(listOf())

    fun resetState() {}

    //
//
    fun onsigninresult(signinresult: Signinresult) {
        _state.update {
            it.copy(
                issignedin = signinresult.data != null,
                signinerror = signinresult.errorMessage
            )
        }
    }

    //
    fun adduserdatatofirebase(userData: UserData) {
        val userDatamap = mapOf(
            "userid" to userData.userid,
            "username" to userData.username,
            "ppurl" to userData.ppurl,
            "email" to userData.email

        )

        val userDocument = usercollection.document(userData.userid ?: return)
        userDocument.get().addOnSuccessListener {
            if (it.exists()) {
                userDocument.update(userDatamap).addOnSuccessListener {
                    Log.d(ContentValues.TAG, "User data added successfully")
                }.addOnFailureListener {
                    Log.d(ContentValues.TAG, "User data failed to add")
                }

            } else {
                userDocument.set(userDatamap).addOnSuccessListener {
                    Log.d(ContentValues.TAG, "User data added successfully")
                }.addOnFailureListener {
                    Log.d(ContentValues.TAG, "User data failed to add")
                }
            }
        }
    }

    fun getuserData(userid: String) {
        userDataListener = usercollection.document(userid).addSnapshotListener { value, error ->
            if (value != null) {
                _state.update {
                    it.copy(userData = value.toObject(UserData::class.java))
                }
            }
        }

    }

    fun hidedialog() {
        _state.update {
            it.copy(
                showdialog = false
            )
        }
    }

    fun showdialog() {
        _state.update {
            it.copy(
                showdialog = true
            )
        }
    }

    fun setsrEmail(email: String) {
        _state.update {
            it.copy(
                srEmail = email
            )
        }
    }

    fun addchat(email: String) {

        Firebase.firestore.collection(CHAT_COLLECTION).where(
            Filter.or(
                Filter.and(
                    Filter.equalTo("user1.email", email),
                    Filter.equalTo("user2.email", state.value.userData?.email)
                ),
                Filter.and(
                    Filter.equalTo("user2.email", email),
                    Filter.equalTo("user1.email", state.value.userData?.email)
                )
            )
        ).get().addOnSuccessListener {
            if (it.isEmpty) {
                usercollection.whereEqualTo("email", email).get().addOnSuccessListener {
                    if (it.isEmpty) {
                        println("failed")
                    } else {
                        val chatpartner = it.toObjects(UserData::class.java).firstOrNull()

                        val id = Firebase.firestore.collection(CHAT_COLLECTION).document().id
                        val chat = chatdata(
                            chatid = id,
                            last = Message(
                                senderId = "",
                                content = "",
                                time = null
                            ),
                            user1 = ChatUserData(
                                userid = state.value.userData?.userid.toString(),
                                typing = false,
                                bio = state.value.userData?.bio.toString(),
                                username = state.value.userData?.username.toString(),
                                email = state.value.userData?.email.toString()
                            ),
                            user2 = ChatUserData(
                                userid = chatpartner?.userid.toString(),
                                typing = false,
                                bio = chatpartner?.bio.toString(),
                                username = chatpartner?.username.toString(),
                                email = chatpartner?.email.toString()
                            )
                        )
                        Firebase.firestore.collection(CHAT_COLLECTION).document(id).set(chat)
                    }

                }
            }
        }
    }

    fun showchats(userid: String) {
        ChatListener = Firebase.firestore.collection(CHAT_COLLECTION).where(
            Filter.or(
                Filter.equalTo("user1.userid", userid),
                Filter.equalTo("user2.userid", userid)

            )
        ).addSnapshotListener { value, error ->
            if (value != null) {
                chats = value.documents.mapNotNull {
                    it.toObject(chatdata::class.java)
                }.sortedBy {
                    it.last?.time
                }.reversed() as List<chatdata>
            }
        }
    }

    fun gettp(
        chatID: String,
    ) {
        tpListener?.remove()
        tpListener = Firebase.firestore.collection(CHAT_COLLECTION).document(chatID)
            .addSnapshotListener { snp, err ->
                if (snp != null) {
                    tp = snp.toObject(chatdata::class.java)!!
                }
            }
    }

    fun setchatuser(usr: ChatUserData, id: String) {
        _state.update {
            it.copy(
                user2 = usr,
                chatid = id
            )
        }
    }

    fun sendreply(
        chatid: String,
        replymessage: Message = Message(),
        msg: String,
        senderid: String = state.value.userData?.userid.toString(),
    ) {
        val id = Firebase.firestore.collection(CHAT_COLLECTION).document().collection(
            MESSAGES_COLLECTION
        ).document().id

        val time = Calendar.getInstance().time

        val message = Message(
            msgId = id,
            repliedMessage = replymessage,
            senderId = senderid,
            content = msg,
            time = Timestamp(date = time)
        )

        Log.d("ChatViewModel", "Sending message: $message to chat: $chatid")

        Firebase.firestore.collection(CHAT_COLLECTION).document(chatid).collection(
            MESSAGES_COLLECTION).document(id).set(message)
            .addOnSuccessListener {
                Log.d("ChatViewModel", "Message sent successfully")
            }
            .addOnFailureListener { e ->
                Log.e("ChatViewModel", "Error sending message", e)
            }
        
        firestore.collection(CHAT_COLLECTION).document(chatid).update("last", message)
            .addOnSuccessListener {
                Log.d("ChatViewModel", "Last message updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("ChatViewModel", "Error updating last message", e)
            }
    }

    fun popmessage(chatid: String){
        msglistener?.remove()
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                if(chatid!=""){
                    msglistener = Firebase.firestore.collection(CHAT_COLLECTION).document(chatid).collection(
                        MESSAGES_COLLECTION).addSnapshotListener { value, error ->
                            if(value!=null){
                                messages = value.documents.mapNotNull {
                                    it.toObject(Message::class.java)
                                }.sortedByDescending {
                                    it.time
                                }
                            }
                    }
                }
            }
        }
    }
    fun uploadimage(img: Uri, callback:(String)->Unit){
        var storageref = Firebase.storage.reference
        val image = storageref.child("$IMAGES_COLLECTION/${System.currentTimeMillis()}")
        image.putFile(img).addOnSuccessListener {
            image.downloadUrl.addOnSuccessListener {
                val url = it.toString()
                callback(url)
            }.addOnFailureListener{
                callback("")
            }
        }.addOnFailureListener{
            callback("")
        }.addOnCompleteListener{
//            callback("")
        }
    }

    fun uploadstory(url: String) {
        val image = image(
            imageurl = url,
            time = Timestamp(Calendar.getInstance().time)
        )
        val id =  firestore.collection(STORIES_COLLECTION).document().id
        val story = story(
            id = id,
            userid = state.value.userData?.userid.toString(),
            username = state.value.userData?.username,
            ppurl = state.value.userData?.ppurl.toString(),
            images = listOf(image)
        )
        firestore.collection(STORIES_COLLECTION).document(id).set(story)
    }
}











