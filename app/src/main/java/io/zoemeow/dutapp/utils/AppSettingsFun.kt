package io.zoemeow.dutapp.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.File
import io.zoemeow.dutapp.model.AppSettings

class AppSettingsFun {
    var appSettings: AppSettings = AppSettings()

    private var filePath = "/data/data/io.zoemeow.dutapp/files/appSettings.json"

    fun importSettings() {
        try {
            val buffer: BufferedReader = File(filePath).bufferedReader()
            val inputStr = buffer.use { it.readText() }
            buffer.close()
            appSettings = Gson().fromJson(inputStr, AppSettings::class.java)
        }
        catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    var username: String?
        get() = appSettings.username
        set(value) {
            appSettings.username = value
            exportSettings()
        }

    var password: String?
        get() = appSettings.password
        set(value) {
            appSettings.password = value
            exportSettings()
        }

    var autoLogin: Boolean
        get() = appSettings.autoLogin
        set(value) {
            appSettings.autoLogin = value
            exportSettings()
        }

    var Instance: AppSettings
        get() = appSettings
        set(value) {
            appSettings = value
            exportSettings()
        }

    private fun exportSettings() {
        try {
            var str = Gson().toJson(appSettings)
            File(filePath).writeText(str)
        }
        catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}