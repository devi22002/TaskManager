package com.example.taskmanager.data.model

data class TaskModel(
    val id: Long = 0,
    val title: String,
    val description: String?,
    val subject: String,
    val deadlineMillis: Long,
    var priority: Int = 0, // 0 = normal, 1 = high
    var status: Int = 0, // 0 = pending, 1 = done
)