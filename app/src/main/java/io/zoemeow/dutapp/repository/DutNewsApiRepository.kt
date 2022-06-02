package io.zoemeow.dutapp.repository

import android.util.Log
import io.zoemeow.dutapp.model.news.NewsGlobalListItem
import io.zoemeow.dutapp.model.news.NewsSubjectListItem
import io.zoemeow.dutapp.network.DutFuncApi
import javax.inject.Inject

class DutNewsApiRepository @Inject constructor(private val api: DutFuncApi) {
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
}