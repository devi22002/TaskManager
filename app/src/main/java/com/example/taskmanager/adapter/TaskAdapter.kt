package com.example.taskmanager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.data.model.TaskModel
import com.example.taskmanager.databinding.ItemTaskBinding
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private var list: List<TaskModel> = emptyList(),
    private val onItemClick: (TaskModel) -> Unit = {}
) : RecyclerView.Adapter<TaskAdapter.VH>() {

    inner class VH(private val b: ItemTaskBinding) : RecyclerView.ViewHolder(b.root) {

        fun bind(t: TaskModel) {

            // --- Title & Description
            b.txtTitle.text = t.title
            b.txtDesc.text = t.description ?: ""

            // --- Deadline
            b.txtDeadline.text = "Deadline: ${formatDate(t.deadlineMillis)}"

            // --- Priority Chip
            b.chipPriority.visibility = if (t.priority == 1) View.VISIBLE else View.GONE

            // --- Status Chip (warna + teks)
            val d = daysLeft(t.deadlineMillis)

            when {
                t.status == 1 -> {  // selesai
                    b.chipStatus.text = "Selesai"
                    b.chipStatus.setTextColor(ContextCompat.getColor(b.root.context, android.R.color.white))
                    b.chipStatus.setBackgroundResource(R.drawable.bg_chip_green)
                    b.imgStatus.setImageResource(R.drawable.ic_done)
                }
                d <= 3 -> {  // deadline mepet
                    b.chipStatus.text = "Mendatang"
                    b.chipStatus.setTextColor(ContextCompat.getColor(b.root.context, android.R.color.white))
                    b.chipStatus.setBackgroundResource(R.drawable.bg_chip_red)
                    b.imgStatus.setImageResource(R.drawable.ic_clock)
                }
                else -> {    // aman
                    b.chipStatus.text = "On Progress"
                    b.chipStatus.setTextColor(ContextCompat.getColor(b.root.context, android.R.color.white))
                    b.chipStatus.setBackgroundResource(R.drawable.bg_chip_yellow)
                    b.imgStatus.setImageResource(R.drawable.ic_task)
                }
            }

            // --- Click listener
            b.root.setOnClickListener { onItemClick(t) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(list[position])

    override fun getItemCount(): Int = list.size

    fun updateList(newList: List<TaskModel>) {
        list = newList
        notifyDataSetChanged()
    }

    // --- Helpers -----------------------------------------

    private fun formatDate(millis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(millis))
    }

    fun daysLeft(deadlineMillis: Long): Int {
        val now = System.currentTimeMillis()
        val diff = deadlineMillis - now
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }
}
