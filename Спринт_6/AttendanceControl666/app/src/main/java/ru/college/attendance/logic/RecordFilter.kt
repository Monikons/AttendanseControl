package ru.college.attendance.logic

import ru.college.attendance.data.AttendanceRecord

object RecordFilter {
    const val ALL_GROUPS = "Все группы"
    const val ALL_STUDENTS = "Все студенты"

    /** null или пустая строка в параметре означает «без ограничения». */
    fun apply(
        records: List<AttendanceRecord>,
        groupName: String?,
        studentId: Int?,
        dateFrom: String?,
        dateTo: String?
    ): List<AttendanceRecord> = records.filter { r ->
        (groupName == null || r.groupName == groupName) &&
            (studentId == null || r.studentId == studentId) &&
            (dateFrom.isNullOrBlank() || r.lessonDate >= dateFrom) &&
            (dateTo.isNullOrBlank() || r.lessonDate <= dateTo)
    }
}
