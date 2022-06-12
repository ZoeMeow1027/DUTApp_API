package io.zoemeow.dutapp.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.File
import javax.inject.Inject

class ExceptionFileRepository @Inject constructor(private val file: File) {
    private val appSettings: ArrayList<Exception> = ArrayList()
    private val debug: Boolean = true

    fun writeToFile(ex: Exception) {
        appSettings.add(ex)
        exportSettings()

        if (debug)
            ex.printStackTrace()
    }

    private fun importSettings() {
        try {
            val buffer: BufferedReader = file.bufferedReader()
            val inputStr = buffer.use { it.readText() }
            buffer.close()

            val itemType = object : TypeToken<ArrayList<Exception>>() {}.type
            val variableItemTemp = Gson().fromJson<ArrayList<Exception>>(inputStr, itemType)
            appSettings.clear()
            appSettings.addAll(variableItemTemp)
            variableItemTemp.clear()
        }
        catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun exportSettings() {
        try {
            val str = Gson().toJson(appSettings)
            file.writeText(str)
        }
        catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    init {
        importSettings()
    }
}