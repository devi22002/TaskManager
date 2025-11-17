package com.example.taskmanager.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.taskmanager.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Set profile data
        binding.tvName.text = "Aulia Rahman"
        binding.tvNpm.text = "NPM : 140810220091"
        binding.tvProdi.text = "Prodi : Teknik Informatika"
        binding.tvEmail.text = "aulia220031@mail.unpad.ac.id"

        // Logout action
        binding.btnLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
