package ru.college.attendance.logic

import ru.college.attendance.data.StudentEntity

object StudentSearch {
    /** Ищет по фамилии, имени и группе без учёта регистра. Пустой запрос возвращает всех. */
    fun filter(students: List<StudentEntity>, query: String): List<StudentEntity> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return students
        return students.filter { "${it.lastName} ${it.firstName} ${it.groupName}".lowercase().contains(q) }
    }
}
