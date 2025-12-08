package com.example.taskmanager.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.taskmanager.R
import com.example.taskmanager.data.repository.UserRepository
import com.example.taskmanager.databinding.ActivityMainBinding
import com.example.taskmanager.ui.home.HomeFragment
import com.example.taskmanager.ui.login.LoginFragment
import com.example.taskmanager.ui.profile.ProfileFragment
import com.example.taskmanager.ui.tasks.AddTaskFragment
import com.example.taskmanager.ui.tasks.TaskListFragment
import com.example.taskmanager.ui.calendar.CalendarFragment
import com.example.taskmanager.viewmodel.TaskViewModel
import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userRepository = UserRepository(this)

        if (userRepository.getEmail() == null) {
            openNoBackstack(LoginFragment())
            setBottomBarVisibility(View.GONE)
        } else {
            setupAppNavigation()
        }
    }

    fun onLoginSuccess() {
        setupAppNavigation()
    }

    private fun setupAppNavigation() {
        viewModel.sync()
        openNoBackstack(HomeFragment())
        setBottomBarVisibility(View.VISIBLE)

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { openNoBackstack(HomeFragment()); true }
                R.id.nav_tasks -> { openNoBackstack(TaskListFragment()); true }
                R.id.nav_calendar -> { openNoBackstack(CalendarFragment()); true }
                R.id.nav_profile -> { openNoBackstack(ProfileFragment()); true }
                else -> false
            }
        }

        binding.fabAdd.setOnClickListener {
            openWithBackstack(AddTaskFragment())
        }
    }

    fun setBottomBarVisibility(visibility: Int) {
        binding.bottomNav.visibility = visibility
        binding.fabAdd.visibility = visibility
    }

    private fun openNoBackstack(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }

    private fun openWithBackstack(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .addToBackStack(null)
            .commit()
    }
}
