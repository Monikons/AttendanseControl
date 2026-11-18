package ru.college.attendance.logic

import ru.college.attendance.data.AttendanceRecord
import ru.college.attendance.data.AttendanceStatus

data class Stats(
    val total: Int,
    val present: Int,
    val absent: Int,
    val late: Int
)

object StatsCalculator {
    fun calculate(records: List<AttendanceRecord>): Stats {
        val total = records.size
        val present = records.count { it.status == AttendanceStatus.PRESENT }
        val absent = records.count { it.status == AttendanceStatus.ABSENT }
        val late = records.count { it.status == AttendanceStatus.LATE }
        return Stats(total, present, absent, late)
    }
}
