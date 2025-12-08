package com.example.taskmanager.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.repository.UserRepository
import com.example.taskmanager.network.ApiService
import com.example.taskmanager.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(application)
    private val apiService: ApiService = RetrofitClient.instance.create(ApiService::class.java)

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val response = apiService.login(mapOf("email" to email, "password" to password))
            if (response.isSuccessful) {
                val data = response.body()?.get("data") as? Map<*, *>
                val userId = (data?.get("id") as? Double)?.toInt()
                if (userId != null) {
                    userRepository.saveUser(email, userId)
                    _loginResult.postValue(true)
                } else {
                    _loginResult.postValue(false)
                }
            } else {
                _loginResult.postValue(false)
            }
        }
    }
}