package com.kienvo.jetweatherforecast.data.local

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// 1. Bảng lưu thành phố yêu thích
@Entity(tableName = "fav_table")
data class Favorite(
    @PrimaryKey
    @ColumnInfo(name = "city") val city: String,
    @ColumnInfo(name = "country") val country: String
)

// 2. Các lệnh thao tác với bảng Favorites
@Dao
interface FavoriteDao {
    @Query("SELECT * FROM fav_table")
    fun getFavorites(): Flow<List<Favorite>>

    @Query("SELECT * FROM fav_table WHERE city = :city")
    suspend fun getFavById(city: String): Favorite?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: Favorite)

    @Delete
    suspend fun deleteFavorite(favorite: Favorite)
}