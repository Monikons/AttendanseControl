package ru.college.attendance.logic

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.college.attendance.data.AttendanceRecord
import ru.college.attendance.data.AttendanceStatus

class StatsCalculatorTest {

    private fun record(id: Int, status: String) = AttendanceRecord(
        id = id, studentId = 1, lessonDate = "2026-09-01", subject = "Программирование",
        status = status, lastName = "Иванов", firstName = "Пётр", groupName = "ИС-21"
    )

    @Test
    fun emptyList_givesZeros() {
        assertEquals(Stats(0, 0, 0, 0), StatsCalculator.calculate(emptyList()))
        assertEquals(0, StatsCalculator.calculate(emptyList()).attendancePercent)
    }

    @Test
    fun countsEachStatus() {
        val records = listOf(
            record(1, AttendanceStatus.PRESENT),
            record(2, AttendanceStatus.PRESENT),
            record(3, AttendanceStatus.ABSENT),
            record(4, AttendanceStatus.LATE)
        )
        assertEquals(Stats(4, 2, 1, 1), StatsCalculator.calculate(records))
    }

    @Test
    fun attendancePercent_countsLateAsVisited() {
        // 2 присутствий + 1 опоздание из 4 отметок = 75%
        assertEquals(75, Stats(4, 2, 1, 1).attendancePercent)
        assertEquals(100, Stats(3, 3, 0, 0).attendancePercent)
        assertEquals(0, Stats(2, 0, 2, 0).attendancePercent)
    }
}
