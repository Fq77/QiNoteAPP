package com.qinoteapp.qinoteapp.data.repository

import com.qinoteapp.qinoteapp.data.dao.CategoryDao
import com.qinoteapp.qinoteapp.data.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    fun getCategoriesByType(type: String): Flow<List<CategoryEntity>> =
        categoryDao.getCategoriesByType(type)

    suspend fun addCategory(category: CategoryEntity): Result<Unit> = try {
        categoryDao.insert(category)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteAllCategories(): Result<Unit> = try {
        categoryDao.deleteAll()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateCategory(category: CategoryEntity): Result<Unit> = try {
        categoryDao.update(category)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteCategory(category: CategoryEntity): Result<Unit> = try {
        categoryDao.delete(category)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
