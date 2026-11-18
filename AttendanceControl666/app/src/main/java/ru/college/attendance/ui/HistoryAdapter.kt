package ru.college.attendance.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.college.attendance.R
import ru.college.attendance.data.AttendanceRecord

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.VH>() {

    private var items: List<AttendanceRecord> = emptyList()

    fun submit(list: List<AttendanceRecord>) {
        items = list
        notifyDataSetChanged()
    }

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvStudent: TextView = view.findViewById(R.id.tvStudent)
        val tvLesson: TextView = view.findViewById(R.id.tvLesson)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val r = items[position]
        holder.tvStudent.text = "${r.lastName} ${r.firstName} (${r.groupName})"
        holder.tvLesson.text = "${r.lessonDate} - ${r.subject}"
        holder.tvStatus.text = r.status
    }

    override fun getItemCount(): Int = items.size
}
