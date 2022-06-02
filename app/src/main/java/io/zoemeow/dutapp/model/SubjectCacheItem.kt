package io.zoemeow.dutapp.model

data class SubjectCacheItem(
    var accountInformationUpdateTime: Long = 0,
    var accountInformationData: AccountInformationItem = AccountInformationItem(),
    var subjectScheduleUpdateTime: Long = 0,
    var subjectScheduleData: ArrayList<SubjectScheduleItem> = ArrayList(),
    var subjectFeeUpdateTime: Long = 0,
    var subjectFeeData: ArrayList<SubjectFeeItem> = ArrayList()
)
