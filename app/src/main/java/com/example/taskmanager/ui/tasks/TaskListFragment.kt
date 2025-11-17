package com.example.taskmanager.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager.adapter.TaskAdapter
import com.example.taskmanager.data.db.TaskDbHelper
import com.example.taskmanager.data.model.TaskModel
import com.example.taskmanager.databinding.FragmentTaskListBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.core.widget.addTextChangedListener

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TaskAdapter
    private lateinit var db: TaskDbHelper

    private var listAllTasks: List<TaskModel> = emptyList()

    private var selectedMatkul: String? = null
    private var selectedStatus: String? = null

    private val listMatkul = listOf(
        "Semua",
        "Pemrograman Mobile",
        "Sistem Basis Data",
        "Kalkulus",
        "Algoritma Struktur Data",
        "Jaringan Komputer"
    )

    private val listStatus = listOf(
        "Semua",
        "Belum Selesai",
        "Selesai"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = TaskDbHelper(requireContext())
        adapter = TaskAdapter(emptyList()) { task -> onTaskClick(task) }

        binding.recyclerTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTasks.adapter = adapter

        selectedMatkul = "Semua"
        selectedStatus = "Semua"

        loadDataFromDb()

        // Search listener
        binding.inputSearch.addTextChangedListener {
            applyFilter()
        }

        // Dialog filter matkul
        binding.filterMatkul.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Pilih Mata Kuliah")
                .setItems(listMatkul.toTypedArray()) { _, index ->
                    selectedMatkul = listMatkul[index]
                    binding.filterMatkul.text = selectedMatkul
                    applyFilter()
                }
                .show()
        }

        // Dialog filter status
        binding.filterStatus.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Pilih Status")
                .setItems(listStatus.toTypedArray()) { _, index ->
                    selectedStatus = listStatus[index]
                    binding.filterStatus.text = selectedStatus
                    applyFilter()
                }
                .show()
        }
    }

    // ⬇ Ambil data dari TaskDbHelper
    private fun loadDataFromDb() {
        listAllTasks = db.getAllTasks()
        applyFilter()
    }

    // ⬇ Proses filter
    private fun applyFilter() {
        val search = binding.inputSearch.text.toString().trim().lowercase()

        var filtered = listAllTasks

        // Filter Search
        if (search.isNotEmpty()) {
            filtered = filtered.filter {
                it.title.lowercase().contains(search) ||
                        it.subject.lowercase().contains(search)
            }
        }

        // Filter Matkul
        selectedMatkul?.let { m ->
            if (m != "Semua") {
                filtered = filtered.filter { it.subject == m }
            }
        }

        // Filter Status
        selectedStatus?.let { s ->
            filtered = when (s) {
                "Selesai" -> filtered.filter { it.status == 1 }
                "Belum Selesai" -> filtered.filter { it.status == 0 }
                else -> filtered
            }
        }

        adapter.updateList(filtered)
    }

    // ⬇ Update status ketika item diklik
    private fun onTaskClick(task: TaskModel) {
        val changed = task.copy(status = if (task.status == 1) 0 else 1)
        db.updateTask(changed)
        loadDataFromDb() // refresh list
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
