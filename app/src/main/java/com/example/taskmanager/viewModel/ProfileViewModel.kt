package com.example.taskmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(application)

    private val _logoutComplete = MutableLiveData<Boolean>()
    val logoutComplete: LiveData<Boolean> = _logoutComplete

    fun getUserEmail(): String? {
        return userRepository.getEmail()
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _logoutComplete.postValue(true)
        }
    }
}