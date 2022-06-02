package io.zoemeow.dutapp.model.account

data class AutoLoginSettings(
    var autoLogin: Boolean = false,
    var username: String? = null,
    var password: String? = null,
)