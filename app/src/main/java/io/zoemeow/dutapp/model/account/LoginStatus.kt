package io.zoemeow.dutapp.model.account

data class LoginStatus(
    val date: Long? = null,
    val session_id: String? = null,
    val logged_in: Boolean = false
)