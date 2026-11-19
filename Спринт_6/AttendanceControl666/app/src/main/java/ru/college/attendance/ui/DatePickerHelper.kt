package ru.college.attendance.ui

import android.app.DatePickerDialog
import android.content.Context
import java.util.Calendar
import java.util.Locale
import ru.college.attendance.logic.DateTools

/** Выбор даты через календарь; результат в формате ГГГГ-ММ-ДД. */
object DatePickerHelper {
    fun pick(context: Context, current: String, onPicked: (String) -> Unit) {
        val cal = Calendar.getInstance()
        if (DateTools.isValidIsoDate(current)) {
            val p = current.split("-")
            cal.set(p[0].toInt(), p[1].toInt() - 1, p[2].toInt())
        }
        DatePickerDialog(
            context,
            { _, year, month, day ->
                onPicked(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
