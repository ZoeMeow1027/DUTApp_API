package io.zoemeow.dutapp.model

import com.google.gson.annotations.SerializedName

data class LoginStatus(
    val date: Long? = null,

    @SerializedName("sessionid")
    val sessionId: String? = null,

    @SerializedName("loggedin")
    val loggedIn: Boolean = false
)
