package com.example.taskmanager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.data.model.TaskModel
import com.example.taskmanager.databinding.ItemNotificationBinding
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(
    private var list: List<TaskModel>
) : RecyclerView.Adapter<NotificationAdapter.VH>() {

    inner class VH(private val b: ItemNotificationBinding)
        : RecyclerView.ViewHolder(b.root) {

        fun bind(t: TaskModel) {
            b.tvNotifTitle.text = "Deadline Mendekati"
            b.tvNotifBody.text = "${t.title} harus dikumpulkan dalam ${daysLeft(t.deadlineMillis)} hari"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(list[position])

    override fun getItemCount() = list.size

    fun updateList(newList: List<TaskModel>) {
        list = newList
        notifyDataSetChanged()
    }

    private fun daysLeft(d: Long): Int {
        val now = System.currentTimeMillis()
        val diff = d - now
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }
}
