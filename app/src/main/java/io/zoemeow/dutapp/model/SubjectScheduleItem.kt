package io.zoemeow.dutapp.model

data class SubjectScheduleItem(
    val ID: String? = null,
    val Name: String? = null,
    val Credit: Int = 0,
    val IsHighQuality: Boolean = false,
    val Lecturer: String? = null,
    val ScheduleStudy: String? = null,
    val Weeks: String? = null,
    val PointFomula: String? = null,
    val GroupExam: String? = null,
    val IsGlobalExam: Boolean? = null,
    val DateExam: Long? = null,
    val RoomExam: String? = null
)
