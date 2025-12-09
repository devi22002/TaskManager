package com.example.taskmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.model.TaskModel
import com.example.taskmanager.data.repository.TaskRepository
import com.example.taskmanager.network.ApiService
import com.example.taskmanager.network.RetrofitClient
import kotlinx.coroutines.launch

class AddTaskViewModel(application: Application) : AndroidViewModel(application) {

    // (DIUBAH) Menggunakan instance Singleton
    private val taskRepository = TaskRepository.getInstance(application)
    private val apiService: ApiService = RetrofitClient.instance.create(ApiService::class.java)

    private val _matakuliahList = MutableLiveData<List<Map<String, Any>>>()
    val matakuliahList: LiveData<List<Map<String, Any>>> = _matakuliahList

    private val _taskCreated = MutableLiveData<Boolean>()
    val taskCreated: LiveData<Boolean> = _taskCreated

    fun fetchMatakuliah() {
        viewModelScope.launch {
            val response = apiService.getMatakuliah()
            if (response.isSuccessful) {
                _matakuliahList.postValue(response.body() ?: emptyList())
            }
        }
    }

    fun createTask(task: TaskModel) {
        viewModelScope.launch {
            val result = taskRepository.createTask(task)
            _taskCreated.postValue(result != -1L)
        }
    }
}
