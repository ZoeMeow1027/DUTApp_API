package io.zoemeow.dutapp.model

import com.google.gson.annotations.SerializedName

data class AccountInformationMainItem(
    val date: Long? = null,
    
    @SerializedName("accountinfo")
    val accountInfo: AccountInformationItem? = null,
)
