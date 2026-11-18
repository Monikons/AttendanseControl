package ru.college.attendance.logic

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.college.attendance.data.AttendanceRecord
import ru.college.attendance.data.AttendanceStatus

class RecordFilterTest {

    private fun record(id: Int, studentId: Int, group: String, date: String) = AttendanceRecord(
        id = id, studentId = studentId, lessonDate = date, subject = "Программирование",
        status = AttendanceStatus.PRESENT, lastName = "Студент$studentId", firstName = "Тест", groupName = group
    )

    private val data = listOf(
        record(1, 1, "ИС-21", "2026-09-01"),
        record(2, 2, "ИС-21", "2026-09-03"),
        record(3, 3, "ИС-22", "2026-09-08")
    )

    @Test
    fun noFilters_returnsAll() {
        assertEquals(3, RecordFilter.apply(data, null, null, "", "").size)
    }

    @Test
    fun filterByGroup() {
        assertEquals(2, RecordFilter.apply(data, "ИС-21", null, null, null).size)
    }

    @Test
    fun filterByStudent() {
        assertEquals(listOf(3), RecordFilter.apply(data, null, 3, null, null).map { it.id })
    }

    @Test
    fun filterByDateRange_isInclusive() {
        val result = RecordFilter.apply(data, null, null, "2026-09-03", "2026-09-08")
        assertEquals(listOf(2, 3), result.map { it.id })
    }

    @Test
    fun noMatches_returnsEmptyList() {
        assertEquals(0, RecordFilter.apply(data, "ИС-99", null, null, null).size)
    }
}
