package com.example.taskmanager.network

import com.example.taskmanager.data.model.TaskApiModel
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // AUTH
    @POST("login")
    suspend fun login(
        @Body body: Map<String, String>
    ): Response<Map<String, Any>>

    @POST("register")
    suspend fun register(
        @Body body: Map<String, String>
    ): Response<Map<String, Any>>

    // PROFILE USER
    @GET("me")
    suspend fun getProfile(): Response<Map<String, Any>>

    // TUGAS / TASK
    @GET("tugas")
    suspend fun getAllTasks(
        @Header("email") email: String
    ): Response<List<TaskApiModel>>

    @POST("tugas")
    suspend fun createTask(
        @Body body: Map<String, Any>
    ): Response<TaskApiModel>

    @PATCH("tugas/{id}/status")
    suspend fun updateTaskStatus(
        @Path("id") id: Long,
        @Body body: Map<String, Boolean>
    ): Response<TaskApiModel>

    @GET("tugas/{id}")
    suspend fun getTaskById(
        @Path("id") id: Long
    ): Response<TaskApiModel>


    @GET("tugas/progres")
    suspend fun getProgress(): Response<Map<String, Any>>

    // MATAKULIAH
    @GET("matakuliah")
    suspend fun getMatakuliah(): Response<List<Map<String, Any>>>
}
