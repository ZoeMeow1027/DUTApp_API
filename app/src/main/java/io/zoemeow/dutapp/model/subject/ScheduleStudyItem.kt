package io.zoemeow.dutapp.model.subject

data class ScheduleStudyItem(
    val schedule: ArrayList<ScheduleItem>? = null,
    val weeks: ArrayList<WeekItem>? = null
)