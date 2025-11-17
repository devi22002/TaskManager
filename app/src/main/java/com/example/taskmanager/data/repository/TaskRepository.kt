package com.example.taskmanager.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskmanager.data.db.TaskDbHelper
import com.example.taskmanager.data.model.TaskModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepository(context: Context) {
    private val dbHelper = TaskDbHelper(context.applicationContext)
    private val _tasks = MutableLiveData<List<TaskModel>>(emptyList())
    val tasks: LiveData<List<TaskModel>> = _tasks

    suspend fun loadTasks() {
        withContext(Dispatchers.IO) {
            val list = dbHelper.getAllTasks()
            _tasks.postValue(list)
        }
    }

    suspend fun addTask(task: TaskModel): Long {
        return withContext(Dispatchers.IO) {
            val id = dbHelper.insertTask(task)
            loadTasks()
            id
        }
    }

    suspend fun updateTask(task: TaskModel) {
        withContext(Dispatchers.IO) {
            dbHelper.updateTask(task)
            loadTasks()
        }
    }

    suspend fun deleteTask(id: Long) {
        withContext(Dispatchers.IO) {
            dbHelper.deleteTask(id)
            loadTasks()
        }
    }
}
