package io.zoemeow.dutapp.model

import com.google.gson.annotations.SerializedName

data class SubjectFeeListItem(
    val date: Long? = null,

    @SerializedName("totalcredit")
    val totalCredit: Int? = null,

    @SerializedName("totalmoney")
    val totalMoney: Int? = null,

    @SerializedName("feelist")
    val feeList: ArrayList<SubjectFeeItem>? = null
)