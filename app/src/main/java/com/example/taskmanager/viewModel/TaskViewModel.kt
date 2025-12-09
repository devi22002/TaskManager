package com.example.taskmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.model.TaskModel
import com.example.taskmanager.data.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = TaskRepository.getInstance(application)
    private val profileViewModel = ProfileViewModel(application)

    val tasks: LiveData<List<TaskModel>> = repo.tasks

    private val _firstName = MutableLiveData<String>()
    val firstName: LiveData<String> = _firstName

    // (BARU) LiveData untuk mengontrol status loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchFirstName()
    }

    private fun fetchFirstName() {
        profileViewModel.fetchProfile()
        profileViewModel.profile.observeForever { profile ->
            val fullName = profile?.get("nama") as? String
            _firstName.postValue(fullName?.split(" ")?.firstOrNull() ?: "User")
        }
    }

    // (DIUBAH) Fungsi sync sekarang mengontrol status loading
    fun sync() = viewModelScope.launch {
        _isLoading.postValue(true) // Tampilkan loading
        repo.syncTasksFromCloud()
        _isLoading.postValue(false) // Sembunyikan loading
    }

    fun loadTasks() = viewModelScope.launch {
        repo.loadTasks()
    }

    fun createTask(t: TaskModel, onFinished: (id: Long) -> Unit) = viewModelScope.launch {
        val id = repo.createTask(t)
        onFinished(id)
    }

    fun updateTask(task: TaskModel) {
        viewModelScope.launch {
            repo.updateTask(task)
        }
    }
}
