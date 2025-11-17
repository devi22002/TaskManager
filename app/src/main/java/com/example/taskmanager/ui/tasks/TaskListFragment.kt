package com.example.taskmanager.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager.adapter.TaskAdapter
import com.example.taskmanager.data.model.TaskModel
import com.example.taskmanager.databinding.FragmentTaskListBinding
import com.example.taskmanager.viewmodel.TaskViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.core.widget.addTextChangedListener


class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val vm: TaskViewModel by viewModels()
    private lateinit var adapter: TaskAdapter

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
        adapter = TaskAdapter(emptyList()) { task -> onTaskClick(task) }
        binding.recyclerTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTasks.adapter = adapter

        selectedMatkul = "Semua"
        selectedStatus = "Semua"

        vm.loadTasks()

        vm.tasks.observe(viewLifecycleOwner) {
            applyFilter()
        }

        binding.inputSearch.addTextChangedListener {
            applyFilter()
        }

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


    private fun applyFilter() {
        val original = vm.tasks.value ?: emptyList()
        val search = binding.inputSearch.text.toString().trim().lowercase()

        var filtered = original

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

    private fun onTaskClick(task: TaskModel) {
        val changed = task.copy(status = if (task.status == 1) 0 else 1)
        vm.updateTask(changed)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
