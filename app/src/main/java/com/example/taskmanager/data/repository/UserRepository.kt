package com.example.taskmanager.data.repository

import android.content.Context
import android.content.SharedPreferences

class UserRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUser(email: String, id: Int) {
        prefs.edit().apply {
            putString("email", email)
            putInt("id", id)
            apply()
        }
    }

    fun getEmail(): String? {
        return prefs.getString("email", null)
    }

    fun getId(): Int {
        return prefs.getInt("id", -1)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}