package ru.college.attendance.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import ru.college.attendance.R
import ru.college.attendance.data.AppDatabase
import ru.college.attendance.data.AttendanceEntity
import ru.college.attendance.data.AttendanceStatus
import ru.college.attendance.data.StudentEntity
import ru.college.attendance.logic.DateTools
import ru.college.attendance.logic.StudentValidator

class AttendanceActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.get(this) }
    private val adapter = AttendanceAdapter()
    private var groups: List<String> = emptyList()
    private var currentStudents: List<StudentEntity> = emptyList()

    private lateinit var spGroup: Spinner
    private lateinit var etDate: EditText
    private lateinit var etSubject: EditText
    private lateinit var tvEmpty: TextView
    private lateinit var tvHint: TextView
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        spGroup = findViewById(R.id.spGroup)
        etDate = findViewById(R.id.etDate)
        etSubject = findViewById(R.id.etSubject)
        tvEmpty = findViewById(R.id.tvEmpty)
        tvHint = findViewById(R.id.tvHint)
        btnSave = findViewById(R.id.btnSave)

        etDate.setText(DateTools.today())
        findViewById<RecyclerView>(R.id.rvMarks).apply {
            layoutManager = LinearLayoutManager(this@AttendanceActivity)
            adapter = this@AttendanceActivity.adapter
        }
        btnSave.setOnClickListener { save() }
        findViewById<Button>(R.id.btnAllPresent).setOnClickListener {
            adapter.setAll(AttendanceStatus.PRESENT)
        }
        // Если занятие уже отмечалось, ранее сохранённые статусы подставляются автоматически
        etDate.doAfterTextChanged { loadExisting() }
        etSubject.doAfterTextChanged { loadExisting() }
        loadGroups()
    }

    private fun loadGroups() {
        lifecycleScope.launch {
            groups = db.studentDao().getGroups()
            if (groups.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
                spGroup.visibility = View.GONE
                btnSave.isEnabled = false
                return@launch
            }
            spGroup.adapter = ArrayAdapter(
                this@AttendanceActivity, android.R.layout.simple_spinner_dropdown_item, groups
            )
            spGroup.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    showStudents(groups[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun showStudents(group: String) {
        lifecycleScope.launch {
            currentStudents = db.studentDao().getByGroup(group)
            adapter.submit(currentStudents)
            loadExisting()
        }
    }

    /** Подставляет статусы, сохранённые ранее для этого занятия (дата + дисциплина). */
    private fun loadExisting() {
        val date = etDate.text.toString().trim()
        val subject = StudentValidator.normalize(etSubject.text.toString())
        if (!DateTools.isValidIsoDate(date) || subject.isEmpty() || currentStudents.isEmpty()) {
            tvHint.visibility = View.GONE
            return
        }
        lifecycleScope.launch {
            val ids = currentStudents.map { it.id }.toSet()
            val marks = db.attendanceDao().getForLesson(date, subject)
                .filter { it.studentId in ids }
                .associate { it.studentId to it.status }
            if (marks.isEmpty()) {
                tvHint.visibility = View.GONE
            } else {
                adapter.applyMarks(marks)
                tvHint.text = "Для этого занятия уже есть отметки: они загружены, их можно изменить."
                tvHint.visibility = View.VISIBLE
            }
        }
    }

    private fun save() {
        val date = etDate.text.toString().trim()
        val subject = StudentValidator.normalize(etSubject.text.toString())
        if (!DateTools.isValidIsoDate(date)) {
            etDate.error = "Введите дату в формате ГГГГ-ММ-ДД"
            return
        }
        if (subject.isEmpty()) {
            etSubject.error = "Укажите дисциплину или тему"
            return
        }
        val statuses = adapter.getStatuses()
        val marks = currentStudents.mapNotNull { student ->
            statuses[student.id]?.let { status ->
                AttendanceEntity(
                    studentId = student.id,
                    lessonDate = date,
                    subject = subject,
                    status = status
                )
            }
        }
        lifecycleScope.launch {
            // Уникальный индекс (студент, дата, дисциплина) + REPLACE = одна итоговая отметка
            db.attendanceDao().upsertAll(marks)
            Toast.makeText(this@AttendanceActivity, "Отметки сохранены: ${marks.size}", Toast.LENGTH_SHORT).show()
        }
    }
}
