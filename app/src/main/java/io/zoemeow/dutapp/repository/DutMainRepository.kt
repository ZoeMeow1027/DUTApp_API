package io.zoemeow.dutapp.repository

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.zoemeow.dutapp.model.*
import io.zoemeow.dutapp.network.DutFuncApi
import javax.inject.Inject

class DutMainRepository @Inject constructor(private val api: DutFuncApi) {
    // Get news
    private var data = NewsListItem()
    private var loading: Boolean = false
    private var ex: Exception? = null

    suspend fun getAllNews(newsType: NewsType, page: Int = 1): NewsListItem {
        try {
            loading = true
            data = api.getNews(
                if (newsType == NewsType.Global)
                    "global"
                else "subjects",
                page
            )
        } catch (ex: Exception) {
            this.ex = ex
        }

        loading = false
        return data
    }


    // Login/Logout
    private val loading2: MutableState<Boolean> = mutableStateOf(false)
    private val ex2: MutableState<Exception> = mutableStateOf(Exception())

    suspend fun dutLogin(user: String, pass: String): LoginStatus {
        var result = LoginStatus(loggedin = false)

        try {
            if (loading2.value)
                throw Exception("Another process is running!")
            loading2.value = true

            result = api.dutLogin(user, pass)
        } catch (ex: Exception) {
            this.ex2.value = ex
        }

        loading2.value = false
        return result
    }

    suspend fun dutLogout(sid: String): LoginStatus {
        var result = LoginStatus(loggedin = false)

        try {
            if (loading2.value)
                throw Exception("Another process is running!")
            loading2.value = true

            result = api.dutLogout(sid)
        } catch (ex: Exception) {
            this.ex2.value = ex
        }

        loading2.value = false
        return result
    }


    // Get subject schedule
    private var subjectScheduleData = SubjectScheduleListItem()
    suspend fun dutGetSubjectSchedule(sid: String, year: Int, semester: Int, inSummer: Boolean): SubjectScheduleListItem {
        try {
            if (loading2.value)
                throw Exception("Another process is running!")
            loading2.value = true

            subjectScheduleData = api.dutGetSubjectSchedule(sid, year, semester, if (inSummer) 1 else 0)
        } catch (ex: Exception) {
            this.ex2.value = ex
        }

        loading2.value = false
        return subjectScheduleData
    }

    // Get subject fee
    private var subjectFeeData = SubjectFeeListItem()
    suspend fun dutGetSubjectFee(sid: String, year: Int, semester: Int, inSummer: Boolean): SubjectFeeListItem {
        try {
            if (loading2.value)
                throw Exception("Another process is running!")
            loading2.value = true

            subjectFeeData = api.dutGetSubjectFee(sid, year, semester, if (inSummer) 1 else 0)
        } catch (ex: Exception) {
            this.ex2.value = ex
        }

        loading2.value = false
        return subjectFeeData
    }
}