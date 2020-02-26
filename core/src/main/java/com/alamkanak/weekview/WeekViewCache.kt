package com.alamkanak.weekview

import android.util.SparseArray

internal class WeekViewCache<T> {

    val allDayEventLayouts = arrayListOf<EventChip<T>>()
    val dayLabelCache = SparseArray<String>()

    fun clearAllDayEventLayouts() {
        allDayEventLayouts.clear()
    }
}
