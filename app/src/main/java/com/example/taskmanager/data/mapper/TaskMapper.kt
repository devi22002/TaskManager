package com.example.taskmanager.data.mapper

import com.example.taskmanager.data.model.CreateTaskRequest
import com.example.taskmanager.data.model.TaskModel
import com.example.taskmanager.data.model.TaskApiModel
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object TaskMapper {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    fun fromApi(api: TaskApiModel): TaskModel {
        val localDateTime = LocalDateTime.parse(api.deadline, formatter)
        val deadlineMillis = localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()

        val currentTimeMillis = System.currentTimeMillis()
        val threeDaysInMillis = 3 * 24 * 60 * 60 * 1000L

        val isHighPriority = (deadlineMillis - currentTimeMillis) <= threeDaysInMillis && deadlineMillis > currentTimeMillis

        return TaskModel(
            id = api.id,
            title = api.judul,
            description = api.deskripsi ?: "",
            subject = api.matakuliah_nama ?: "N/A",
            deadlineMillis = deadlineMillis,
            priority = if (isHighPriority) 1 else 0, 
            status = if (api.is_done) 1 else 0
        )
    }

    fun toApiBody(task: TaskModel, userId: Int): CreateTaskRequest {
        val instant = java.time.Instant.ofEpochMilli(task.deadlineMillis)
        val deadlineString = LocalDateTime.ofInstant(instant, ZoneOffset.UTC).format(formatter)

        return CreateTaskRequest(
            judul = task.title,
            deskripsi = task.description ?: "",
            deadline = deadlineString,
            matakuliahId = task.subject.toIntOrNull() ?: 0, 
            userId = userId,
            prioritas = "Sedang" // Eksplisit mengirim "Sedang"
        )
    }
}
