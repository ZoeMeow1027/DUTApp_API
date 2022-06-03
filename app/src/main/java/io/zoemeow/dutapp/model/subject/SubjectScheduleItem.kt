package io.zoemeow.dutapp.model.subject


data class SubjectScheduleItem(
    val id: String? = null,
    val name: String? = null,
    val credit: Int = 0,
    val is_high_quality: Boolean = false,
    val lecturer: String? = null,
    val schedule_study: ScheduleStudyItem? = null,
    val schedule_exam: ScheduleExamItem? = null,
    val point_formula: String? = null,
)
