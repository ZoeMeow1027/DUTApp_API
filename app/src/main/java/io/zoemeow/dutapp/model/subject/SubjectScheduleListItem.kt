package io.zoemeow.dutapp.model.subject

data class SubjectScheduleListItem(
    val date: Long? = null,
    val total_credit: Int? = null,
    val schedule_list: ArrayList<SubjectScheduleItem>? = null
)