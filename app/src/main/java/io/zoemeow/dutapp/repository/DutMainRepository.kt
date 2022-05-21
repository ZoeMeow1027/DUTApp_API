package io.zoemeow.dutapp.repository

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.zoemeow.dutapp.model.LoginStatus
import io.zoemeow.dutapp.model.NewsItem
import io.zoemeow.dutapp.model.NewsType
import io.zoemeow.dutapp.network.DutFuncApi
import javax.inject.Inject

class DutMainRepository @Inject constructor(private val api: DutFuncApi) {
    private var data = NewsItem()
    private var loading: Boolean = false
    private var ex: Exception? = null

    suspend fun getAllNews(newsType: NewsType, page: Int = 1): NewsItem {
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

    private val loading2: MutableState<Boolean> = mutableStateOf(false)
    private val ex2: MutableState<Exception> = mutableStateOf(Exception())

    suspend fun dutLogin(user: String, pass: String): LoginStatus {
        var result: LoginStatus = LoginStatus(loggedin = false)

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
        var result: LoginStatus = LoginStatus(loggedin = false)

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
}