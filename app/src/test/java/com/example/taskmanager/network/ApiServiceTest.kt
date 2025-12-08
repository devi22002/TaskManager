package com.example.taskmanager.network

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.example.taskmanager.data.model.TaskApiModel
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var apiService: ApiService

    @Before
    fun setUp() {
        server = MockWebServer()
        apiService = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `getAllTasks should return a list of tasks`() = runBlocking {
        val taskList = listOf(
            TaskApiModel(1, "Task 1", "Description 1", "2024-05-20", false),
            TaskApiModel(2, "Task 2", "Description 2", "2024-05-21", true)
        )
        val response = MockResponse()
            .setBody(Gson().toJson(taskList))
            .setResponseCode(200)

        server.enqueue(response)

        val result = apiService.getAllTasks()

        assertThat(result.isSuccessful).isTrue()
        assertThat(result.body()).hasSize(2)
        assertThat(result.body()?.get(0)?.namaTugas).isEqualTo("Task 1")
    }

    @Test
    fun `createTask should return the created task`() = runBlocking {
        val newTask = TaskApiModel(3, "New Task", "New Description", "2024-05-22", false)
        val response = MockResponse()
            .setBody(Gson().toJson(newTask))
            .setResponseCode(201)

        server.enqueue(response)

        val result = apiService.createTask(mapOf(
            "namaTugas" to "New Task",
            "deskripsi" to "New Description",
            "tanggal" to "2024-05-22"
        ))

        assertThat(result.isSuccessful).isTrue()
        assertThat(result.body()?.namaTugas).isEqualTo("New Task")
    }

    @Test
    fun `updateTaskStatus should return the updated task`() = runBlocking {
        val updatedTask = TaskApiModel(1, "Task 1", "Description 1", "2024-05-20", true)
        val response = MockResponse()
            .setBody(Gson().toJson(updatedTask))
            .setResponseCode(200)

        server.enqueue(response)

        val result = apiService.updateTaskStatus(1, mapOf("status" to true))

        assertThat(result.isSuccessful).isTrue()
        assertThat(result.body()?.status).isTrue()
    }

    @Test
    fun `getTaskById should return a single task`() = runBlocking {
        val task = TaskApiModel(1, "Task 1", "Description 1", "2024-05-20", false)
        val response = MockResponse()
            .setBody(Gson().toJson(task))
            .setResponseCode(200)

        server.enqueue(response)

        val result = apiService.getTaskById(1)

        assertThat(result.isSuccessful).isTrue()
        assertThat(result.body()?.namaTugas).isEqualTo("Task 1")
    }

    @Test
    fun `getProfile should return user profile`() = runBlocking {
        val profile = mapOf("name" to "Umaru", "email" to "umaru@example.com")
        val response = MockResponse()
            .setBody(Gson().toJson(profile))
            .setResponseCode(200)

        server.enqueue(response)

        val result = apiService.getProfile()

        assertThat(result.isSuccessful).isTrue()
        assertThat(result.body()?.get("name")).isEqualTo("Umaru")
    }

    @Test
    fun `getMatakuliah should return a list of subjects`() = runBlocking {
        val matakuliah = listOf(mapOf("name" to "Mobile Programming"))
        val response = MockResponse()
            .setBody(Gson().toJson(matakuliah))
            .setResponseCode(200)

        server.enqueue(response)

        val result = apiService.getMatakuliah()

        assertThat(result.isSuccessful).isTrue()
        assertThat(result.body()?.get(0)?.get("name")).isEqualTo("Mobile Programming")
    }
}