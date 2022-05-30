package io.zoemeow.dutapp.repository

import com.google.gson.Gson
import io.zoemeow.dutapp.model.AppSettings
import java.io.BufferedReader
import java.io.File
import javax.inject.Inject

class AppSettingsRepository @Inject constructor(private val file: File) {
    private var appSettings: AppSettings = AppSettings()

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

    private fun importSettings() {
        try {
            val buffer: BufferedReader = file.bufferedReader()
            val inputStr = buffer.use { it.readText() }
            buffer.close()
            appSettings = Gson().fromJson(inputStr, AppSettings::class.java)
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