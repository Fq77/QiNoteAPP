package com.qinoteapp.qinoteapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "bills")
data class BillEntity(
    @PrimaryKey val id: String,
    val type: String,
    val category: String,
    val title: String,
    val amount: Long,
    val note: String = "",
    val date: String,
    val time: String,
    val image: String? = null,
    val source: String = "manual",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long? = null
)
