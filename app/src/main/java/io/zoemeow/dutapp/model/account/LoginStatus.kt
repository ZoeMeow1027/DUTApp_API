package io.zoemeow.dutapp.model.account

import com.google.gson.annotations.SerializedName

data class LoginStatus(
    val date: Long? = null,

    @SerializedName("sessionid")
    val sessionId: String? = null,

    @SerializedName("loggedin")
    val loggedIn: Boolean = false
)
