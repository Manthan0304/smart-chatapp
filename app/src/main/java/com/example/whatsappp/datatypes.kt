package com.example.whatsappp
import com.google.firebase.Timestamp

data class Signinresult (
    val data: UserData?,
    val errorMessage: String?
)


data class UserData(
    val userid:String = "",
    val username:String? = "",
    val ppurl:String? = "",
    val email:String? = "",
    val bio:String = ""
)

data class appstate(
    val issignedin:Boolean=false,
    val userData: UserData? = null,
    val signinerror:String?=null,
    val srEmail : String = "",
    val showdialog: Boolean = false,
    val user2 : ChatUserData? = null,
    val chatid : String = ""
) {
}

data class chatdata(
    val chatid:String = "",
    val last : Message?=null,
    val user1:ChatUserData?=null,
    val user2:ChatUserData?=null

)
data class Message(
    val msgId: String = "",
    val senderId: String = "",
    val repliedMessage: Message? = null,
    val reaction: List<Reaction> = emptyList(),
    val imgUrl: String = "",
    val fireUrl: String = "",
    val fileName: String = "",
    val fileSize: String = "",
    val vidUrl: String = "",
    val progress: String = "",
    val content: String = "",
    val time: Timestamp? = null,
    val forwarded: Boolean = false,
    val read: Boolean = false
)

data class Reaction(
    val ppurl: String = "",
    val username: String = "",
    val userid: String = "",
    val reaction: String = ""
)

data class ChatUserData(
    val userid: String = "",
    val username: String = "",
    val ppurl: String = "",
    val bio:String = "",
    val typing:Boolean = false,
    val email:String = "",
    val status:Boolean= false,
    val unread : Int = 0
)

data class image(
    val imageurl :String = "",
    val time: Timestamp? = Timestamp.now()
)

data class story(
    val id : String = "",
    val userid:String = "",
    val username : String? = "",
    val ppurl : String? = "",
    val images : List<image> = emptyList()
)