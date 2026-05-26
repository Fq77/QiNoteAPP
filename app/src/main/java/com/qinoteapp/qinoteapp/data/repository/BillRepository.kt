package com.qinoteapp.qinoteapp.data.repository

import com.qinoteapp.qinoteapp.data.dao.BillDao
import com.qinoteapp.qinoteapp.data.entity.BillEntity
import com.qinoteapp.qinoteapp.util.AmountUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillRepository @Inject constructor(
    private val billDao: BillDao
) {
    fun getAllBills(): Flow<List<BillEntity>> = billDao.getAll()

    suspend fun getPagedBills(limit: Int, offset: Int): List<BillEntity> = billDao.getPaged(limit, offset)

    suspend fun getBillCount(): Int = billDao.getCount()

    suspend fun getById(billId: String): BillEntity? = billDao.getById(billId)

    suspend fun findDuplicate(title: String, amount: Long, type: String, date: String, time: String): BillEntity? =
        billDao.findDuplicate(title, amount, type, date, time)

    fun getBillsByDate(dateStr: String): Flow<List<BillEntity>> = billDao.getByDate(dateStr)

    fun getBillsByDateRange(start: String, end: String): Flow<List<BillEntity>> =
        billDao.getByDateRange(start, end)

    suspend fun getBillsByDateRangeSuspend(start: String, end: String): List<BillEntity> =
        billDao.getByDateRangeSuspend(start, end)

    fun getDayExpense(dateStr: String): Flow<Long> = billDao.getDayExpense(dateStr)

    fun getDayIncome(dateStr: String): Flow<Long> = billDao.getDayIncome(dateStr)

    fun getMonthExpense(monthPrefix: String): Flow<Long> = billDao.getMonthExpense(monthPrefix)

    fun getMonthIncome(monthPrefix: String): Flow<Long> = billDao.getMonthIncome(monthPrefix)

    suspend fun addBill(bill: BillEntity): Result<Unit> = try {
        billDao.insert(bill)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateBill(bill: BillEntity): Result<Unit> = try {
        billDao.update(bill)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteBill(bill: BillEntity): Result<Unit> = try {
        billDao.delete(bill)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteAllBills(): Result<Unit> = try {
        billDao.deleteAll()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun exportBillsToJson(bills: List<BillEntity>): Result<String> = try {
        val json = kotlinx.serialization.json.Json { prettyPrint = true }
        val result = json.encodeToString(kotlinx.serialization.builtins.ListSerializer(BillEntity.serializer()), bills)
        Result.success(result)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun importBillsFromJson(jsonString: String): Result<List<BillEntity>> = try {
        val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
        val jsonArray = json.parseToJsonElement(jsonString).jsonArray

        val bills = jsonArray.map { element ->
            val obj = element.jsonObject
            val amountPrim = obj["amount"]?.jsonPrimitive
            val amountCents = if (amountPrim != null) {
                val intVal = amountPrim.intOrNull
                if (intVal != null) {
                    intVal.toLong()
                } else {
                    val yuan = amountPrim.doubleOrNull ?: amountPrim.content.toDoubleOrNull() ?: 0.0
                    AmountUtils.yuanToCents(yuan)
                }
            } else 0L

            val convertedObj = JsonObject(obj + ("amount" to JsonPrimitive(amountCents)))
            json.decodeFromJsonElement(BillEntity.serializer(), convertedObj)
        }
        billDao.insertAll(bills)
        Result.success(bills)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
