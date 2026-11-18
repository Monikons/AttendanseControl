package ru.college.attendance.ui

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import ru.college.attendance.R
import ru.college.attendance.data.AppDatabase
import ru.college.attendance.data.AttendanceRecord
import ru.college.attendance.data.StudentEntity
import ru.college.attendance.logic.DateTools
import ru.college.attendance.logic.RecordFilter
import ru.college.attendance.logic.StatsCalculator

class StatisticsActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.get(this) }
    private val adapter = HistoryAdapter()

    private var allRecords: List<AttendanceRecord> = emptyList()
    private var students: List<StudentEntity> = emptyList()
    private var groups: List<String> = emptyList()

    private lateinit var spGroup: Spinner
    private lateinit var spStudent: Spinner
    private lateinit var etFrom: EditText
    private lateinit var etTo: EditText
    private lateinit var tvSummary: TextView
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        spGroup = findViewById(R.id.spGroup)
        spStudent = findViewById(R.id.spStudent)
        etFrom = findViewById(R.id.etFrom)
        etTo = findViewById(R.id.etTo)
        tvSummary = findViewById(R.id.tvSummary)
        tvEmpty = findViewById(R.id.tvEmpty)

        findViewById<RecyclerView>(R.id.rvHistory).apply {
            layoutManager = LinearLayoutManager(this@StatisticsActivity)
            adapter = this@StatisticsActivity.adapter
        }
        findViewById<Button>(R.id.btnApply).setOnClickListener { applyFilters() }
        findViewById<Button>(R.id.btnReset).setOnClickListener { resetFilters() }

        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            allRecords = db.attendanceDao().getHistory()
            students = db.studentDao().getAll()
            groups = db.studentDao().getGroups()

            spGroup.adapter = ArrayAdapter(
                this@StatisticsActivity,
                android.R.layout.simple_spinner_dropdown_item,
                listOf(RecordFilter.ALL_GROUPS) + groups
            )
            spStudent.adapter = ArrayAdapter(
                this@StatisticsActivity,
                android.R.layout.simple_spinner_dropdown_item,
                listOf(RecordFilter.ALL_STUDENTS) +
                    students.map { "${it.lastName} ${it.firstName} (${it.groupName})" }
            )
            applyFilters()
        }
    }

    private fun applyFilters() {
        val from = etFrom.text.toString().trim()
        val to = etTo.text.toString().trim()
        if (from.isNotEmpty() && !DateTools.isValidIsoDate(from)) {
            etFrom.error = "Формат ГГГГ-ММ-ДД"
            return
        }
        if (to.isNotEmpty() && !DateTools.isValidIsoDate(to)) {
            etTo.error = "Формат ГГГГ-ММ-ДД"
            return
        }
        if (from.isNotEmpty() && to.isNotEmpty() && from > to) {
            etTo.error = "Дата «По» раньше даты «С»"
            return
        }

        val group = if (spGroup.selectedItemPosition > 0) groups[spGroup.selectedItemPosition - 1] else null
        val studentId =
            if (spStudent.selectedItemPosition > 0) students[spStudent.selectedItemPosition - 1].id else null

        val filtered = RecordFilter.apply(allRecords, group, studentId, from, to)
        val stats = StatsCalculator.calculate(filtered)

        tvSummary.text = "Занятий: ${stats.total}  |  Присутствий: ${stats.present}  |  " +
            "Пропусков: ${stats.absent}  |  Опозданий: ${stats.late}"
        adapter.submit(filtered)
        tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun resetFilters() {
        spGroup.setSelection(0)
        spStudent.setSelection(0)
        etFrom.text.clear()
        etTo.text.clear()
        etFrom.error = null
        etTo.error = null
        applyFilters()
    }
}
