package io.zoemeow.dutapp.network

import io.zoemeow.dutapp.model.account.AccountInformationMainItem
import io.zoemeow.dutapp.model.account.LoginStatus
import io.zoemeow.dutapp.model.news.NewsGlobalListItem
import io.zoemeow.dutapp.model.news.NewsSubjectListItem
import io.zoemeow.dutapp.model.subject.SubjectFeeListItem
import io.zoemeow.dutapp.model.subject.SubjectScheduleListItem
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DutFuncApi {
    @GET("/news?type=global")
    suspend fun getNewsGlobal(
        @Query(value = "page") page: Int = 1
    ): NewsGlobalListItem

    @GET("/news?type=subjects")
    suspend fun getNewsSubject(
        @Query(value = "page") page: Int = 1
    ): NewsSubjectListItem

    @POST("/account?type=login")
    suspend fun dutLogin(
        @Query(value = "user") user: String,
        @Query(value = "pass") pass: String
    ): LoginStatus

    @POST("/account?type=logout")
    suspend fun dutLogout(
        @Query(value = "sid") sid: String,
    ): LoginStatus

    @POST("/account?type=subjectschedule")
    suspend fun dutGetSubjectSchedule(
        @Query(value = "sid") sid: String,
        @Query(value = "year") year: Int,
        @Query(value = "semester") semester: Int,
        @Query(value = "insummer") insummer: Int
    ): SubjectScheduleListItem

    @POST("/account?type=subjectfee")
    suspend fun dutGetSubjectFee(
        @Query(value = "sid") sid: String,
        @Query(value = "year") year: Int,
        @Query(value = "semester") semester: Int,
        @Query(value = "insummer") insummer: Int
    ): SubjectFeeListItem

    @POST("/account?type=accinfo")
    suspend fun dutGetAccInfo(
        @Query(value = "sid") sid: String
    ): AccountInformationMainItem
}