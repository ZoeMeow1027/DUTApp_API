package io.zoemeow.dutapp.repository

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.zoemeow.dutapp.model.VariableItem
import java.io.BufferedReader
import java.io.File
import javax.inject.Inject

class AppSettingsFileRepository @Inject constructor(private val file: File)  {
    private val appSettings: ArrayList<VariableItem> = ArrayList()
    internal val changedCount: MutableState<Long> = mutableStateOf(0)

    operator fun get(key: String): VariableItem {
        return try {
            val temp = appSettings.firstOrNull {
                it.key == key
            } ?: throw Exception("temp is null!")

            val d = VariableItem(temp.key, temp.value)
            d
        } catch (ex: Exception) {
            ex.printStackTrace()
            VariableItem(key = key, value = null)
        }
    }

    operator fun set(key: String, value: String) {
        try {
            if (appSettings.firstOrNull { it.key == key } != null)
                appSettings.firstOrNull { it.key == key }?.value = value
            else appSettings.add(VariableItem(key = key, value = value))
        }
        catch (ex: Exception) {
            ex.printStackTrace()
        }
        changedCount.value += 1
        exportSettings()
    }

    private fun importSettings() {
        try {
            val buffer: BufferedReader = file.bufferedReader()
            val inputStr = buffer.use { it.readText() }
            buffer.close()

            val itemType = object : TypeToken<ArrayList<VariableItem>>() {}.type
            val variableItemTemp = Gson().fromJson<ArrayList<VariableItem>>(inputStr, itemType)
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