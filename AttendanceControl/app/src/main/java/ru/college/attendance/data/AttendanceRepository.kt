package ru.college.attendance.data

/**
 * Хранилище отметок в памяти (спринт 3).
 * Правило: одна итоговая отметка на студента и занятие (дата + дисциплина).
 */
object AttendanceRepository {
    private val records = mutableListOf<Attendance>()
    private var nextId = 1

    fun saveMark(studentId: Int, lessonDate: String, subject: String, status: String) {
        records.removeAll {
            it.studentId == studentId &&
                it.lessonDate == lessonDate &&
                it.subject.equals(subject, ignoreCase = true)
        }
        records.add(Attendance(nextId++, studentId, lessonDate, subject, status))
    }

    fun find(studentId: Int, lessonDate: String, subject: String): Attendance? =
        records.firstOrNull {
            it.studentId == studentId &&
                it.lessonDate == lessonDate &&
                it.subject.equals(subject, ignoreCase = true)
        }

    fun getAll(): List<Attendance> = records.toList()

    fun clear() {
        records.clear()
        nextId = 1
    }
}
