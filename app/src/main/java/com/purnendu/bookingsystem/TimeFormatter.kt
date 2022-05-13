package com.purnendu.bookingsystem

import java.text.SimpleDateFormat
import java.util.*

object TimeFormatter {

    fun dateFormat(): SimpleDateFormat {
        val myFormat = "EEE, d MMM yyyy"
        val local = Locale("English")
        return  SimpleDateFormat(myFormat, local)
    }

}