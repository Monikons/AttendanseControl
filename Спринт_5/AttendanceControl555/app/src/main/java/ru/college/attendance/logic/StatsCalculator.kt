package ru.college.attendance.logic

import ru.college.attendance.data.AttendanceRecord
import ru.college.attendance.data.AttendanceStatus

data class Stats(
    val total: Int,
    val present: Int,
    val absent: Int,
    val late: Int
) {
    /** Посещаемость, %: (присутствовал + опоздал) / все отметки. Для пустого списка 0. */
    val attendancePercent: Int
        get() = if (total == 0) 0 else Math.round((present + late) * 100f / total)
}

object StatsCalculator {
    fun calculate(records: List<AttendanceRecord>): Stats {
        val total = records.size
        val present = records.count { it.status == AttendanceStatus.PRESENT }
        val absent = records.count { it.status == AttendanceStatus.ABSENT }
        val late = records.count { it.status == AttendanceStatus.LATE }
        return Stats(total, present, absent, late)
    }
}
