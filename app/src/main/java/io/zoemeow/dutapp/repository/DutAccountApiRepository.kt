package io.zoemeow.dutapp.repository

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.zoemeow.dutapp.model.AccountInformationMainItem
import io.zoemeow.dutapp.model.LoginStatus
import io.zoemeow.dutapp.model.SubjectFeeListItem
import io.zoemeow.dutapp.model.SubjectScheduleListItem
import io.zoemeow.dutapp.network.DutFuncApi
import javax.inject.Inject

class DutAccountApiRepository @Inject constructor(private val api: DutFuncApi) {
    private val ex: MutableState<Exception> = mutableStateOf(Exception())

    // Login/Logout
    suspend fun dutLogin(user: String, pass: String): LoginStatus {
        var result = LoginStatus(loggedIn = false)

        try {
            result = api.dutLogin(user, pass)
        } catch (ex: Exception) {
            this.ex.value = ex
        }

        return result
    }

    suspend fun dutLogout(sid: String): LoginStatus {
        var result = LoginStatus(loggedIn = false)

        try {
            result = api.dutLogout(sid)
        } catch (ex: Exception) {
            this.ex.value = ex
        }

        return result
    }


    // Get subject schedule
    private var subjectScheduleData = SubjectScheduleListItem()
    suspend fun dutGetSubjectSchedule(
        sid: String,
        year: Int,
        semester: Int,
        inSummer: Boolean
    ): SubjectScheduleListItem {
        try {
            subjectScheduleData =
                api.dutGetSubjectSchedule(sid, year, semester, if (inSummer) 1 else 0)
        } catch (ex: Exception) {
            this.ex.value = ex
        }

        return subjectScheduleData
    }

    // Get subject fee
    private var subjectFeeData = SubjectFeeListItem()
    suspend fun dutGetSubjectFee(
        sid: String,
        year: Int,
        semester: Int,
        inSummer: Boolean
    ): SubjectFeeListItem {
        try {
            subjectFeeData = api.dutGetSubjectFee(sid, year, semester, if (inSummer) 1 else 0)
        } catch (ex: Exception) {
            this.ex.value = ex
        }

        return subjectFeeData
    }

    // Settings information
    private var accountInformation = AccountInformationMainItem()
    suspend fun dutGetAccInfo(sid: String): AccountInformationMainItem {
        try {
            accountInformation = api.dutGetAccInfo(sid)
        } catch (ex: Exception) {
            this.ex.value = ex
        }

        return accountInformation
    }
}