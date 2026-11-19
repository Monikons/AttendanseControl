package ru.college.attendance.logic

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.college.attendance.data.AttendanceRecord
import ru.college.attendance.data.AttendanceStatus

class ReportBuilderTest {

    private val record = AttendanceRecord(
        id = 1, studentId = 1, lessonDate = "2026-09-01", subject = "Программирование",
        status = AttendanceStatus.PRESENT, lastName = "Иванов", firstName = "Пётр", groupName = "ИС-21"
    )

    @Test
    fun report_containsSummaryAndFilters() {
        val text = ReportBuilder.build(listOf(record), Stats(1, 1, 0, 0), "группа ИС-21")
        assertTrue(text.contains("Фильтры: группа ИС-21"))
        assertTrue(text.contains("Занятий: 1"))
        assertTrue(text.contains("Посещаемость: 100%"))
        assertTrue(text.contains("Иванов Пётр (ИС-21)"))
    }

    @Test
    fun emptyReport_hasNoMarksSection() {
        val text = ReportBuilder.build(emptyList(), Stats(0, 0, 0, 0), "без фильтров")
        assertFalse(text.contains("Отметки:"))
        assertTrue(text.contains("Посещаемость: 0%"))
    }
}
