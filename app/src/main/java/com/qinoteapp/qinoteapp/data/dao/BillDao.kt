package com.qinoteapp.qinoteapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.qinoteapp.qinoteapp.data.entity.BillEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {

    @Query("SELECT * FROM bills ORDER BY date DESC, time DESC")
    fun getAll(): Flow<List<BillEntity>>

    @Query("SELECT * FROM bills ORDER BY date DESC, time DESC LIMIT :limit OFFSET :offset")
    suspend fun getPaged(limit: Int, offset: Int): List<BillEntity>

    @Query("SELECT COUNT(*) FROM bills")
    suspend fun getCount(): Int

    @Query("SELECT * FROM bills WHERE date = :dateStr ORDER BY time DESC")
    fun getByDate(dateStr: String): Flow<List<BillEntity>>

    @Query("SELECT * FROM bills WHERE date BETWEEN :startStr AND :endStr ORDER BY date DESC, time DESC")
    fun getByDateRange(startStr: String, endStr: String): Flow<List<BillEntity>>

    @Query("SELECT * FROM bills WHERE date BETWEEN :startStr AND :endStr ORDER BY date DESC, time DESC")
    suspend fun getByDateRangeSuspend(startStr: String, endStr: String): List<BillEntity>

    @Query("DELETE FROM bills")
    suspend fun deleteAll()

    @Query("SELECT COALESCE(SUM(amount), 0) FROM bills WHERE type = 'expense' AND date = :dateStr")
    fun getDayExpense(dateStr: String): Flow<Long>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM bills WHERE type = 'income' AND date = :dateStr")
    fun getDayIncome(dateStr: String): Flow<Long>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM bills WHERE type = 'expense' AND date LIKE :monthPrefix || '%'")
    fun getMonthExpense(monthPrefix: String): Flow<Long>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM bills WHERE type = 'income' AND date LIKE :monthPrefix || '%'")
    fun getMonthIncome(monthPrefix: String): Flow<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bill: BillEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bills: List<BillEntity>)

    @Update
    suspend fun update(bill: BillEntity)

    @Delete
    suspend fun delete(bill: BillEntity)

    @Query("SELECT * FROM bills WHERE id = :billId")
    suspend fun getById(billId: String): BillEntity?

    @Query("SELECT * FROM bills WHERE title = :title AND amount = :amount AND type = :type AND date = :date AND time = :time LIMIT 1")
    suspend fun findDuplicate(title: String, amount: Long, type: String, date: String, time: String): BillEntity?
}
