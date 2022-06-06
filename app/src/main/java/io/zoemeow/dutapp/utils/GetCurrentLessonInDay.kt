package io.zoemeow.dutapp.utils

import java.util.*
import kotlin.time.Duration

fun getCurrentLesson(): Int {
    val hoursLesson = arrayOf(0, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 19, 20)
    val minuteLesson = arrayOf(0, 0, 0, 0, 0, 0, 30, 30, 30, 30, 30, 30, 15, 10, 55, 40)
    val hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val minute = Calendar.getInstance().get(Calendar.MINUTE)

    if (hours > hoursLesson[hoursLesson.size - 1] ||
        (hours == hoursLesson[hoursLesson.size - 1] && minute > minuteLesson[minuteLesson.size - 1])
    ) return hoursLesson.size - 1


    for (i in 0..(hoursLesson.size - 2)) {
        val d1 = Duration.parse("${hoursLesson[i]}h ${minuteLesson[i]}m")
        val d2 = Duration.parse("${hoursLesson[i+1]}h ${minuteLesson[i+1]}m")
        val current = Duration.parse("${hours}h ${minute}m")

        if (current > d1 && current < d2)
            return i
    }
    return -1
}