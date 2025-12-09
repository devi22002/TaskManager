package com.example.taskmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.repository.TaskRepository
import com.example.taskmanager.data.repository.UserRepository
import com.example.taskmanager.network.ApiService
import com.example.taskmanager.network.RetrofitClient
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(application)
    // (DIUBAH) Menggunakan instance Singleton yang sama dengan ViewModel lain
    private val taskRepository = TaskRepository.getInstance(application)
    private val apiService: ApiService = RetrofitClient.instance.create(ApiService::class.java)

    private val _profile = MutableLiveData<Map<String, Any>?>()
    val profile: LiveData<Map<String, Any>?> = _profile

    private val _logoutComplete = MutableLiveData<Boolean>()
    val logoutComplete: LiveData<Boolean> = _logoutComplete

    fun fetchProfile() {
        viewModelScope.launch {
            val userEmail = userRepository.getEmail()
            if (userEmail != null) {
                try {
                    val response = apiService.getProfile(userEmail)
                    if (response.isSuccessful) {
                        _profile.postValue(response.body())
                    } else {
                        _profile.postValue(null)
                    }
                } catch (e: Exception) {
                    _profile.postValue(null)
                }
            } else {
                _profile.postValue(null)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            taskRepository.clearAllTasksData() // Sekarang ini akan membersihkan data yang benar
            _logoutComplete.postValue(true)
        }
    }
}
