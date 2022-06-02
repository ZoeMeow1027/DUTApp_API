package io.zoemeow.dutapp.model.cache

import io.zoemeow.dutapp.model.account.AccountInformationItem
import io.zoemeow.dutapp.model.subject.SubjectFeeItem
import io.zoemeow.dutapp.model.subject.SubjectScheduleItem

data class SubjectCacheItem(
    var accountInformationUpdateTime: Long = 0,
    var accountInformationData: AccountInformationItem = AccountInformationItem(),
    var subjectScheduleUpdateTime: Long = 0,
    var subjectScheduleData: ArrayList<SubjectScheduleItem> = ArrayList(),
    var subjectFeeUpdateTime: Long = 0,
    var subjectFeeData: ArrayList<SubjectFeeItem> = ArrayList()
)
