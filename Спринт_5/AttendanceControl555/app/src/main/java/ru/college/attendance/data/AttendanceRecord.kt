package ru.college.attendance.data

/** Строка истории: отметка + данные студента (результат JOIN-запроса). */
data class AttendanceRecord(
    val id: Int,
    val studentId: Int,
    val lessonDate: String,
    val subject: String,
    val status: String,
    val lastName: String,
    val firstName: String,
    val groupName: String
)
