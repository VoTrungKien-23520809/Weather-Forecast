package com.kienvo.jetweatherforecast.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// 1. Khai báo bảng lưu Đơn vị đo lường
@Entity(tableName = "settings_table")
data class Unit(
    @PrimaryKey
    @ColumnInfo(name = "unit")
    val unit: String,

    @ColumnInfo(name = "wind_unit")
    val windUnit: String = "m/s",

    @ColumnInfo(name = "time_format")
    val timeFormat: String = "24h",

    @ColumnInfo(name = "gemini_api_key")
    val geminiApiKey: String = ""
)

// 2. Các lệnh thao tác (DAO)
@Dao
interface UnitDao {
    @Query("SELECT * FROM settings_table")
    fun getUnits(): Flow<List<Unit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnit(unit: Unit)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUnit(unit: Unit)

    @Query("DELETE FROM settings_table")
    suspend fun deleteAllUnits()

    @Delete
    suspend fun deleteUnit(unit: Unit)
}