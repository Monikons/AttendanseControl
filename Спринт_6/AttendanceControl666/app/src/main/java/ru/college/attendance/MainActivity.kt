package ru.college.attendance

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.college.attendance.data.AppDatabase
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.college.attendance.data.DemoData
import ru.college.attendance.ui.AttendanceActivity
import ru.college.attendance.ui.StatisticsActivity
import ru.college.attendance.ui.StudentsActivity

class MainActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.get(this) }
    private lateinit var tvSummary: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvSummary = findViewById(R.id.tvSummary)

        findViewById<Button>(R.id.btnStudents).setOnClickListener {
            startActivity(Intent(this, StudentsActivity::class.java))
        }
        findViewById<Button>(R.id.btnAttendance).setOnClickListener {
            startActivity(Intent(this, AttendanceActivity::class.java))
        }
        findViewById<Button>(R.id.btnStatistics).setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
        }

        findViewById<Button>(R.id.btnDemo).setOnClickListener {
            lifecycleScope.launch {
                val loaded = DemoData.load(db)
                val message = if (loaded) {
                    "Демо-данные загружены"
                } else {
                    "База не пуста: демо-данные не загружены"
                }
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                refreshSummary()
            }
        }
        findViewById<Button>(R.id.btnClear).setOnClickListener { confirmClear() }
    }

    override fun onResume() {
        super.onResume()
        refreshSummary()
    }

    /** Сводка на главном экране: сколько студентов и отметок в базе. */
    private fun refreshSummary() {
        lifecycleScope.launch {
            val students = db.studentDao().count()
            val marks = db.attendanceDao().count()
            tvSummary.text = "Студентов: $students   •   Отметок: $marks"
        }
    }

    private fun confirmClear() {
        AlertDialog.Builder(this)
            .setTitle("Очистить все данные?")
            .setMessage("Будут удалены все студенты и отметки. Действие нельзя отменить.")
            .setPositiveButton("Очистить") { _, _ ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) { db.clearAllTables() }
                    Toast.makeText(this@MainActivity, "Данные удалены", Toast.LENGTH_SHORT).show()
                    refreshSummary()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}
