package com.example.taskmanager.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentProfileBinding
import com.example.taskmanager.ui.MainActivity
import com.example.taskmanager.ui.login.LoginFragment
import com.example.taskmanager.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userEmail = viewModel.getUserEmail()
        binding.tvName.text = "User TaskManager"
        binding.tvEmail.text = userEmail

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }

        viewModel.logoutComplete.observe(viewLifecycleOwner) { isComplete ->
            if (isComplete) {
                (activity as? MainActivity)?.setBottomBarVisibility(View.GONE)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, LoginFragment())
                    .commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
