package com.heronet.sellnetbeta.util

import java.text.SimpleDateFormat

object DateParser {
    private val parser by lazy { SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss") }
    private val formatter by lazy { SimpleDateFormat("dd-MM-yyyy HH:mm a") }
    fun getFormattedDate(date: String): String {
        return formatter.format(parser.parse(date)!!)
    }
}