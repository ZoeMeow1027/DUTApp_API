package io.zoemeow.dutapp.model.cache

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import io.zoemeow.dutapp.model.account.AccountInformationItem
import io.zoemeow.dutapp.model.subject.SubjectFeeItem
import io.zoemeow.dutapp.model.subject.SubjectScheduleItem

data class SubjectCacheItem(
    var accountInformationUpdateTime: Long = 0,
    var accountInformationData: AccountInformationItem = AccountInformationItem(),
    var subjectScheduleUpdateTime: Long = 0,
    var subjectScheduleData: SnapshotStateList<SubjectScheduleItem> = mutableStateListOf(),
    var subjectFeeUpdateTime: Long = 0,
    var subjectFeeData: SnapshotStateList<SubjectFeeItem> = mutableStateListOf(),
    var subjectCreditTotal: Int = 0,
    var subjectMoneyTotal: Long = 0,
)
