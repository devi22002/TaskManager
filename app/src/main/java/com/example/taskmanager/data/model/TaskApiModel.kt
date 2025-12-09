package com.example.taskmanager.data.model

import com.google.gson.annotations.SerializedName

//this for remote storage
data class TaskApiModel(
    val judul: String,
    val deskripsi: String?,
    val deadline: String,
    @SerializedName("is_done")
    val is_done: Boolean,
    val prioritas: String,
    @SerializedName("matakuliah_id")
    val matakuliah_id: Int,
    val id: Long,
    @SerializedName("user_id")
    val user_id: Long,
    @SerializedName("created_at")
    val created_at: String,
    @SerializedName("updated_at")
    val updated_at: String,
    @SerializedName("matakuliah_nama")
    val matakuliah_nama: String?
)
