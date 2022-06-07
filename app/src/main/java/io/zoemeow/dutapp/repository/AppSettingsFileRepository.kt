package io.zoemeow.dutapp.repository

import com.google.gson.Gson
import io.zoemeow.dutapp.model.AppSettings
import io.zoemeow.dutapp.model.subject.SubjectSchoolYearSettings
import java.io.BufferedReader
import java.io.File
import javax.inject.Inject

class AppSettingsFileRepository @Inject constructor(private val file: File) {
    private var appSettings: AppSettings = AppSettings()

    var username: String?
        get() = appSettings.autoLoginSettings.username
        set(value) {
            appSettings.autoLoginSettings.username = value
            exportSettings()
        }

    var password: String?
        get() = appSettings.autoLoginSettings.password
        set(value) {
            appSettings.autoLoginSettings.password = value
            exportSettings()
        }

    var autoLogin: Boolean
        get() = appSettings.autoLoginSettings.autoLogin
        set(value) {
            appSettings.autoLoginSettings.autoLogin = value
            exportSettings()
        }

    var subjectSchoolYearSettings: SubjectSchoolYearSettings
        get() = appSettings.subjectSchoolYearSettings
        set(value) {
            appSettings.subjectSchoolYearSettings = value
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