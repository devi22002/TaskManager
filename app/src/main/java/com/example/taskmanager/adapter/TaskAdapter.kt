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

            // --- Label Prioritas (Hanya muncul jika prioritas=1 DAN status=0)
            b.chipPriority.visibility = if (t.priority == 1 && t.status == 0) View.VISIBLE else View.GONE

            if (t.status == 1) {  // TUGAS SELESAI
                b.chipStatus.text = "Selesai"
                b.chipStatus.setTextColor(ContextCompat.getColor(b.root.context, android.R.color.white))
                b.chipStatus.setBackgroundResource(R.drawable.bg_chip_green)
                b.imgStatus.setImageResource(R.drawable.ic_done)
            } else {    // TUGAS BELUM SELESAI
                b.chipStatus.text = "On Progress"
                b.chipStatus.setTextColor(ContextCompat.getColor(b.root.context, android.R.color.black))
                b.chipStatus.setBackgroundResource(R.drawable.bg_chip_normal)
                
                // --- (BARU) LOGIKA IKON --- 
                // Jika prioritas, ikon jam merah. Jika tidak, ikon tugas biasa.
                if (t.priority == 1) {
                    b.imgStatus.setImageResource(R.drawable.ic_clock)
                } else {
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

    private fun formatDate(millis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(millis))
    }
}
