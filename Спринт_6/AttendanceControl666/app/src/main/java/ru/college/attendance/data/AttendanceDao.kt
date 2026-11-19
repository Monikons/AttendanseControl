package ru.college.attendance.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<AttendanceEntity>)

    @Query("SELECT * FROM attendance")
    suspend fun getAll(): List<AttendanceEntity>

    @Query("SELECT COUNT(*) FROM attendance")
    suspend fun count(): Int

    @Query("SELECT * FROM attendance WHERE lessonDate = :date AND subject = :subject")
    suspend fun getForLesson(date: String, subject: String): List<AttendanceEntity>

    /** История отметок вместе с данными студента (для статистики и фильтров). */
    @Query(
        "SELECT a.id AS id, a.studentId AS studentId, a.lessonDate AS lessonDate, " +
            "a.subject AS subject, a.status AS status, " +
            "s.lastName AS lastName, s.firstName AS firstName, s.groupName AS groupName " +
            "FROM attendance a INNER JOIN students s ON s.id = a.studentId " +
            "ORDER BY a.lessonDate DESC, s.lastName, s.firstName"
    )
    suspend fun getHistory(): List<AttendanceRecord>
}
