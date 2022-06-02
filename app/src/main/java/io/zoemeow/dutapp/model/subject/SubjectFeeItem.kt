package io.zoemeow.dutapp.model.subject

data class SubjectFeeItem(
    val ID: String? = null,
    val Name: String? = null,
    val Credit: Int = 0,
    val IsHighQuality: Boolean = false,
    val Price: Int = 0,
    val Debt: Boolean = false,
    val IsReStudy: Boolean = false,
)
