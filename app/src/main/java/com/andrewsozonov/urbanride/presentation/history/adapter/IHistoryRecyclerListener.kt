package com.andrewsozonov.urbanride.presentation.history.adapter


/**
 * Интерфейс слушателя нажатий на элемент списка экрана History
 *
 * @author Андрей Созонов
 */
interface IHistoryRecyclerListener {

    /**
     * Вызывается при клике на изображение с картой
     *
     * @param position номер позиции элемента
     */
    fun onMapClick(position: Int)

    /**
     * Вызывается при клике на кнопку Share
     *
     * @param position номер позиции элемента
     */
    fun onShareClick(position: Int)
}