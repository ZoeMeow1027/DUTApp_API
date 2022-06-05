package io.zoemeow.dutapp.repository

import com.google.gson.Gson
import io.zoemeow.dutapp.model.account.AccountInformationItem
import io.zoemeow.dutapp.model.cache.SubjectCacheItem
import io.zoemeow.dutapp.model.subject.SubjectFeeItem
import io.zoemeow.dutapp.model.subject.SubjectScheduleItem
import java.io.BufferedReader
import java.io.File
import javax.inject.Inject

class SubjectCacheFileRepository @Inject constructor(
    private val file: File
) {
    private var accountCache: SubjectCacheItem = SubjectCacheItem()

    var subjectScheduleUpdateTime: Long
        get() = accountCache.subjectScheduleUpdateTime
        set(value) {
            accountCache.subjectScheduleUpdateTime = value
            exportSettings()
        }

    var subjectFeeUpdateTime: Long
        get() = accountCache.subjectFeeUpdateTime
        set(value) {
            accountCache.subjectFeeUpdateTime = value
            exportSettings()
        }

    var accountInformationUpdateTime: Long
        get() = accountCache.accountInformationUpdateTime
        set(value) {
            accountCache.accountInformationUpdateTime = value
            exportSettings()
        }

    fun setSubjectSchedule(list: ArrayList<SubjectScheduleItem>, append: Boolean = false) {
        if (!append) {
            accountCache.subjectScheduleData.clear()
            accountCache.subjectScheduleData.addAll(list)
        }
        else {
            // TODO: Append news global here.
        }
        exportSettings()
    }

    fun getSubjectSchedule(): ArrayList<SubjectScheduleItem> {
        return accountCache.subjectScheduleData
    }

    fun getSubjectSchedule(id: String): SubjectScheduleItem? {
        return accountCache.subjectScheduleData.firstOrNull { it.id == id }
    }

    fun deleteSubjectSchedule(item: SubjectScheduleItem) {
        accountCache.subjectScheduleData.remove(item)
        exportSettings()
    }

    fun deleteAllSubjectSchedule() {
        accountCache.subjectScheduleData.clear()
        exportSettings()
    }

    fun setSubjectFee(list: ArrayList<SubjectFeeItem>, append: Boolean = false) {
        if (!append) {
            accountCache.subjectFeeData.clear()
            accountCache.subjectFeeData.addAll(list)
        }
        else {
            // TODO: Append news global here.
        }
        exportSettings()
    }

    fun getSubjectFee(): ArrayList<SubjectFeeItem> {
        return accountCache.subjectFeeData
    }

    fun getSubjectFee(id: String): SubjectFeeItem? {
        return accountCache.subjectFeeData.firstOrNull { it.id == id }
    }

    fun deleteSubjectFee(item: SubjectFeeItem) {
        accountCache.subjectFeeData.remove(item)
        exportSettings()
    }

    fun deleteAllSubjectFee() {
        accountCache.subjectFeeData.clear()
        exportSettings()
    }

    fun setAccountInformation(item: AccountInformationItem) {
        accountCache.accountInformationData = item
        exportSettings()
    }

    fun getAccountInformation(): AccountInformationItem {
        return accountCache.accountInformationData
    }

    fun deleteAccountInformation() {
        accountCache.accountInformationData = AccountInformationItem()
        exportSettings()
    }

    fun getSubjectCreditTotal(): Int {
        return accountCache.subjectCreditTotal
    }

    fun setSubjectCreditTotal(value: Int) {
        accountCache.subjectCreditTotal = value
        exportSettings()
    }

    fun getSubjectMoneyTotal(): Long {
        return accountCache.subjectMoneyTotal
    }

    fun setSubjectMoneyTotal(value: Long) {
        accountCache.subjectMoneyTotal = value
        exportSettings()
    }

    private fun importSettings() {
        try {
            val buffer: BufferedReader = file.bufferedReader()
            val inputStr = buffer.use { it.readText() }
            buffer.close()
            accountCache = Gson().fromJson(inputStr, SubjectCacheItem::class.java)
        }
        catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun exportSettings() {
        try {
            val str = Gson().toJson(accountCache)
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