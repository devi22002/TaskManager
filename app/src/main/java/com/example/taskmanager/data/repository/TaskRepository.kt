package com.example.taskmanager.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskmanager.data.db.TaskDbHelper
import com.example.taskmanager.data.mapper.TaskMapper
import com.example.taskmanager.data.model.TaskApiModel
import com.example.taskmanager.data.model.TaskModel
import com.example.taskmanager.network.ApiService
import com.example.taskmanager.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepository(context: Context) {

    private val dbHelper = TaskDbHelper(context.applicationContext)

    // Retrofit REAL API
    private val api: ApiService = RetrofitClient.instance.create(ApiService::class.java)

    private val _tasks = MutableLiveData<List<TaskModel>>(emptyList())
    val tasks: LiveData<List<TaskModel>> = _tasks

    // 1) SYNC FROM CLOUD → LOCAL
    suspend fun syncTasksFromCloud() {
        withContext(Dispatchers.IO) {
            val res = api.getAllTasks()   // GET /tugas

            if (res.isSuccessful) {
                val apiList = res.body() ?: emptyList()

                val localList = apiList.map { TaskMapper.fromApi(it) }

                // clear old local data
                dbHelper.clearAllTasks()

                // save new local data
                localList.forEach { dbHelper.insertTask(it) }

                _tasks.postValue(localList)
            }
        }
    }

    // 2) CREATE LOCAL + PUSH CLOUD
    suspend fun createTask(task: TaskModel): Long {
        return withContext(Dispatchers.IO) {

            val apiBody = TaskMapper.toApiBody(task)
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

    // ================================
    // 3) LOAD LOCAL
    // ================================
    suspend fun loadTasks() {
        withContext(Dispatchers.IO) {
            val list = dbHelper.getAllTasks()
            _tasks.postValue(list)
        }
    }

    // ================================
    // 4) ADD LOCAL + PUSH CLOUD (optional)
    // ================================
    suspend fun addTask(task: TaskModel): Long {
        return withContext(Dispatchers.IO) {
            val id = dbHelper.insertTask(task)

            try {
                api.createTask(TaskMapper.toApiBody(task))
            } catch (_: Exception) {
            }

            loadTasks()
            id
        }
    }

    // ================================
    // 5) UPDATE LOCAL + PUSH CLOUD
    // ================================
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

    // 6) DELETE LOCAL
    suspend fun deleteTask(id: Long) {
        withContext(Dispatchers.IO) {
            dbHelper.deleteTask(id)
            loadTasks()
        }
    }
}
