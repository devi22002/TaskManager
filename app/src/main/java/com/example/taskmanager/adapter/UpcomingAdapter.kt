package com.example.taskmanager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.data.model.TaskModel
import com.example.taskmanager.databinding.ItemTaskSmallBinding
import java.text.SimpleDateFormat
import java.util.*

class UpcomingAdapter(
    private var list: List<TaskModel>,
    private val onItemClick: (TaskModel) -> Unit // Ditambahkan
) : RecyclerView.Adapter<UpcomingAdapter.VH>() {

    inner class VH(private val b: ItemTaskSmallBinding)
        : RecyclerView.ViewHolder(b.root) {

        fun bind(t: TaskModel) {
            b.tvTitle.text = t.title
            b.tvSubject.text = t.subject
            b.tvDeadline.text = formatDate(t.deadlineMillis)
            
            // Click listener ditambahkan di sini
            b.root.setOnClickListener { onItemClick(t) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            ItemTaskSmallBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    fun updateList(newList: List<TaskModel>) {
        list = newList
        notifyDataSetChanged()
    }

    private fun formatDate(m: Long): String {
        val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
        return sdf.format(Date(m))
    }
}
