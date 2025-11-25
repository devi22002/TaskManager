package com.example.taskmanager.data.model

data class TaskApiModel(
    val judul: String,
    val deskripsi: String?,
    val deadline: String,
    val is_done: Boolean,
    val prioritas: String,
    val matakuliah_id: Int,
    val id: Long,
    val user_id: Long,
    val created_at: String,
    val updated_at: String
)
