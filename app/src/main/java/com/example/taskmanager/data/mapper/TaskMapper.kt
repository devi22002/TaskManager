package com.example.taskmanager.data.mapper

import com.example.taskmanager.data.model.TaskModel
import com.example.taskmanager.data.model.TaskApiModel

object TaskMapper {

    fun fromApi(api: TaskApiModel): TaskModel {
        return TaskModel(
            id = api.id, // ← penting: cloud ID = local ID
            title = api.judul,
            description = api.deskripsi ?: "",
            subject = api.matakuliah_id.toString(),

            deadlineMillis = java.time.Instant.parse(api.deadline).toEpochMilli(),

            priority = when (api.prioritas.lowercase()) {
                "tinggi" -> 1
                else -> 0
            },

            status = if (api.is_done) 1 else 0
        )
    }

    fun toApiBody(task: TaskModel): Map<String, Any> {
        return mapOf(
            "judul" to task.title,
            "deskripsi" to (task.description ?: ""),
            "prioritas" to if (task.priority == 1) "Tinggi" else "Sedang",
            "deadline" to java.time.Instant.ofEpochMilli(task.deadlineMillis).toString(),
            "matakuliah_id" to (task.subject.toIntOrNull() ?: 0)
        )
    }
}
