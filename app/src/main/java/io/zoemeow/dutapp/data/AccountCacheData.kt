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
    private var sessionID: MutableState<String> = mutableStateOf(String())

    var SubjectScheduleData: MutableState<ArrayList<SubjectScheduleItem>>
        get() = dataSubjectSchedule
        set(value) {
            dataSubjectSchedule.value.clear()
            dataSubjectSchedule.value.addAll(value.value)
        }

    var SubjectFeeData: MutableState<ArrayList<SubjectFeeItem>>
        get() = dataSubjectFee
        set(value) {
            dataSubjectFee.value.clear()
            dataSubjectFee.value.addAll(value.value)
        }

    var AccountInformationData: MutableState<AccountInformationItem>
        get() = dataInfo
        set(value) { dataInfo.value = value.value }

    var SessionID: MutableState<String>
        get() = sessionID
        set(value) { sessionID.value = value.value }

    fun clearAllData() {
        dataInfo.value = AccountInformationItem()
        dataSubjectSchedule.value.clear()
        dataSubjectFee.value.clear()
        sessionID.value = String()
    }

    fun isStoringSessionID(): Boolean {
        return sessionID.value.isNotEmpty()
    }
}
