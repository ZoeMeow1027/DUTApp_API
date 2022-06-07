package io.zoemeow.dutapp.utils

import java.math.BigInteger
import java.security.MessageDigest

// Generate md5 from string.
fun getMD5FromString(input: String): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}