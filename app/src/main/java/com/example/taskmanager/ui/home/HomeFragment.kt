package com.example.taskmanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.taskmanager.data.db.TaskDbHelper
import com.example.taskmanager.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // DB helper instance
        val db = TaskDbHelper(requireContext())

        // Ambil semua data task
        val tasks = db.getAllTasks()

        // Tampilkan ke TextView
        binding.tvWelcome.text = "Welcome to TaskManager!"
        binding.tvSub.text = "Total Task: ${tasks.size}"

        // Kalau mau menampilkan data detail
        var textDisplay = ""
        for (t in tasks) {
            textDisplay += "• ${t.title} - ${t.subject}\n"
        }
        binding.tvList.text = textDisplay

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
