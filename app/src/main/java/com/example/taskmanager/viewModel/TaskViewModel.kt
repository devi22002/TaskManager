package com.example.taskmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.model.TaskModel
import com.example.taskmanager.data.repository.TaskRepository
import kotlinx.coroutines.launch


class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = TaskRepository(application)

    val tasks = repo.tasks

    fun loadTasks() {
        viewModelScope.launch {
            repo.loadTasks()
        }
    }

    fun sync() {
        viewModelScope.launch {
            repo.syncTasksFromCloud()
        }
    }

    fun addTask(task: TaskModel, onComplete: ((Long) -> Unit)? = null) {
        viewModelScope.launch {
            val id = repo.addTask(task)
            onComplete?.invoke(id)
        }
    }

    fun createTask(task: TaskModel) {
        viewModelScope.launch {
            repo.createTask(task)
        }
    }

    fun updateTask(task: TaskModel) {
        viewModelScope.launch {
            repo.updateTask(task)
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            repo.deleteTask(id)
        }
    }

}
