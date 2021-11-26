package com.andrewsozonov.urbanride.presentation.history.adapter

/**
 * Интерфейс слушателя нажатий на карту и кнопку share в элементе списка экрана History
 *
 * @author Андрей Созонов
 */
interface IHistoryRecyclerListener {

    /**
     * Вызывается при клике на изображение с картой
     *
     * @param id id элемента
     */
    fun onMapClick(id: Int)

    /**
     * Вызывается при клике на кнопку Share
     *
     * @param position номер позиции элемента
     */
    fun onShareClick(position: Int)
}