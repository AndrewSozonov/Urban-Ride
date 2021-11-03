package com.andrewsozonov.urbanride.database

import androidx.room.*

/**
 * Интерфейс описывающий методы доступа к БД
 *
 * @author Андрей Созонов
 */
@Dao
interface RideDAO {

    /**
     * Добавить поездку в БД
     *
     * @param ride модель данных поездки [Ride]
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRide(ride: Ride)

    /**
     * Удалить поездку из БД
     *
     * @param ride модель данных поездки [Ride]
     */
    @Delete
    fun deleteRide(ride: Ride)


    /**
     * Получит список поездок из БД
     *
     * @return список моделей [Ride]
     */
    @Query("SELECT * FROM riding_table")
    fun getAllRides(): List<Ride>
}