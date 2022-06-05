package io.zoemeow.dutapp.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.zoemeow.dutapp.model.account.AccountInformationItem
import io.zoemeow.dutapp.model.subject.SubjectFeeItem
import io.zoemeow.dutapp.model.subject.SubjectScheduleItem

class AccountCacheData {
    private var dataInfo: MutableState<AccountInformationItem> = mutableStateOf(
        AccountInformationItem()
    )
    private var dataSubjectSchedule: ArrayList<SubjectScheduleItem> = ArrayList()
    private var dataSubjectFee: ArrayList<SubjectFeeItem> = ArrayList()
    private var subjectCreditTotal: Int = 0
    private var subjectMoneyTotal: Long = 0

    @Transient
    private var sessionIDPri: MutableState<String> = mutableStateOf(String())

    var sessionID: MutableState<String>
        get() = sessionIDPri
        set(value) {
            sessionIDPri.value = value.value
        }

    var subjectCredit: Int
        get() = subjectCreditTotal
        set(value) { subjectCreditTotal = value }

    var subjectMoney: Long
        get() = subjectMoneyTotal
        set(value) { subjectMoneyTotal = value }

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

    fun clearAllData() {
        dataInfo.value = AccountInformationItem()
        dataSubjectSchedule.clear()
        dataSubjectFee.clear()
        subjectCredit = 0
        subjectMoney = 0
        sessionIDPri.value = String()
    }

}
