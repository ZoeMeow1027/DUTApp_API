package io.zoemeow.dutapp.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import io.zoemeow.dutapp.model.account.AccountInformationItem
import io.zoemeow.dutapp.model.subject.SubjectFeeItem
import io.zoemeow.dutapp.model.subject.SubjectScheduleItem

class AccountCacheData {
    @Transient private var sessionIDPri: MutableState<String> = mutableStateOf(String())
    private var dataSubjectSchedule: ArrayList<SubjectScheduleItem> = ArrayList()
    private var dataSubjectFee: ArrayList<SubjectFeeItem> = ArrayList()
    private var dataInfo = mutableStateOf(AccountInformationItem())
    private var subjectCreditTotal: Int = 0
    private var subjectMoneyTotal: Long = 0
    private var getTime: Long = 0
    @Transient private var dataSubjectScheduleToday = mutableStateListOf<SubjectScheduleItem>()
    @Transient private var dataSubjectScheduleFuture = mutableStateListOf<SubjectScheduleItem>()
    @Transient private var dataSubjectExaminationOn7Days = mutableStateListOf<SubjectScheduleItem>()


    var sessionID: MutableState<String>
        get() = sessionIDPri
        set(value) {
            sessionIDPri.value = value.value
        }

    var subjectScheduleData: ArrayList<SubjectScheduleItem>
        get() = dataSubjectSchedule
        set(value) {
            dataSubjectSchedule.clear()
            dataSubjectSchedule.addAll(value)
        }

    var subjectFeeData: ArrayList<SubjectFeeItem>
        get() = dataSubjectFee
        set(value) {
            dataSubjectFee.clear()
            dataSubjectFee.addAll(value)
        }

    var accountInformationData: MutableState<AccountInformationItem>
        get() = dataInfo
        set(value) { dataInfo.value = value.value }

    var subjectCredit: Int
        get() = subjectCreditTotal
        set(value) { subjectCreditTotal = value }

    var subjectMoney: Long
        get() = subjectMoneyTotal
        set(value) { subjectMoneyTotal = value }

    var subjectGetTime: Long
        get() = getTime
        set(value) { getTime = value }

    val subjectScheduleDataOnToday: SnapshotStateList<SubjectScheduleItem>
        get() = dataSubjectScheduleToday

    val subjectScheduleDataOnFuture: SnapshotStateList<SubjectScheduleItem>
        get() = dataSubjectScheduleFuture

    val subjectExaminationOn7Days: SnapshotStateList<SubjectScheduleItem>
        get() = dataSubjectExaminationOn7Days

    fun clearAllData() {
        sessionID.value = String()
        dataInfo.value = AccountInformationItem()
        dataSubjectSchedule.clear()
        dataSubjectFee.clear()
        subjectCredit = 0
        subjectMoney = 0
        subjectScheduleDataOnToday.clear()
        subjectScheduleDataOnFuture.clear()
        subjectExaminationOn7Days.clear()
    }
}
