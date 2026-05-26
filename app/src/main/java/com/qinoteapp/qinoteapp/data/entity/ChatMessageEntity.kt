package com.qinoteapp.qinoteapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val role: String,
    val content: String,
    val imageUri: String? = null,
    val isStatus: Boolean = false,
    val isFailed: Boolean = false,
    val errorDetail: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
