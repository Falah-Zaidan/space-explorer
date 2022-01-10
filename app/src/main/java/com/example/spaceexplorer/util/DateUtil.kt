package com.example.spaceexplorer.util

import java.text.SimpleDateFormat
import java.util.*

class DateUtil {
    companion object {
        fun formatDate(date: String): String {
            return convertToDate(convertFromDate(date))
        }

        fun convertToDate(long: Long): String {
            val reformattedDate = SimpleDateFormat("yyyy-MM-dd").format(Date(long))

            return reformattedDate
        }

        fun convertFromDate(date: String): Long {
            if (!date.equals("")) {
                val df = SimpleDateFormat("yyyy-MM-dd")
                return df.parse(date).time
            }

            return -1
        }
    }
}
