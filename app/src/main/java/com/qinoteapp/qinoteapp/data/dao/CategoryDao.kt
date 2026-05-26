package com.qinoteapp.qinoteapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qinoteapp.qinoteapp.data.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY isBuiltin DESC, CASE WHEN isBuiltin = 1 THEN id END ASC, CASE WHEN isBuiltin = 0 THEN id END DESC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY isBuiltin DESC, CASE WHEN isBuiltin = 1 THEN id END ASC, CASE WHEN isBuiltin = 0 THEN id END DESC")
    fun getCategoriesByType(type: String): Flow<List<CategoryEntity>>

    @Query("DELETE FROM categories")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Update
    suspend fun update(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)
}
