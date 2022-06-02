package io.zoemeow.dutapp.model.account

import com.google.gson.annotations.SerializedName

data class AccountInformationMainItem(
    val date: Long? = null,

    @SerializedName("accountinfo")
    val accountInfo: AccountInformationItem? = null,
)
