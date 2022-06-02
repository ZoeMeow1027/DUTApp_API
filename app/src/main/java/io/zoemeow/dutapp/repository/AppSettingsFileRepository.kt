package io.zoemeow.dutapp.repository

import com.google.gson.Gson
import io.zoemeow.dutapp.model.AppSettings
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

    var subjectDefault: Boolean
        get() = appSettings.subjectSchoolYearSettings.subjectDefault
        set(value) {
            appSettings.subjectSchoolYearSettings.subjectDefault = value
            exportSettings()
        }

    var subjectYear: Int
        get() = appSettings.subjectSchoolYearSettings.subjectYear
        set(value) {
            appSettings.subjectSchoolYearSettings.subjectYear = value
            exportSettings()
        }

    var subjectSemester: Int
        get() = appSettings.subjectSchoolYearSettings.subjectSemester
        set(value) {
            appSettings.subjectSchoolYearSettings.subjectSemester = value
            exportSettings()
        }

    var subjectInSummer: Boolean
        get() = appSettings.subjectSchoolYearSettings.subjectInSummer
        set(value) {
            appSettings.subjectSchoolYearSettings.subjectInSummer = value
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