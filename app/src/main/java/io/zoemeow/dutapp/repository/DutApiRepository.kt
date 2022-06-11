package io.zoemeow.dutapp.repository

import android.util.Log
import io.zoemeow.dutapp.model.account.AccountInformationMainItem
import io.zoemeow.dutapp.model.account.LoginStatus
import io.zoemeow.dutapp.model.news.NewsGlobalListItem
import io.zoemeow.dutapp.model.news.NewsSubjectListItem
import io.zoemeow.dutapp.model.subject.SubjectFeeListItem
import io.zoemeow.dutapp.model.subject.SubjectScheduleListItem
import io.zoemeow.dutapp.network.DutFuncApi
import javax.inject.Inject

class DutApiRepository @Inject constructor(private val api: DutFuncApi) {
    private var ex: Exception? = null

    // Get news global
    private var dataGlobal = NewsGlobalListItem()
    suspend fun getNewsGlobal(page: Int = 1): NewsGlobalListItem {
        try {
            dataGlobal = api.getNewsGlobal(page)
        } catch (ex: Exception) {
            this.ex = ex
        }

        if (ex != null)
            Log.d("DutNewsRepo", ex?.message.toString())
        return dataGlobal
    }

    // Get news subjects
    private var dataSubject = NewsSubjectListItem()
    suspend fun getNewsSubject(page: Int = 1): NewsSubjectListItem {
        try {
            dataSubject = api.getNewsSubject(page)
        } catch (ex: Exception) {
            this.ex = ex
        }

        if (ex != null)
            Log.d("DutNewsRepo", ex?.message.toString())
        return dataSubject
    }

    // Login/Logout
    suspend fun dutLogin(user: String, pass: String): LoginStatus {
        var result = LoginStatus(logged_in = false)

        try {
            result = api.dutLogin(user, pass)
        } catch (ex: Exception) {
            this.ex = ex
        }

        return result
    }

    suspend fun dutLogout(sid: String): LoginStatus {
        var result = LoginStatus(logged_in = false)

        try {
            result = api.dutLogout(sid)
        } catch (ex: Exception) {
            this.ex = ex
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
            this.ex = ex
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
            this.ex = ex
        }

        return subjectFeeData
    }

    // Settings information
    private var accountInformation = AccountInformationMainItem()
    suspend fun dutGetAccInfo(sid: String): AccountInformationMainItem {
        try {
            accountInformation = api.dutGetAccInfo(sid)
        } catch (ex: Exception) {
            this.ex = ex
        }

        return accountInformation
    }
}