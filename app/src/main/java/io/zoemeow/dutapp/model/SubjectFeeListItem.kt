package io.zoemeow.dutapp.model

data class SubjectFeeListItem(
    val date: Long? = null,
    val totalcredit: Int? = null,
    val totalmoney: Int? = null,
    val feelist: List<SubjectFeeItem>? = null
)