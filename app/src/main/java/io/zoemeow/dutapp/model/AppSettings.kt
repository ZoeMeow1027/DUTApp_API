package io.zoemeow.dutapp.model

data class AppSettings(
    var autoLogin: Boolean = false,
    var username: String? = null,
    var password: String? = null,
)
