package com.example.taskmanager.ui.tasks

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.taskmanager.data.model.TaskModel
import com.example.taskmanager.databinding.DialogAddTaskBinding
import com.example.taskmanager.viewmodel.TaskViewModel
import java.util.Calendar

class AddTaskFragment : Fragment() {

    private var _binding: DialogAddTaskBinding? = null
    private val binding get() = _binding!!

    private val vm: TaskViewModel by viewModels(ownerProducer = { requireActivity() })
    private val listMatkul = listOf(
        "Pemrograman Mobile", "Sistem Basis Data", "Kalkulus", "Algoritma Struktur Data", "Jaringan Komputer"
    )

    private var selectedDeadlineMillis: Long = -1L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Dropdown open dialog
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listMatkul)
        binding.dropdownMatkul.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Pilih Mata Kuliah")
                .setAdapter(adapter) { _, pos ->
                    binding.txtSelectedMatkul.text = listMatkul[pos]
                }
                .show()
        }

        // date picker
        binding.pickDeadline.setOnClickListener {
            val cal = Calendar.getInstance()
            val dpd = DatePickerDialog(requireContext(),
                { _, y, m, d ->
                    val c = Calendar.getInstance()
                    c.set(y, m, d, 23, 59, 59)
                    selectedDeadlineMillis = c.timeInMillis
                    binding.txtDeadline.text = "$d/${m + 1}/$y"
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
            )
            dpd.show()
        }

        binding.btnCreateTask.setOnClickListener {
            val title = binding.inputTitle.text.toString().trim()
            val desc = binding.inputDescription.text.toString().trim()
            val subject = binding.txtSelectedMatkul.text.toString().takeIf { it != "Pilih Mata Kuliah" }
            // validate
            if (title.isEmpty()) {
                binding.inputTitle.error = "Judul harus diisi"
                return@setOnClickListener
            }

            if (desc.isEmpty()) {
                binding.inputDescription.error = "Deskripsi harus diisi"
                return@setOnClickListener
            }

            if (subject.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Pilih mata kuliah", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedDeadlineMillis == -1L) {
                Toast.makeText(requireContext(), "Deadline harus dipilih", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val t = TaskModel(
                title = title,
                description = desc,
                subject = subject,
                deadlineMillis = selectedDeadlineMillis,
                priority = 0,
                status = 0
            )

            vm.addTask(t) { id ->
                Toast.makeText(requireContext(), "Tugas tersimpan", Toast.LENGTH_SHORT).show()
                // kembali ke daftar tugas
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
