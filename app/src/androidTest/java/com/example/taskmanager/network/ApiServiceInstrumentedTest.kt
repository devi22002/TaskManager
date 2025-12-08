package com.example.taskmanager.network

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ApiServiceInstrumentedTest {

    private lateinit var apiService: ApiService

    @Before
    fun setUp() {
        apiService = RetrofitClient.instance.create(ApiService::class.java)
    }

    @Test
    fun testGetAllTasks() = runBlocking {
        val result = apiService.getAllTasks(email = "umar22002@mail.unpad.ac.id")
        assertThat(result.isSuccessful).isTrue()
    }
}