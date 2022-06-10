package io.zoemeow.dutapp.utils

fun dateTimeToString(long: Long): String {
    var count = 0
    var temp = long
    val num = arrayOf(0, 1000, 60, 60, 24)
    val str = arrayOf("millisecond", "second", "minute", "hour", "day")
    return if (temp <= 0) {
        "Happening now"
    }
    else {
        while (count < num.size-1 && temp / num[count+1] > 0) {
            count += 1
            temp /= num[count]
        }
        if (count == 0 || (count == 1 && temp < 30)) {
            "After few seconds"
        }
        else {
            "After $temp ${str[count]}${if (temp > 1) "s" else ""}"
        }
    }
}