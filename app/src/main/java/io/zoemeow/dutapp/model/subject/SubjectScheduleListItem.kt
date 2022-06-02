package io.zoemeow.dutapp.model.subject

import com.google.gson.annotations.SerializedName

data class SubjectScheduleListItem(
    val date: Long? = null,

    @SerializedName("totalcredit")
    val totalCredit: Int? = null,

    @SerializedName("schedulelist")
    val scheduleList: ArrayList<SubjectScheduleItem>? = null
)