package ru.college.attendance.logic

import ru.college.attendance.data.AttendanceRecord

object ReportBuilder {
    /** Текстовый отчёт: фильтры, сводка и список отметок. */
    fun build(records: List<AttendanceRecord>, stats: Stats, filters: String): String {
        val sb = StringBuilder()
        sb.append("Отчёт о посещаемости\n")
        sb.append("Фильтры: ").append(filters).append("\n\n")
        sb.append("Занятий: ").append(stats.total).append("\n")
        sb.append("Присутствий: ").append(stats.present).append("\n")
        sb.append("Пропусков: ").append(stats.absent).append("\n")
        sb.append("Опозданий: ").append(stats.late).append("\n")
        sb.append("Посещаемость: ").append(stats.attendancePercent).append("%\n")
        if (records.isNotEmpty()) {
            sb.append("\nОтметки:\n")
            records.forEach {
                sb.append("${it.lessonDate} | ${it.subject} | ${it.lastName} ${it.firstName} (${it.groupName}) | ${it.status}\n")
            }
        }
        return sb.toString()
    }
}
