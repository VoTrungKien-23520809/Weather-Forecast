package com.kienvo.jetweatherforecast.repository

import com.kienvo.jetweatherforecast.data.local.UnitDao
import kotlinx.coroutines.flow.Flow
import com.kienvo.jetweatherforecast.data.local.Unit
import javax.inject.Inject

class SettingsRepository @Inject constructor(private val unitDao: UnitDao) {
    fun getUnits(): Flow<List<Unit>> = unitDao.getUnits()
    suspend fun insertUnit(unit: Unit) = unitDao.insertUnit(unit)
    suspend fun updateUnit(unit: Unit) = unitDao.updateUnit(unit)
    suspend fun deleteAllUnits() = unitDao.deleteAllUnits()
    suspend fun deleteUnit(unit: Unit) = unitDao.deleteUnit(unit)
}