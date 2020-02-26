package com.alamkanak.weekview

import android.content.Context
import android.text.format.DateFormat
import java.util.Calendar
import java.util.Locale

interface DateTimeInterpreter {
    fun onSetNumberOfDays(days: Int) {
        // Free ad space
    }
    fun interpretDate(date: Calendar): String
    fun interpretTime(hour: Int, minute: Int = 0): String
}

internal class DefaultDateTimeInterpreter(
    dateFormatProvider: DateFormatProvider,
    numberOfDays: Int
) : DateTimeInterpreter {

    private var sdfDate = getDefaultDateFormat(numberOfDays)
    private val sdfTime = getDefaultTimeFormat(dateFormatProvider.is24HourFormat)

    // This calendar is only used for interpreting the time. To avoid issues with time changes,
    // we always use the first day of the year
    private val calendar = firstDayOfYear()

    override fun onSetNumberOfDays(days: Int) {
        sdfDate = getDefaultDateFormat(days)
    }

    @ExperimentalStdlibApi
    override fun interpretDate(date: Calendar): String =
            sdfDate.format(date.time).capitalize(Locale.getDefault())

    override fun interpretTime(hour: Int, minute: Int): String =
            sdfTime.format(calendar.withTime(hour, minutes = minute).time)
}

internal interface DateFormatProvider {
    val is24HourFormat: Boolean
}

internal class RealDateFormatProvider(
    private val context: Context
) : DateFormatProvider {

    override val is24HourFormat: Boolean
        get() = DateFormat.is24HourFormat(context)
}
