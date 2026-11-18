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
}
