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
    // Get news
    private var dataGlobal = NewsGlobalListItem()
    private var dataSubject = NewsSubjectListItem()
    private var loading: Boolean = false
    private var ex: Exception? = null

    suspend fun getNewsGlobal(page: Int = 1): NewsGlobalListItem {
        try {
            loading = true
            dataGlobal = api.getNewsGlobal(page)
        } catch (ex: Exception) {
            this.ex = ex
        }

        loading = false
        if (ex != null)
            Log.d("DutNewsRepo", ex?.message.toString())
        return dataGlobal
    }

    suspend fun getNewsSubject(page: Int = 1): NewsSubjectListItem {
        try {
            loading = true
            dataSubject = api.getNewsSubject(page)
        } catch (ex: Exception) {
            this.ex = ex
        }

        loading = false
        if (ex != null)
            Log.d("DutNewsRepo", ex?.message.toString())
        return dataSubject
    }
}