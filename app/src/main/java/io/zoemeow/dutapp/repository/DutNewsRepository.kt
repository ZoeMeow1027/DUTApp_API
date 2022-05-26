package io.zoemeow.dutapp.repository

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import io.zoemeow.dutapp.model.NewsGlobalItem
import io.zoemeow.dutapp.model.NewsGlobalListItem
import io.zoemeow.dutapp.model.NewsSubjectItem
import io.zoemeow.dutapp.model.NewsSubjectListItem
import io.zoemeow.dutapp.network.DutFuncApi
import javax.inject.Inject

class DutNewsRepository @Inject constructor(private val api: DutFuncApi) {
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