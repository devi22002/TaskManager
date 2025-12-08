package com.example.taskmanager.data.mapper

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

        return TaskModel(
            id = api.id, // ← penting: cloud ID = local ID
            title = api.judul,
            description = api.deskripsi ?: "",
            subject = api.matakuliah_id.toString(),
            deadlineMillis = deadlineMillis,
            priority = when (api.prioritas.lowercase()) {
                "tinggi" -> 1
                else -> 0
            },
            status = if (api.is_done) 1 else 0
        )
    }

    fun toApiBody(task: TaskModel): Map<String, Any> {
        val instant = java.time.Instant.ofEpochMilli(task.deadlineMillis)
        val deadlineString = LocalDateTime.ofInstant(instant, ZoneOffset.UTC).format(formatter)

        return mapOf(
            "judul" to task.title,
            "deskripsi" to (task.description ?: ""),
            "prioritas" to if (task.priority == 1) "Tinggi" else "Sedang",
            "deadline" to deadlineString,
            "matakuliah_id" to (task.subject.toIntOrNull() ?: 0)
        )
    }
}
