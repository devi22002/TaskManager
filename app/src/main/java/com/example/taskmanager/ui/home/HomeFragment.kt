package com.example.taskmanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager.R
import com.example.taskmanager.adapter.UpcomingAdapter
import com.example.taskmanager.data.model.TaskModel
import com.example.taskmanager.databinding.FragmentHomeBinding
import com.example.taskmanager.viewmodel.TaskViewModel
import kotlin.math.max

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var upcomingAdapter: UpcomingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUpcomingRecycler()

        viewModel.loadTasks()

        viewModel.firstName.observe(viewLifecycleOwner) { firstName ->
            binding.tvHello.text = "Halo, $firstName"
        }

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            updateHeader(tasks)
            updateProgress(tasks)
            updateUpcoming(tasks)
            updateNotifications(tasks)
        }
    }

    private fun onTaskClick(task: TaskModel) {
        val changed = task.copy(status = if (task.status == 1) 0 else 1)
        viewModel.updateTask(changed)
    }

    private fun setupUpcomingRecycler() {
        upcomingAdapter = UpcomingAdapter(emptyList()) { task ->
            onTaskClick(task)
        }
        binding.rvUpcomingTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUpcomingTasks.adapter = upcomingAdapter
    }

    private fun updateHeader(tasks: List<TaskModel>) {
        val totalPending = tasks.count { it.status == 0 }
        binding.tvSubtitle.text = "Kamu punya $totalPending tugas yang perlu diselesaikan"
    }

    private fun updateProgress(tasks: List<TaskModel>) {
        val total = tasks.size
        val done = tasks.count { it.status == 1 }
        val doing = tasks.count { it.status == 0 && daysLeft(it.deadlineMillis) > 3 }
        val near = tasks.count { it.status == 0 && daysLeft(it.deadlineMillis) <= 3 }

        val percent = if (total == 0) 0 else ((done * 100f) / total).toInt()

        binding.progressOverall.progress = percent
        binding.tvProgressPercent.text = "$percent%"
        binding.tvProgressText.text = "$done dari $total tugas selesai"

        binding.tvDoneCount.text = done.toString()
        binding.tvDoingCount.text = doing.toString()
        binding.tvPendingCount.text = near.toString()
    }

    private fun updateUpcoming(tasks: List<TaskModel>) {
        val upcoming = tasks.filter { it.status == 0 }.sortedBy { it.deadlineMillis }
        upcomingAdapter.updateList(upcoming)
    }

    private fun updateNotifications(tasks: List<TaskModel>) {
        val container: LinearLayout = binding.notificationContainer
        container.removeAllViews()

        val nearDeadline = tasks.filter { it.status == 0 && daysLeft(it.deadlineMillis) in 0..3 }

        if (nearDeadline.isEmpty()) {
            val emptyView = TextView(requireContext()).apply {
                text = "Tidak ada notifikasi."
                setPadding(8, 8, 8, 8)
                setTextColor(resources.getColor(android.R.color.darker_gray))
            }
            container.addView(emptyView)
            return
        }

        for (task in nearDeadline) {
            val itemView = layoutInflater.inflate(R.layout.item_notification, container, false)
            val title = itemView.findViewById<TextView>(R.id.tvNotifTitle)
            val body = itemView.findViewById<TextView>(R.id.tvNotifBody)
            val days = max(0, daysLeft(task.deadlineMillis))

            title.text = "Deadline Mendekati"
            body.text = "${task.title} ${task.subject} harus dikumpulkan dalam $days hari"

            container.addView(itemView)
        }
    }

    private fun daysLeft(deadlineMillis: Long): Int {
        val now = System.currentTimeMillis()
        val diff = deadlineMillis - now
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
