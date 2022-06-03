package io.zoemeow.dutapp.model.subject

data class ScheduleExamItem(
    val group: String? = null,
    val is_global: Boolean = false,
    val date: Long = 0,
    val room: String? = null,
)