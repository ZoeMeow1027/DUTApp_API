package io.zoemeow.dutapp.utils

import java.util.*

fun getDayOfWeek(): Int {
    val day: Int = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    // Sunday as 1, so if we want to equal to host server (Sunday = 0),
    // we need to decrease 1 here.
    return day - 1
}