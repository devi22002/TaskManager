package com.example.taskmanager.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskmanager.data.db.TaskDbHelper
import com.example.taskmanager.data.mapper.TaskMapper
import com.example.taskmanager.data.model.TaskModel
import com.example.taskmanager.network.ApiService
import com.example.taskmanager.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Konstruktor dibuat private agar tidak bisa dibuat dari luar
class TaskRepository private constructor(context: Context) {

    private val dbHelper = TaskDbHelper(context.applicationContext)
    private val userRepository = UserRepository(context.applicationContext)

    private val api: ApiService = RetrofitClient.instance.create(ApiService::class.java)

    private val _tasks = MutableLiveData<List<TaskModel>>(emptyList())
    val tasks: LiveData<List<TaskModel>> = _tasks

    suspend fun syncTasksFromCloud() {
        withContext(Dispatchers.IO) {
            val userEmail = userRepository.getEmail()
            if (userEmail != null) {
                val res = api.getAllTasks(email = userEmail)

                if (res.isSuccessful) {
                    val apiList = res.body() ?: emptyList()
                    val localList = apiList.map { TaskMapper.fromApi(it) }

                    dbHelper.clearAllTasks()
                    localList.forEach { dbHelper.insertTask(it) }

                    _tasks.postValue(localList)
                }
            }
        }
    }

    suspend fun createTask(task: TaskModel): Long {
        return withContext(Dispatchers.IO) {
            val userId = userRepository.getId()
            if (userId == -1) return@withContext -1L

            val apiBody = TaskMapper.toApiBody(task, userId)
            val res = api.createTask(apiBody)

            if (res.isSuccessful) {
                val cloud = res.body()!!
                val local = TaskMapper.fromApi(cloud)
                dbHelper.insertTask(local)
                loadTasks()
                local.id
            } else {
                -1L
            }
        }
    }

    suspend fun loadTasks() {
        withContext(Dispatchers.IO) {
            val list = dbHelper.getAllTasks()
            _tasks.postValue(list)
        }
    }
    
    suspend fun clearAllTasksData() {
        withContext(Dispatchers.IO) {
            dbHelper.clearAllTasks()
            _tasks.postValue(emptyList())
        }
    }

    suspend fun updateTask(task: TaskModel) {
        withContext(Dispatchers.IO) {
            dbHelper.updateTask(task)

            try {
                api.updateTaskStatus(
                    task.id,
                    mapOf("is_done" to (task.status == 1))
                )
            } catch (_: Exception) {}

            loadTasks()
        }
    }

    suspend fun deleteTask(id: Long) {
        withContext(Dispatchers.IO) {
            dbHelper.deleteTask(id)
            loadTasks()
        }
    }

    // --- (BARU) BAGIAN UNTUK SINGLETON ---
    companion object {
        @Volatile
        private var INSTANCE: TaskRepository? = null

        fun getInstance(context: Context): TaskRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = TaskRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}
