package com.example.taskmanager.data.model

import com.google.gson.annotations.SerializedName

data class CreateTaskRequest(
    val judul: String,
    val deskripsi: String,
    val prioritas: String = "Sedang", // Ditambahkan kembali
    val deadline: String,
    @SerializedName("matakuliah_id")
    val matakuliahId: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("is_done")
    val isDone: Boolean = false
)
