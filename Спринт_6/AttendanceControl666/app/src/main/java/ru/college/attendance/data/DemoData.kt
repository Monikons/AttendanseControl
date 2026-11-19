package ru.college.attendance.data

/** Вымышленные данные для демонстрации: 10 студентов, 3 занятия, 30 отметок. */
object DemoData {

    private val students = listOf(
        Triple("Иванов", "Пётр", "ИС-21"),
        Triple("Смирнова", "Анна", "ИС-21"),
        Triple("Кузнецов", "Дмитрий", "ИС-21"),
        Triple("Попова", "Мария", "ИС-21"),
        Triple("Соколов", "Алексей", "ИС-21"),
        Triple("Лебедева", "Ольга", "ИС-21"),
        Triple("Новиков", "Сергей", "ИС-22"),
        Triple("Морозова", "Елена", "ИС-22"),
        Triple("Волков", "Андрей", "ИС-22"),
        Triple("Фёдорова", "Ирина", "ИС-22")
    )

    private val lessons = listOf(
        "2026-09-01" to "Программирование",
        "2026-09-03" to "Базы данных",
        "2026-09-08" to "Программирование"
    )

    /** Загружает данные только в пустую базу. Возвращает true, если данные добавлены. */
    suspend fun load(db: AppDatabase): Boolean {
        if (db.studentDao().count() > 0) return false

        val ids = students.map { (last, first, group) ->
            db.studentDao().insert(StudentEntity(lastName = last, firstName = first, groupName = group)).toInt()
        }

        val marks = mutableListOf<AttendanceEntity>()
        ids.forEachIndexed { studentIndex, studentId ->
            lessons.forEachIndexed { lessonIndex, (date, subject) ->
                // Детерминированный набор статусов: 1 из 5 - пропуск, 1 из 5 - опоздание
                val status = when ((studentIndex + lessonIndex) % 5) {
                    0 -> AttendanceStatus.ABSENT
                    1 -> AttendanceStatus.LATE
                    else -> AttendanceStatus.PRESENT
                }
                marks.add(AttendanceEntity(studentId = studentId, lessonDate = date, subject = subject, status = status))
            }
        }
        db.attendanceDao().upsertAll(marks)
        return true
    }
}
