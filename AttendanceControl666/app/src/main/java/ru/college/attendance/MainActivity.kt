package ru.college.attendance

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.college.attendance.data.AppDatabase
import ru.college.attendance.data.DemoData
import ru.college.attendance.ui.AttendanceActivity
import ru.college.attendance.ui.StatisticsActivity
import ru.college.attendance.ui.StudentsActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                val loaded = DemoData.load(AppDatabase.get(this@MainActivity))
                val message = if (loaded) {
                    "Демо-данные загружены"
                } else {
                    "База не пуста: демо-данные не загружены"
                }
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
