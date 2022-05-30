package io.zoemeow.dutapp.model

import com.google.gson.annotations.SerializedName

data class SubjectScheduleListItem(
    val date: Long? = null,

    @SerializedName("totalcredit")
    val totalCredit: Int? = null,

    @SerializedName("schedulelist")
    val scheduleList: ArrayList<SubjectScheduleItem>? = null
)