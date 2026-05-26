package com.qinoteapp.qinoteapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.Callback
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.qinoteapp.qinoteapp.data.dao.BillDao
import com.qinoteapp.qinoteapp.data.dao.CategoryDao
import com.qinoteapp.qinoteapp.data.dao.ChatMessageDao
import com.qinoteapp.qinoteapp.data.entity.BillEntity
import com.qinoteapp.qinoteapp.data.entity.CategoryEntity
import com.qinoteapp.qinoteapp.data.entity.ChatMessageEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(
    entities = [BillEntity::class, CategoryEntity::class, ChatMessageEntity::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun billDao(): BillDao
    abstract fun categoryDao(): CategoryDao
    abstract fun chatMessageDao(): ChatMessageDao
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context
    ): AppDatabase {
        val builtinCategories = listOf(
            CategoryEntity(id = "food", name = "餐饮美食", icon = "restaurant", type = "expense", isBuiltin = true, sortOrder = 0),
            CategoryEntity(id = "transport", name = "交通出行", icon = "directions_car", type = "expense", isBuiltin = true, sortOrder = 1),
            CategoryEntity(id = "shopping", name = "购物消费", icon = "shopping_bag", type = "expense", isBuiltin = true, sortOrder = 2),
            CategoryEntity(id = "entertainment", name = "休闲娱乐", icon = "sports_esports", type = "expense", isBuiltin = true, sortOrder = 3),
            CategoryEntity(id = "living", name = "居住生活", icon = "home", type = "expense", isBuiltin = true, sortOrder = 4),
            CategoryEntity(id = "daily", name = "日常杂项", icon = "more_horiz", type = "expense", isBuiltin = true, sortOrder = 5),
            CategoryEntity(id = "health", name = "医疗健康", icon = "local_hospital", type = "expense", isBuiltin = true, sortOrder = 6),
            CategoryEntity(id = "education", name = "教育培训", icon = "school", type = "expense", isBuiltin = true, sortOrder = 7),
            CategoryEntity(id = "social", name = "人情社交", icon = "people", type = "expense", isBuiltin = true, sortOrder = 8),
            CategoryEntity(id = "pet", name = "宠物萌宠", icon = "pets", type = "expense", isBuiltin = true, sortOrder = 9),
            CategoryEntity(id = "invest_expense", name = "投资理财", icon = "trending_up", type = "expense", isBuiltin = true, sortOrder = 10),
            CategoryEntity(id = "digital", name = "数码电子", icon = "devices", type = "expense", isBuiltin = true, sortOrder = 11),
            CategoryEntity(id = "housing", name = "住房缴费", icon = "receipt_long", type = "expense", isBuiltin = true, sortOrder = 12),
            CategoryEntity(id = "salary", name = "工资收入", icon = "account_balance_wallet", type = "income", isBuiltin = true, sortOrder = 0),
            CategoryEntity(id = "freelance", name = "兼职收入", icon = "work", type = "income", isBuiltin = true, sortOrder = 1),
            CategoryEntity(id = "investment", name = "投资收益", icon = "show_chart", type = "income", isBuiltin = true, sortOrder = 2)
        )

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE categories ADD COLUMN icon TEXT NOT NULL DEFAULT ''")
                db.execSQL("UPDATE categories SET icon = 'restaurant' WHERE id = 'food'")
                db.execSQL("UPDATE categories SET icon = 'directions_car' WHERE id = 'transport'")
                db.execSQL("UPDATE categories SET icon = 'shopping_bag' WHERE id = 'shopping'")
                db.execSQL("UPDATE categories SET icon = 'sports_esports' WHERE id = 'entertainment'")
                db.execSQL("UPDATE categories SET icon = 'home' WHERE id = 'living'")
                db.execSQL("UPDATE categories SET icon = 'more_horiz' WHERE id = 'daily'")
                db.execSQL("UPDATE categories SET icon = 'local_hospital' WHERE id = 'health'")
                db.execSQL("UPDATE categories SET icon = 'school' WHERE id = 'education'")
                db.execSQL("UPDATE categories SET icon = 'people' WHERE id = 'social'")
                db.execSQL("UPDATE categories SET icon = 'pets' WHERE id = 'pet'")
                db.execSQL("UPDATE categories SET icon = 'trending_up' WHERE id = 'invest_expense'")
                db.execSQL("UPDATE categories SET icon = 'devices' WHERE id = 'digital'")
                db.execSQL("UPDATE categories SET icon = 'receipt_long' WHERE id = 'housing'")
                db.execSQL("UPDATE categories SET icon = 'account_balance_wallet' WHERE id = 'salary'")
                db.execSQL("UPDATE categories SET icon = 'work' WHERE id = 'freelance'")
                db.execSQL("UPDATE categories SET icon = 'show_chart' WHERE id = 'investment'")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE categories_new (
                        id TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        type TEXT NOT NULL,
                        isBuiltin INTEGER NOT NULL,
                        sortOrder INTEGER NOT NULL,
                        icon TEXT NOT NULL DEFAULT ''
                    )
                """.trimIndent())
                db.execSQL("""
                    INSERT INTO categories_new (id, name, type, isBuiltin, sortOrder, icon)
                    SELECT id, name, type, isBuiltin, sortOrder, '' FROM categories
                """.trimIndent())
                db.execSQL("DROP TABLE categories")
                db.execSQL("ALTER TABLE categories_new RENAME TO categories")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("UPDATE categories SET icon = 'restaurant' WHERE id = 'food'")
                db.execSQL("UPDATE categories SET icon = 'directions_car' WHERE id = 'transport'")
                db.execSQL("UPDATE categories SET icon = 'shopping_bag' WHERE id = 'shopping'")
                db.execSQL("UPDATE categories SET icon = 'sports_esports' WHERE id = 'entertainment'")
                db.execSQL("UPDATE categories SET icon = 'home' WHERE id = 'living'")
                db.execSQL("UPDATE categories SET icon = 'more_horiz' WHERE id = 'daily'")
                db.execSQL("UPDATE categories SET icon = 'local_hospital' WHERE id = 'health'")
                db.execSQL("UPDATE categories SET icon = 'school' WHERE id = 'education'")
                db.execSQL("UPDATE categories SET icon = 'people' WHERE id = 'social'")
                db.execSQL("UPDATE categories SET icon = 'pets' WHERE id = 'pet'")
                db.execSQL("UPDATE categories SET icon = 'trending_up' WHERE id = 'invest_expense'")
                db.execSQL("UPDATE categories SET icon = 'devices' WHERE id = 'digital'")
                db.execSQL("UPDATE categories SET icon = 'receipt_long' WHERE id = 'housing'")
                db.execSQL("UPDATE categories SET icon = 'account_balance_wallet' WHERE id = 'salary'")
                db.execSQL("UPDATE categories SET icon = 'work' WHERE id = 'freelance'")
                db.execSQL("UPDATE categories SET icon = 'show_chart' WHERE id = 'investment'")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE bills ADD COLUMN source TEXT NOT NULL DEFAULT 'manual'")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS chat_messages (
                        id TEXT NOT NULL PRIMARY KEY,
                        role TEXT NOT NULL,
                        content TEXT NOT NULL,
                        imageUri TEXT,
                        isStatus INTEGER NOT NULL,
                        isFailed INTEGER NOT NULL,
                        errorDetail TEXT,
                        timestamp INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE bills_new (
                        id TEXT NOT NULL PRIMARY KEY,
                        type TEXT NOT NULL,
                        category TEXT NOT NULL,
                        title TEXT NOT NULL,
                        amount INTEGER NOT NULL,
                        note TEXT NOT NULL,
                        date TEXT NOT NULL,
                        time TEXT NOT NULL,
                        image TEXT,
                        source TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER
                    )
                """.trimIndent())
                db.execSQL("""
                    INSERT INTO bills_new (id, type, category, title, amount, note, date, time, image, source, createdAt, updatedAt)
                    SELECT id, type, category, title, CAST(ROUND(amount * 100) AS INTEGER), note, date, time, image, source, createdAt, updatedAt FROM bills
                """.trimIndent())
                db.execSQL("DROP TABLE bills")
                db.execSQL("ALTER TABLE bills_new RENAME TO bills")
            }
        }

        return androidx.room.Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "qinote_database"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
            .addCallback(object : Callback() {
                override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onCreate(db)
                    builtinCategories.forEach { category ->
                        db.execSQL(
                            "INSERT OR IGNORE INTO categories (id, name, icon, type, isBuiltin, sortOrder) VALUES (?, ?, ?, ?, ?, ?)",
                            arrayOf(
                                category.id,
                                category.name,
                                category.icon,
                                category.type,
                                if (category.isBuiltin) 1 else 0,
                                category.sortOrder
                            )
                        )
                    }
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    builtinCategories.forEach { category ->
                        db.execSQL(
                            "UPDATE categories SET icon = ? WHERE id = ?",
                            arrayOf(category.icon, category.id)
                        )
                    }
                }
            })
            .build()
    }

    @Provides
    fun provideBillDao(database: AppDatabase): BillDao = database.billDao()

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideChatMessageDao(database: AppDatabase): ChatMessageDao = database.chatMessageDao()
}
