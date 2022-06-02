package io.zoemeow.dutapp.model.subject

data class SubjectSchoolYearSettings(
    var subjectDefault: Boolean = true,
    var subjectYear: Int = 21,
    var subjectSemester: Int = 2,
    var subjectInSummer: Boolean = false,
)
