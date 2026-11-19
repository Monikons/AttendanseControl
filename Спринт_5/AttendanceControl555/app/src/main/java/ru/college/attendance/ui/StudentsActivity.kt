package ru.college.attendance.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import ru.college.attendance.R
import ru.college.attendance.data.AppDatabase
import ru.college.attendance.data.StudentEntity
import ru.college.attendance.logic.StudentKey
import ru.college.attendance.logic.StudentSearch
import ru.college.attendance.logic.StudentValidator

class StudentsActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.get(this) }
    private lateinit var adapter: StudentAdapter

    private lateinit var tilLast: TextInputLayout
    private lateinit var tilFirst: TextInputLayout
    private lateinit var tilGroup: TextInputLayout
    private lateinit var etLast: TextInputEditText
    private lateinit var etFirst: TextInputEditText
    private lateinit var etGroup: TextInputEditText
    private lateinit var etSearch: TextInputEditText
    private lateinit var tvEmpty: TextView
    private lateinit var tvCount: TextView

    private var allStudents: List<StudentEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students)

        tilLast = findViewById(R.id.tilLastName)
        tilFirst = findViewById(R.id.tilFirstName)
        tilGroup = findViewById(R.id.tilGroup)
        etLast = findViewById(R.id.etLastName)
        etFirst = findViewById(R.id.etFirstName)
        etGroup = findViewById(R.id.etGroup)
        etSearch = findViewById(R.id.etSearch)
        tvEmpty = findViewById(R.id.tvEmpty)
        tvCount = findViewById(R.id.tvCount)

        adapter = StudentAdapter { student -> confirmDelete(student) }
        findViewById<RecyclerView>(R.id.rvStudents).apply {
            layoutManager = LinearLayoutManager(this@StudentsActivity)
            adapter = this@StudentsActivity.adapter
        }

        // Ошибка под полем пропадает, когда пользователь начинает исправлять ввод
        etLast.doAfterTextChanged { tilLast.error = null }
        etFirst.doAfterTextChanged { tilFirst.error = null }
        etGroup.doAfterTextChanged { tilGroup.error = null }
        etSearch.doAfterTextChanged { showList() }

        findViewById<Button>(R.id.btnAdd).setOnClickListener { addStudent() }
        refresh()
    }

    private fun addStudent() {
        val last = StudentValidator.normalize(etLast.text?.toString().orEmpty())
        val first = StudentValidator.normalize(etFirst.text?.toString().orEmpty())
        val group = StudentValidator.normalize(etGroup.text?.toString().orEmpty())

        tilLast.error = if (last.isEmpty()) "Введите фамилию" else null
        tilFirst.error = if (first.isEmpty()) "Введите имя" else null
        tilGroup.error = if (group.isEmpty()) "Введите группу" else null
        if (last.isEmpty() || first.isEmpty() || group.isEmpty()) return

        lifecycleScope.launch {
            val existing = db.studentDao().getAll()
                .map { StudentKey(it.lastName, it.firstName, it.groupName) }
            val error = StudentValidator.validate(last, first, group, existing)
            if (error != null) {
                tilLast.error = error
            } else {
                db.studentDao().insert(StudentEntity(lastName = last, firstName = first, groupName = group))
                etLast.setText("")
                etFirst.setText("")
                tilLast.error = null
                tilFirst.error = null
                Toast.makeText(this@StudentsActivity, "Студент добавлен", Toast.LENGTH_SHORT).show()
                refresh()
            }
        }
    }

    private fun confirmDelete(student: StudentEntity) {
        AlertDialog.Builder(this)
            .setTitle("Удалить студента?")
            .setMessage(
                "${student.lastName} ${student.firstName} (${student.groupName}). " +
                    "Его отметки посещаемости тоже будут удалены."
            )
            .setPositiveButton("Удалить") { _, _ ->
                lifecycleScope.launch {
                    db.studentDao().delete(student)
                    refresh()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun refresh() {
        lifecycleScope.launch {
            allStudents = db.studentDao().getAll()
            showList()
        }
    }

    private fun showList() {
        val shown = StudentSearch.filter(allStudents, etSearch.text?.toString().orEmpty())
        adapter.submit(shown)
        tvCount.text = if (allStudents.isEmpty()) "" else "Студентов: ${allStudents.size}, показано: ${shown.size}"
        tvEmpty.text = if (allStudents.isEmpty()) {
            "Список студентов пуст. Добавьте первого студента."
        } else {
            "Ничего не найдено."
        }
        tvEmpty.visibility = if (shown.isEmpty()) View.VISIBLE else View.GONE
    }
}
