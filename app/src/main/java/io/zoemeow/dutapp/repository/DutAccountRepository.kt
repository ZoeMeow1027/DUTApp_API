package io.zoemeow.dutapp.repository

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.zoemeow.dutapp.model.LoginStatus
import io.zoemeow.dutapp.model.SubjectFeeListItem
import io.zoemeow.dutapp.model.SubjectScheduleListItem
import io.zoemeow.dutapp.network.DutFuncApi
import javax.inject.Inject

class DutAccountRepository @Inject constructor(private val api: DutFuncApi) {
    // Login/Logout
    private val loading: MutableState<Boolean> = mutableStateOf(false)
    private val ex: MutableState<Exception> = mutableStateOf(Exception())

    suspend fun dutLogin(user: String, pass: String): LoginStatus {
        var result = LoginStatus(loggedin = false)

        try {
            if (loading.value)
                throw Exception("Another process is running!")
            loading.value = true

            result = api.dutLogin(user, pass)
        } catch (ex: Exception) {
            this.ex.value = ex
        }

        loading.value = false
        return result
    }

    suspend fun dutLogout(sid: String): LoginStatus {
        var result = LoginStatus(loggedin = false)

        try {
            if (loading.value)
                throw Exception("Another process is running!")
            loading.value = true

            result = api.dutLogout(sid)
        } catch (ex: Exception) {
            this.ex.value = ex
        }

        loading.value = false
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
            if (loading.value)
                throw Exception("Another process is running!")
            loading.value = true

            subjectScheduleData =
                api.dutGetSubjectSchedule(sid, year, semester, if (inSummer) 1 else 0)
        } catch (ex: Exception) {
            this.ex.value = ex
        }

        loading.value = false
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
            if (loading.value)
                throw Exception("Another process is running!")
            loading.value = true

            subjectFeeData = api.dutGetSubjectFee(sid, year, semester, if (inSummer) 1 else 0)
        } catch (ex: Exception) {
            this.ex.value = ex
        }

        loading.value = false
        return subjectFeeData
    }
}