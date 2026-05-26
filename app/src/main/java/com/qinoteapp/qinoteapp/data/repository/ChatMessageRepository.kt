package com.qinoteapp.qinoteapp.data.repository

import com.qinoteapp.qinoteapp.data.dao.ChatMessageDao
import com.qinoteapp.qinoteapp.data.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatMessageRepository @Inject constructor(
    private val chatMessageDao: ChatMessageDao
) {
    fun getAllMessages(): Flow<List<ChatMessageEntity>> = chatMessageDao.getAll()

    suspend fun addMessage(message: ChatMessageEntity): Result<Unit> = try {
        chatMessageDao.insert(message)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun addMessages(messages: List<ChatMessageEntity>): Result<Unit> = try {
        chatMessageDao.insertAll(messages)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteByIds(ids: List<String>): Result<Unit> = try {
        chatMessageDao.deleteByIds(ids)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteAllMessages(): Result<Unit> = try {
        chatMessageDao.deleteAll()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
