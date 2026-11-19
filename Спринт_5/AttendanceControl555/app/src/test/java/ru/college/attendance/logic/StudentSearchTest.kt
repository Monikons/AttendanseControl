package ru.college.attendance.logic

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.college.attendance.data.StudentEntity

class StudentSearchTest {

    private val list = listOf(
        StudentEntity(1, "Пётр", "Иванов", "ИС-21"),
        StudentEntity(2, "Анна", "Смирнова", "ИС-22")
    )

    @Test
    fun emptyQuery_returnsAll() {
        assertEquals(2, StudentSearch.filter(list, "  ").size)
    }

    @Test
    fun searchByLastName_ignoresCase() {
        assertEquals(listOf(1), StudentSearch.filter(list, "ИВАН").map { it.id })
    }

    @Test
    fun searchByGroup() {
        assertEquals(listOf(2), StudentSearch.filter(list, "ис-22").map { it.id })
    }

    @Test
    fun noMatches_returnsEmptyList() {
        assertEquals(0, StudentSearch.filter(list, "Петров").size)
    }
}
