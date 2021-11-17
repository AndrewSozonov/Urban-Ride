package com.andrewsozonov.urbanride.data.database

import androidx.room.*

/**
 * Интерфейс описывающий методы доступа к БД
 *
 * @author Андрей Созонов
 */
@Dao
interface RideDao {

    /**
     * Добавить поездку в БД
     *
     * @param rideDBModel модель данных поездки [RideDBModel]
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRide(rideDBModel: RideDBModel)

    /**
     * Удалить поездку из БД
     *
     * @param ride модель данных поездки [RideDBModel]
     */
    @Query("DELETE FROM riding_table WHERE id = :id")
    fun deleteRide(id: Int)


    /**
     * Получит список поездок из БД
     *
     * @return список моделей [RideDBModel]
     */
    @Query("SELECT * FROM riding_table")
    fun getAllRides(): List<RideDBModel>


    @Query("SELECT * FROM riding_table WHERE id = :id")
    fun getRideByID(id: Int): RideDBModel
}