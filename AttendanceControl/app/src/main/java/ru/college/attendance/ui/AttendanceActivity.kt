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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.college.attendance.R
import ru.college.attendance.data.AttendanceRepository
import ru.college.attendance.data.Student
import ru.college.attendance.data.StudentRepository
import ru.college.attendance.logic.DateTools

class AttendanceActivity : AppCompatActivity() {

    private val adapter = AttendanceAdapter()
    private var groups: List<String> = emptyList()
    private var currentStudents: List<Student> = emptyList()

    private lateinit var spGroup: Spinner
    private lateinit var etDate: EditText
    private lateinit var etSubject: EditText
    private lateinit var tvEmpty: TextView
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        spGroup = findViewById(R.id.spGroup)
        etDate = findViewById(R.id.etDate)
        etSubject = findViewById(R.id.etSubject)
        tvEmpty = findViewById(R.id.tvEmpty)
        btnSave = findViewById(R.id.btnSave)

        etDate.setText(DateTools.today())
        findViewById<RecyclerView>(R.id.rvMarks).apply {
            layoutManager = LinearLayoutManager(this@AttendanceActivity)
            adapter = this@AttendanceActivity.adapter
        }
        btnSave.setOnClickListener { save() }
        loadGroups()
    }

    private fun loadGroups() {
        groups = StudentRepository.getAll().map { it.groupName }.distinct().sorted()
        if (groups.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            spGroup.visibility = View.GONE
            btnSave.isEnabled = false
            return
        }
        spGroup.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, groups)
        spGroup.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                showStudents(groups[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun showStudents(group: String) {
        currentStudents = StudentRepository.getAll().filter { it.groupName == group }
        adapter.submit(currentStudents)
    }

    private fun save() {
        val date = etDate.text.toString().trim()
        val subject = etSubject.text.toString().trim()
        if (!DateTools.isValidIsoDate(date)) {
            etDate.error = "Введите дату в формате ГГГГ-ММ-ДД"
            return
        }
        if (subject.isEmpty()) {
            etSubject.error = "Укажите дисциплину или тему"
            return
        }
        val statuses = adapter.getStatuses()
        currentStudents.forEach { student ->
            val status = statuses[student.id] ?: return@forEach
            AttendanceRepository.saveMark(student.id, date, subject, status)
        }
        Toast.makeText(this, "Отметки сохранены: ${currentStudents.size}", Toast.LENGTH_SHORT).show()
    }
}
