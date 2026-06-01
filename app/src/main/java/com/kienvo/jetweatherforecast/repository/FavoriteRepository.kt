package com.kienvo.jetweatherforecast.repository

import com.kienvo.jetweatherforecast.data.local.Favorite
import com.kienvo.jetweatherforecast.data.local.FavoriteDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteRepository @Inject constructor(private val favoriteDao: FavoriteDao) {

    fun getFavorites(): Flow<List<Favorite>> = favoriteDao.getFavorites()

    suspend fun insertFavorite(favorite: Favorite) {
        favoriteDao.insertFavorite(favorite)
    }

    suspend fun deleteFavorite(favorite: Favorite) {
        favoriteDao.deleteFavorite(favorite)
    }

    suspend fun getFavById(city: String): Favorite? {
        return favoriteDao.getFavById(city)
    }
}

