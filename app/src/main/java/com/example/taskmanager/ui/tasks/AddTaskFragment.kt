package com.example.taskmanager.ui.tasks

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.taskmanager.data.model.TaskModel
import com.example.taskmanager.databinding.DialogAddTaskBinding
import com.example.taskmanager.viewmodel.AddTaskViewModel
import java.util.Calendar

class AddTaskFragment : Fragment() {

    private var _binding: DialogAddTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddTaskViewModel by viewModels()

    private var selectedMatakuliahId: Int = -1
    private var selectedDeadlineMillis: Long = -1L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchMatakuliah()

        viewModel.matakuliahList.observe(viewLifecycleOwner) { matakuliahList ->
            val matakuliahNames = matakuliahList.map { it["nama"] as String }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, matakuliahNames)

            binding.dropdownMatkul.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Pilih Mata Kuliah")
                    .setAdapter(adapter) { _, pos ->
                        binding.txtSelectedMatkul.text = matakuliahNames[pos]
                        val idAsDouble = matakuliahList[pos]["id"] as? Double
                        selectedMatakuliahId = idAsDouble?.toInt() ?: -1
                    }
                    .show()
            }
        }

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

            if (title.isEmpty()) {
                binding.inputTitle.error = "Judul harus diisi"
                return@setOnClickListener
            }

            if (desc.isEmpty()) {
                binding.inputDescription.error = "Deskripsi harus diisi"
                return@setOnClickListener
            }

            if (selectedMatakuliahId == -1) {
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
                subject = selectedMatakuliahId.toString(),
                deadlineMillis = selectedDeadlineMillis,
                priority = 0,
                status = 0
            )

            viewModel.createTask(t)
        }

        viewModel.taskCreated.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "Tugas berhasil dibuat!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}