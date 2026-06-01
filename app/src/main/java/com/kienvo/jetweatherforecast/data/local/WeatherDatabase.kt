package com.kienvo.jetweatherforecast.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import com.kienvo.jetweatherforecast.data.local.Unit
import kotlinx.coroutines.flow.Flow

// 1. Khai báo Bảng dữ liệu (Entity)
@Entity(tableName = "search_history")
data class SearchHistory(
    @PrimaryKey val cityName: String,
    val timestamp: Long = System.currentTimeMillis()
)

// 2. Khai báo các câu lệnh thao tác (DAO)
@Dao
interface SearchHistoryDao {
    // Lấy 5 tìm kiếm gần nhất, sắp xếp theo thời gian mới nhất
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 5")
    fun getRecentSearches(): Flow<List<SearchHistory>>

    // Lưu vào lịch sử (Nếu trùng tên thành phố thì ghi đè để cập nhật thời gian)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(searchHistory: SearchHistory)
}

// 3. Khởi tạo Database
@Database(entities = [SearchHistory::class, Favorite::class, Unit::class], version = 5, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun unitDao(): UnitDao
}

