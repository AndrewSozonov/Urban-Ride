package com.andrewsozonov.urbanride.repository

import com.andrewsozonov.urbanride.database.Ride
import com.andrewsozonov.urbanride.database.RideDAO
import javax.inject.Inject


/**
 * Главный репозиторий приложения
 *
 * @param rideDAO интерфейс предоставляющий доступ к БД
 *
 * @author Андрей Созонов
 */
class MainRepository @Inject constructor(private val rideDAO: RideDAO) {

    /**
     * Добавляет поездку в БД
     *
     * @param ride модель данных поездки [Ride]
     */
    fun addRide(ride: Ride) = rideDAO.addRide(ride)


    /**
     * Удаляет поездку из БД
     *
     * @param ride модель данных поездки [Ride]
     */
    fun deleteRide(ride: Ride) = rideDAO.deleteRide(ride)


    /**
     * Получает список всех поездок из БД
     *
     * @return спиок поездок [Ride]
     */
    fun getAllRides() : List<Ride> {
        return rideDAO.getAllRides()
    }

}