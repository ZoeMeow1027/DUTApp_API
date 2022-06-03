package io.zoemeow.dutapp.model.subject

data class ScheduleItem(
    val day_of_week: Int? = null,
    val lesson: LessonItem? = null,
    val room: String? = null,
)