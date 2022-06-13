package io.zoemeow.dutapp.model.subject

data class SubjectFeeItem(
    val id: String? = null,
    val name: String? = null,
    val credit: Int = 0,
    val is_high_quality: Boolean? = false,
    val price: Int = 0,
    val debt: Boolean = false,
    val is_restudy: Boolean = false,
)