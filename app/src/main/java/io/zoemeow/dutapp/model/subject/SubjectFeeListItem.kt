package io.zoemeow.dutapp.model.subject

data class SubjectFeeListItem(
    val date: Long? = null,
    val total_credit: Int? = null,
    val total_money: Long? = null,
    val fee_list: ArrayList<SubjectFeeItem>? = null
)