package io.zoemeow.dutapp.model

import io.zoemeow.dutapp.model.account.AutoLoginSettings
import io.zoemeow.dutapp.model.subject.SubjectSchoolYearSettings

data class AppSettings(
    var autoLoginSettings: AutoLoginSettings = AutoLoginSettings(),
    var subjectSchoolYearSettings: SubjectSchoolYearSettings = SubjectSchoolYearSettings(),
)
