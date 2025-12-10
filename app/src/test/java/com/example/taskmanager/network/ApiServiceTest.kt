package com.example.taskmanager.network

import com.example.taskmanager.data.model.CreateTaskRequest
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
    fun `getAllTasks should return a list of tasks`() {
        runBlocking {
            val taskList = listOf(
                TaskApiModel("Task 1", "Description 1", "2024-05-20", false, "Tinggi", 1, 1, 1, "2024-01-01", "2024-01-01", "Web"),
                TaskApiModel("Task 2", "Description 2", "2024-05-21", true, "Rendah", 1, 2, 1, "2024-01-01", "2024-01-01", "Web")
            )
            val response = MockResponse()
                .setBody(Gson().toJson(taskList))
                .setResponseCode(200)

            server.enqueue(response)

            val result = apiService.getAllTasks("test@example.com")

            assertThat(result.isSuccessful).isTrue()
            assertThat(result.body()).hasSize(2)
            assertThat(result.body()?.get(0)?.judul).isEqualTo("Task 1")
        }
    }

    @Test
    fun `createTask should return the created task`() {
        runBlocking {
            val newTaskRequest = CreateTaskRequest("New Task", "New Description", "Sedang", "2024-05-22", 1, 1, false)
            val createdTask = TaskApiModel("New Task", "New Description", "2024-05-22", false, "Sedang", 1, 3, 1, "2024-01-01", "2024-01-01", "Web")
            val response = MockResponse()
                .setBody(Gson().toJson(createdTask))
                .setResponseCode(201)

            server.enqueue(response)

            val result = apiService.createTask(newTaskRequest)

            assertThat(result.isSuccessful).isTrue()
            assertThat(result.body()?.judul).isEqualTo("New Task")
        }
    }

    @Test
    fun `updateTaskStatus should return the updated task`() {
        runBlocking {
            val updatedTask = TaskApiModel("Task 1", "Description 1", "2024-05-20", true, "Tinggi", 1, 1, 1, "2024-01-01", "2024-01-01", "Web")
            val response = MockResponse()
                .setBody(Gson().toJson(updatedTask))
                .setResponseCode(200)

            server.enqueue(response)

            val result = apiService.updateTaskStatus(1, mapOf("is_done" to true))

            assertThat(result.isSuccessful).isTrue()
            assertThat(result.body()?.is_done).isTrue()
        }
    }

    @Test
    fun `getTaskById should return a single task`() {
        runBlocking {
            val task = TaskApiModel("Task 1", "Description 1", "2024-05-20", false, "Tinggi", 1, 1, 1, "2024-01-01", "2024-01-01", "Web")
            val response = MockResponse()
                .setBody(Gson().toJson(task))
                .setResponseCode(200)

            server.enqueue(response)

            val result = apiService.getTaskById(1)

            assertThat(result.isSuccessful).isTrue()
            assertThat(result.body()?.judul).isEqualTo("Task 1")
        }
    }

    @Test
    fun `getProfile should return user profile`() {
        runBlocking {
            val profile = mapOf("name" to "Umaru", "email" to "umaru@example.com")
            val response = MockResponse()
                .setBody(Gson().toJson(profile))
                .setResponseCode(200)

            server.enqueue(response)

            val result = apiService.getProfile("umaru@example.com")

            assertThat(result.isSuccessful).isTrue()
            assertThat(result.body()?.get("name")).isEqualTo("Umaru")
        }
    }

    @Test
    fun `getMatakuliah should return a list of subjects`() {
        runBlocking {
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
}