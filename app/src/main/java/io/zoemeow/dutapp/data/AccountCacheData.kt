package io.zoemeow.dutapp.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.zoemeow.dutapp.model.AccountInformationItem
import io.zoemeow.dutapp.model.SubjectFeeItem
import io.zoemeow.dutapp.model.SubjectScheduleItem

class AccountCacheData() {
    private var dataInfo: MutableState<AccountInformationItem> = mutableStateOf(AccountInformationItem())
    private var dataSubjectSchedule: MutableState<ArrayList<SubjectScheduleItem>> = mutableStateOf(ArrayList())
    private var dataSubjectFee: MutableState<ArrayList<SubjectFeeItem>> = mutableStateOf(ArrayList())

    @Transient
    private var sessionIDPri: MutableState<String> = mutableStateOf(String())

    var sessionID: MutableState<String>
        get() = sessionIDPri
        set(value) { sessionIDPri.value = value.value }

    fun isStoringSessionID(): Boolean {
        return sessionIDPri.value.isNotEmpty()
    }

    var subjectScheduleData: MutableState<ArrayList<SubjectScheduleItem>>
        get() = dataSubjectSchedule
        set(value) {
            dataSubjectSchedule.value.clear()
            dataSubjectSchedule.value.addAll(value.value)
        }

    var subjectFeeData: MutableState<ArrayList<SubjectFeeItem>>
        get() = dataSubjectFee
        set(value) {
            dataSubjectFee.value.clear()
            dataSubjectFee.value.addAll(value.value)
        }

    var accountInformationData: MutableState<AccountInformationItem>
        get() = dataInfo
        set(value) { dataInfo.value = value.value }

    fun clearAllData() {
        dataInfo.value = AccountInformationItem()
        dataSubjectSchedule.value.clear()
        dataSubjectFee.value.clear()
        sessionIDPri.value = String()
    }

}
