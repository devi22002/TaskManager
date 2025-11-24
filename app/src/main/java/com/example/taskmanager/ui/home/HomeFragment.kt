package com.example.taskmanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager.R
import com.example.taskmanager.adapter.UpcomingAdapter
import com.example.taskmanager.data.db.TaskDbHelper
import com.example.taskmanager.data.model.TaskModel
import com.example.taskmanager.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

class HomeFragment : Fragment() {

    private lateinit var b: FragmentHomeBinding
    private lateinit var db: TaskDbHelper
    private lateinit var upcomingAdapter: UpcomingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentHomeBinding.inflate(inflater, container, false)
        db = TaskDbHelper(requireContext())

        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUpcomingRecycler()
        loadData()
    }

    //  SETUP UPCOMING LIST

    private fun setupUpcomingRecycler() {
        upcomingAdapter = UpcomingAdapter(emptyList())
        b.rvUpcomingTasks.layoutManager = LinearLayoutManager(requireContext())
        b.rvUpcomingTasks.adapter = upcomingAdapter
    }

    //  LOAD ALL DATA
    private fun loadData() {
        val tasks = db.getAllTasks()

        updateHeader(tasks)
        updateProgress(tasks)
        updateUpcoming(tasks)
        updateNotifications(tasks)
    }

    //  HEADER
    private fun updateHeader(tasks: List<TaskModel>) {
        val totalPending = tasks.count { it.status == 0 }
        b.tvSubtitle.text = "Kamu punya $totalPending tugas yang perlu diselesaikan"
    }

    //  PROGRESS
    private fun updateProgress(tasks: List<TaskModel>) {
        val total = tasks.size
        val done = tasks.count { it.status == 1 }
        val doing = tasks.count { it.status == 0 && daysLeft(it.deadlineMillis) > 3 }
        val near = tasks.count { it.status == 0 && daysLeft(it.deadlineMillis) <= 3 }

        val percent = if (total == 0) 0 else ((done * 100f) / total).toInt()

        b.progressOverall.progress = percent
        b.tvProgressPercent.text = "$percent%"
        b.tvProgressText.text = "$done dari $total tugas selesai"

        b.tvDoneCount.text = done.toString()
        b.tvDoingCount.text = doing.toString()
        b.tvPendingCount.text = near.toString()
    }

    //  UPCOMING TASKS
    private fun updateUpcoming(tasks: List<TaskModel>) {
        val upcoming = tasks
            .filter { it.status == 0 }
            .sortedBy { it.deadlineMillis }

        upcomingAdapter.updateList(upcoming)
    }

    private fun updateNotifications(tasks: List<TaskModel>) {
        val container: LinearLayout = b.notificationContainer
        container.removeAllViews()

        val nearDeadline = tasks.filter {
            it.status == 0 && daysLeft(it.deadlineMillis) in 0..3
        }

        // Tidak ada notifikasi
        if (nearDeadline.isEmpty()) {
            val emptyView = TextView(requireContext()).apply {
                text = "Tidak ada notifikasi."
                setPadding(8, 8, 8, 8)
                setTextColor(resources.getColor(R.color.white))
            }
            container.addView(emptyView)
            return
        }

        // Untuk setiap task → inflate item_notification.xml
        for (task in nearDeadline) {
            val itemView = layoutInflater.inflate(
                R.layout.item_notification,
                container,
                false
            )

            val title = itemView.findViewById<TextView>(R.id.tvNotifTitle)
            val body = itemView.findViewById<TextView>(R.id.tvNotifBody)

            val days = max(0, daysLeft(task.deadlineMillis))

            title.text = "Deadline Mendekati"
            body.text = "${task.title} ${task.subject} harus dikumpulkan dalam $days hari"

            container.addView(itemView)
        }
    }

    // --------------------------------------------------------
    //  UTIL
    // --------------------------------------------------------
    private fun daysLeft(deadlineMillis: Long): Int {
        val now = System.currentTimeMillis()
        val diff = deadlineMillis - now
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }

}
