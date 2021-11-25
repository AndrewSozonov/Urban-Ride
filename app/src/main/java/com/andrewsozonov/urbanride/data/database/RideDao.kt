package com.andrewsozonov.urbanride.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andrewsozonov.urbanride.models.data.RideDBModel

/**
 * Интерфейс описывающий методы доступа к таблице с историей поездок [RidingDatabase]
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
     * @param id id поездки [RideDBModel]
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